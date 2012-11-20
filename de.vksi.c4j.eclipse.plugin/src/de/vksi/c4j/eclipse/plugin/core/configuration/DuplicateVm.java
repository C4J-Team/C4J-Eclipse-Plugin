package de.vksi.c4j.eclipse.plugin.core.configuration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMStandin;

public class DuplicateVM {
	private static final String JRE_CONTAINER = "org.eclipse.jdt.launching.JRE_CONTAINER";
	private IJavaProject javaProject;

	private VMStandin duplicatedStandin;
	private IVMInstall duplicatedVm;

	public DuplicateVM(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public void duplicateVM(String nameOfDuplicate, String idOfDuplicate) throws CoreException {
		if (!isDuplicated(nameOfDuplicate, idOfDuplicate)) {
			IVMInstall currentInstalledVM = JavaRuntime.getVMInstall(this.javaProject);
			duplicatedStandin = new VMStandin(currentInstalledVM, currentInstalledVM.getId() + idOfDuplicate);
			duplicatedStandin.setName(currentInstalledVM.getName() + nameOfDuplicate);
		}
	}

	public void convertDuplicateToRealVM() throws CoreException {
		removeJREContainer();
		duplicatedVm = duplicatedStandin.convertToRealVM();
		addNewJREContainer();
	}
	
	public void setVMArgument(String parameter) {
		if (duplicatedStandin != null) {
			duplicatedStandin.setVMArguments(new String[] { parameter });
		}
	}

	private boolean isDuplicated(String nameOfDuplicate, String idOfDuplicate) throws CoreException {
		IVMInstall vmInstall = JavaRuntime.getVMInstall(this.javaProject);

		boolean nameEndsWithC4Jpostfix = vmInstall.getName().endsWith(nameOfDuplicate);
		boolean idEndsWithC4Jpostfix = vmInstall.getId().endsWith(idOfDuplicate);

		return nameEndsWithC4Jpostfix && idEndsWithC4Jpostfix;
	}

	private void removeJREContainer() throws CoreException {
		IClasspathEntry[] classpathEntries = this.javaProject.getRawClasspath();
		List<IClasspathEntry> entriesWithoutJREContainer = new ArrayList<IClasspathEntry>();

		for (IClasspathEntry entry : classpathEntries) {
			if (!isJreContainerEntry(entry)) {
				entriesWithoutJREContainer.add(entry);
			}
		}

		setClasspathEntries(entriesWithoutJREContainer);
	}

	private void setClasspathEntries(List<IClasspathEntry> entriesWithoutJREContainer)
			throws JavaModelException {
		IClasspathEntry[] entries = entriesWithoutJREContainer
				.toArray(new IClasspathEntry[entriesWithoutJREContainer.size()]);
		this.javaProject.setRawClasspath(entries, null);
	}

	private boolean isJreContainerEntry(IClasspathEntry entry) {
		return IClasspathEntry.CPE_CONTAINER == entry.getEntryKind()
				&& entry.getPath().toString().startsWith(JRE_CONTAINER);
	}

	private void addNewJREContainer() throws CoreException {
		if (duplicatedVm != null) {
			IPath jreContainerPath = JavaRuntime.newJREContainerPath(duplicatedVm);
			IClasspathEntry jreEntry = JavaCore.newContainerEntry(jreContainerPath);
			new AddClasspathEntry(this.javaProject).add(jreEntry);
		}
	}
}