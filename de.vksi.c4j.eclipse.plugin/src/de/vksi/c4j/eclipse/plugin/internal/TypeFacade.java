package de.vksi.c4j.eclipse.plugin.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;

import de.vksi.c4j.eclipse.plugin.ui.CreateMethodAction;
import de.vksi.c4j.eclipse.plugin.util.requestor.AssosiatedMemberRequest;
import de.vksi.c4j.eclipse.plugin.util.requestor.Requestor;
import de.vksi.c4j.eclipse.plugin.util.requestor.AssosiatedMemberRequest.MemberType;
import de.vksi.c4j.eclipse.plugin.util.ContractChecker;
import de.vksi.c4j.eclipse.plugin.wizards.WizardRunner;

public abstract class TypeFacade {
	protected final ICompilationUnit compilationUnit;

	private Requestor assosiatedTypeSearcher;

	protected TypeFacade(ICompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	private TypeFacade(IFile file) {
		this(JavaCore.createCompilationUnitFrom(file));
	}

	protected TypeFacade(IEditorPart editorPart) {
		this((IFile) editorPart.getEditorInput().getAdapter(IFile.class));
	}

	public static TypeFacade createFacade(ICompilationUnit compilationUnit) {
		if (isContract(compilationUnit.findPrimaryType())) {
			return new C4JContractFacade(compilationUnit);
		}
		return new C4JTargetFacade(compilationUnit);
	}

	public static boolean isContract(IType type) {
		if (type == null)
			return false;

		return ContractChecker.isContract(type);
	}

	public IType getType() {
		return this.compilationUnit.findPrimaryType();
	}

	public ICompilationUnit getCompilationUnit() {
		return this.compilationUnit;
	}

	public IMember getAssosiatedType(AssosiatedMemberRequest request) {
		final Collection<IType> proposedClasses = getCorrespondingClasses();
		final MemberAction action;

		action = getPerfectCorrespondingMember(request, proposedClasses);

		if (action == null)
			return null;

		IMember memberToJump = action.getCorrespondingMember();

		return memberToJump;
	}

	public boolean hasMethod(IMethod method) {
		return getMethod(method).exists();
	}

	public IMethod getMethod(IMethod method) {
		if (isConstructor(method))
			return getType().getMethod(getType().getElementName(), method.getParameterTypes());

		return getType().getMethod(method.getElementName(), method.getParameterTypes());
	}

	private boolean isConstructor(IMethod method) {
		try {
			return method.isConstructor();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return false;
	}

	private MemberAction getPerfectCorrespondingMember(AssosiatedMemberRequest request,
			Collection<IType> proposedClasses) {

		MemberAction memberAction = null;
		if (request.shouldReturn(MemberType.METHOD))
			memberAction = getMemberForMethodRequest(request, proposedClasses);

		return memberAction != null ? memberAction : getMemberForTypeRequest(request, proposedClasses);
	}

	private MemberAction getMemberForMethodRequest(AssosiatedMemberRequest request,
			Collection<IType> proposedClasses) {
		Collection<IMethod> proposedMethods = findAssosiatedMethodInClasses(request, proposedClasses);

		if (proposedMethods.isEmpty() && request.isCreateRequest()) {
			if (proposedClasses.size() == 1)
				return new ReturnMember(request, proposedClasses.iterator().next());
			if (proposedClasses.size() > 1)
				return new OpenChoiceDialog(request, proposedClasses, proposedMethods, true);
		} else if (proposedMethods.size() == 1 && !request.isCreateRequest()) {
			return new ReturnMember(request, proposedMethods.iterator().next());
		} else if (proposedMethods.size() >= 1) {
			return new OpenChoiceDialog(request, proposedClasses, proposedMethods, true);
		}
		return null;
	}

	private MemberAction getMemberForTypeRequest(AssosiatedMemberRequest request,
			Collection<IType> proposedClasses) {
		if (proposedClasses.size() == 1 && !request.isCreateRequest()) {
			return new ReturnMember(request, proposedClasses.iterator().next());
		} else if (proposedClasses.size() >= 1) {
			return new OpenChoiceDialog(request, proposedClasses, true);
		} else if (request.isCreateRequest()) {
			return new OpenNewClassWizard();
		}
		return null;
	}

	private Collection<IMethod> findAssosiatedMethodInClasses(AssosiatedMemberRequest request,
			Collection<IType> classes) {
		Collection<IMethod> proposedMethods = new LinkedHashSet<IMethod>();

		if (request.shouldReturn(MemberType.METHOD)) {
			IMethod currentMethod = request.getCurrentMethod();
			if (currentMethod != null && !classes.isEmpty()) {
				proposedMethods.addAll(getAssosiatedMethodsFromClasses(currentMethod, classes));
			}
		}

		return proposedMethods;
	}

	public abstract Collection<IType> getCorrespondingClasses();

	protected Requestor getCorrespondingTypeSearcher() {
		if (this.assosiatedTypeSearcher == null)
			this.assosiatedTypeSearcher = getRequestor();

		return this.assosiatedTypeSearcher;
	}

	protected abstract Collection<IMethod> getAssosiatedMethodsFromClasses(IMethod method,
			Collection<IType> classes);

	protected abstract WizardRunner<IType> getCorrespondingWizard(IType fromType);

	protected abstract IMember openDialog(AssosiatedMemberRequest request, Collection<IType> proposedClasses,
			Collection<IMethod> proposedMethods, boolean perfectMatches);

	protected abstract Requestor getRequestor();

	private static interface MemberAction {
		IMember getCorrespondingMember();
	}

	private static class ReturnMember implements MemberAction {
		private final IMember member;
		private AssosiatedMemberRequest request;

		public ReturnMember(AssosiatedMemberRequest request, IMember member) {
			this.request = request;
			this.member = member;
		}

		public IMember getCorrespondingMember() {
			if (request.isCreateRequest() && request.shouldReturn(MemberType.METHOD)) {
				IType type = null;
				if (member instanceof IType)
					type = (IType) member;
				else if (member instanceof IMethod)
					type = ((IMethod) member).getDeclaringType();

				return new CreateMethodAction(type, request.getCurrentMethodDeclaration()).execute();
			}
			return member;
		}
	}

	private class OpenChoiceDialog implements MemberAction {
		private final AssosiatedMemberRequest request;
		private final Collection<IType> proposedClasses;
		private final Collection<IMethod> proposedMethods;
		private final boolean perfectMatches;

		public OpenChoiceDialog(AssosiatedMemberRequest request, Collection<IType> proposedClasses,
				boolean perfectMatches) {
			this(request, proposedClasses, Collections.<IMethod> emptySet(), perfectMatches);
		}

		public OpenChoiceDialog(AssosiatedMemberRequest request, Collection<IType> proposedClasses,
				Collection<IMethod> proposedMethods, boolean perfectMatches) {
			this.request = request;
			this.proposedClasses = proposedClasses;
			this.proposedMethods = proposedMethods;
			this.perfectMatches = perfectMatches;
		}

		public IMember getCorrespondingMember() {
			return openDialog(request, proposedClasses, proposedMethods, perfectMatches);
		}
	}

	private class OpenNewClassWizard implements MemberAction {
		public IType getCorrespondingMember() {
			WizardRunner<IType> wizard = getCorrespondingWizard(getType());
			return wizard != null ? wizard.run() : null;
		}
	}
}
