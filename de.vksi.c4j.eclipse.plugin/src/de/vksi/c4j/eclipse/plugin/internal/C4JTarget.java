package de.vksi.c4j.eclipse.plugin.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

import de.vksi.c4j.eclipse.plugin.ui.ChooseMemberDialog;
import de.vksi.c4j.eclipse.plugin.ui.CreateMethodAction;
import de.vksi.c4j.eclipse.plugin.ui.CreateNewClassAction;
import de.vksi.c4j.eclipse.plugin.ui.MemberContentProvider;
import de.vksi.c4j.eclipse.plugin.ui.TreeActionElement;
import de.vksi.c4j.eclipse.plugin.util.requestor.AssosiatedMemberRequest;
import de.vksi.c4j.eclipse.plugin.util.requestor.AssosiatedMemberRequest.MemberType;
import de.vksi.c4j.eclipse.plugin.util.requestor.ContractRequestor;
import de.vksi.c4j.eclipse.plugin.util.requestor.Requestor;
import de.vksi.c4j.eclipse.plugin.util.requestor.TypeHierarchyRequestor;
import de.vksi.c4j.eclipse.plugin.wizards.CreateContractWizardRunner;
import de.vksi.c4j.eclipse.plugin.wizards.WizardRunner;

public class C4JTarget extends TypeFacade {

	protected C4JTarget(ICompilationUnit compilationUnit) {
		super(compilationUnit);
	}

	@Override
	protected Collection<IMethod> getAssosiatedMethodsFromClasses(IMethod method, Collection<IType> classes) {
		List<IMethod> matchedMethods = new ArrayList<IMethod>();
		for (IType type : classes) {
			TypeFacade contract = TypeFacade.createFacade(type.getCompilationUnit());
			if (contract.hasMethod(method)) {
				matchedMethods.add(contract.getMethod(method));
			}
		}
		return matchedMethods;
	}

	@Override
	protected WizardRunner<IType> getCorrespondingWizard(IType type) {
		return new CreateContractWizardRunner(type);
	}

	@Override
	protected Requestor getRequestor() {
		return new ContractRequestor();
	}
	
	public Set<IType> getCorrespondingClasses() {
		Set<IType> assosiatedMemers = new HashSet<IType>();
		IType[] typeHierachy = TypeHierarchyRequestor.getTypeHierachy(getType());
		for (IType type : typeHierachy) {
			assosiatedMemers.addAll(getCorrespondingTypeSearcher().getAssociatedMemberOf(type));
		}
		return assosiatedMemers;
	}

	@Override
	protected IMember openDialog(AssosiatedMemberRequest request, Collection<IType> proposedClasses,
			Collection<IMethod> proposedMethods, boolean perfectMatches) {
		String promptText = generatePromptText(request.getPromptText());
		String infoText = generateInfoText();

		Set<TreeActionElement<?>> createMethodActions = generateCreateMethodActions(request, proposedClasses);
		proposedClasses = removeDuplicates(proposedClasses, createMethodActions);
		
		MemberContentProvider contentProvider = new MemberContentProvider(proposedClasses, proposedMethods).withAction(new CreateNewClassAction() {
			@Override
			public IType execute() {
				return getCorrespondingWizard(getType()).run();
			}
		});

		contentProvider = contentProvider.withAction(createMethodActions);

		return new ChooseMemberDialog<IMember>(promptText, infoText, contentProvider).getChoice();
	}

	private Collection<IType> removeDuplicates(Collection<IType> proposedClasses,
			Set<TreeActionElement<?>> createMethodActions) {
		for (TreeActionElement<?> treeActionElement : createMethodActions) {
			if(treeActionElement instanceof CreateMethodAction)
				proposedClasses.remove(((CreateMethodAction)treeActionElement).getType());
		}
		
		return proposedClasses;
	}

	private Set<TreeActionElement<?>> generateCreateMethodActions(AssosiatedMemberRequest request, Collection<IType> proposedClasses) {
		Set<TreeActionElement<?>> createMethodActions = new HashSet<TreeActionElement<?>>();
		
		if (request.shouldReturn(MemberType.METHOD) && request.isCreateRequest()) {
			for (IType type : proposedClasses) {
				TypeFacade contract = C4JContract.createFacade(type.getCompilationUnit());
				if (!contract.hasMethod(request.getCurrentMethod())) {
					createMethodActions.add(new CreateMethodAction(type, request.getCurrentMethodDeclaration()));
				}
			}
		}
		return createMethodActions;
	}
	
	private String generatePromptText(String promptText) {
		return String.format("%s %s%s", promptText, System.getProperty("line.separator"), "The following Contracts could be found:");
	}

	private String generateInfoText() {
		return "Please note that the 'create' functionality does not comprise static code analysis, make sure your contract(s) is(are) valid.";
	}
}
