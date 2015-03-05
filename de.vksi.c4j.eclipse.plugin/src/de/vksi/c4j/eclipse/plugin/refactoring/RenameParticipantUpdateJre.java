package de.vksi.c4j.eclipse.plugin.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

public class RenameParticipantUpdateJre extends RenameParticipant {

	private static final String PROJECT_LOC_WITHNAME = "${project_loc:NAME}/";

	private IJavaProject javaProject;
	private String oldProjectName;

	@Override
	protected boolean initialize(Object element) {
		this.javaProject = (IJavaProject) element;
		this.oldProjectName = javaProject.getElementName();
		return true;
	}

	@Override
	public String getName() {
		return "JRE Updater for C4J";
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		return RefactoringStatus
				.createInfoStatus("Adapt JRE default arguments to new project name");
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {

		System.out.println("CreateChange");

		final String newProjectName = this.getArguments().getNewName();

		Change change = new Change() {

			@Override
			public String getName() {
				return "Adapt JRE default arguments to new project name";
			}

			@Override
			public void initializeValidationData(IProgressMonitor pm) {
			}

			@Override
			public RefactoringStatus isValid(IProgressMonitor pm)
					throws CoreException, OperationCanceledException {
				return RefactoringStatus
						.createInfoStatus("Adapt JRE default arguments to new project name");
			}

			@Override
			public Change perform(IProgressMonitor pm) throws CoreException {
				try {
					IVMInstall2 ivmInstall = (IVMInstall2) JavaRuntime
							.getVMInstall(javaProject);
					String args = ivmInstall.getVMArgs();
					System.out.println("args");
					String oldPart = PROJECT_LOC_WITHNAME.replace("NAME",
							oldProjectName);
					String newPart = PROJECT_LOC_WITHNAME.replace("NAME",
							newProjectName);
					args = args.replace(oldPart, newPart);
					ivmInstall.setVMArgs(args);
				} catch (CoreException e) {
					throw new RuntimeException(e);
				}

				return null;
			}

			@Override
			public Object getModifiedElement() {
				return null;
			}
		};

		return change;
	}

}