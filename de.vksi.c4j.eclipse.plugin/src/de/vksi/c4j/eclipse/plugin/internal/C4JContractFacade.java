package de.vksi.c4j.eclipse.plugin.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

import de.vksi.c4j.eclipse.plugin.util.requestor.AssosiatedMemberRequest;
import de.vksi.c4j.eclipse.plugin.util.requestor.Requestor;
import de.vksi.c4j.eclipse.plugin.util.requestor.TargetRequestor;
import de.vksi.c4j.eclipse.plugin.wizards.WizardRunner;

public class C4JContractFacade extends TypeFacade {

	protected C4JContractFacade(ICompilationUnit compilationUnit) {
		super(compilationUnit);
	}

	@Override
	protected Collection<IMethod> getAssosiatedMethodsFromClasses(IMethod method, Collection<IType> classes) {
		List<IMethod> matchedMethods = new ArrayList<IMethod>();
		for (IType type : classes) {
			TypeFacade target = C4JContractFacade.createFacade(type.getCompilationUnit());
			if (target.hasMethod(method)) {
				matchedMethods.add(target.getMethod(method));
			}
		}
		return matchedMethods;
	}

	@Override
	protected WizardRunner<IType> getCorrespondingWizard(IType fromType) {
		return null;
	}

	@Override
	protected Requestor getRequestor() {
		return new TargetRequestor();
	}
	
	public Collection<IType> getCorrespondingClasses() {
		return getCorrespondingTypeSearcher().getAssociatedMemberOf(getType());
	}

	@Override
	protected IMember openDialog(AssosiatedMemberRequest request, Collection<IType> proposedClasses,
			Collection<IMethod> proposedMethods, boolean perfectMatches) {
		return null;
	}
}
