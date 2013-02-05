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

public class VMDuplicator {
	private static final String JRE_CONTAINER = "org.eclipse.jdt.launching.JRE_CONTAINER";
	private IJavaProject javaProject;

	private VMStandin duplicatedStandin;
	private IVMInstall duplicatedVm;

	public VMDuplicator(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public void duplicateVM(String nameOfDuplicate, String idOfDuplicate) throws CoreException {
		IVMInstall currentInstalledVM = JavaRuntime.getVMInstall(this.javaProject);
		duplicatedStandin = new VMStandin(currentInstalledVM, idOfDuplicate);
		duplicatedStandin.setName(nameOfDuplicate);
	}

	public VMStandin getDuplicate() {
		return this.duplicatedStandin;
	}

	public void convertDuplicateToRealVM() throws CoreException {
		if (!isAlreadyDuplicated()) {
			removeJREContainer();
			duplicatedVm = getDuplicate().convertToRealVM();
			addNewJREContainer();
		}
	}

	public void setVMArgumentForDuplicate(String parameter) {
		if (getDuplicate() != null) {
			duplicatedStandin.setVMArguments(new String[] { parameter });
		}
	}

	private boolean isAlreadyDuplicated() throws CoreException {
		IVMInstall vmInstall = JavaRuntime.getVMInstall(this.javaProject);

		boolean nameEndsWith = vmInstall.getName().equals(this.duplicatedStandin.getName());
		boolean idEndsWith = vmInstall.getId().equals(this.duplicatedStandin.getId());

		return nameEndsWith && idEndsWith;
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

	private void setClasspathEntries(List<IClasspathEntry> entries) throws JavaModelException {
		IClasspathEntry[] classPathEntries = entries.toArray(new IClasspathEntry[entries.size()]);
		this.javaProject.setRawClasspath(classPathEntries, null);
	}

	private boolean isJreContainerEntry(IClasspathEntry entry) {
		return IClasspathEntry.CPE_CONTAINER == entry.getEntryKind()
				&& entry.getPath().toString().startsWith(JRE_CONTAINER);
	}

	private void addNewJREContainer() throws CoreException {
		if (duplicatedVm != null) {
			IPath jreContainerPath = JavaRuntime.newJREContainerPath(duplicatedVm);
			IClasspathEntry jreEntry = JavaCore.newContainerEntry(jreContainerPath);
			new Classpath(this.javaProject).add(jreEntry);
		}
	}
}