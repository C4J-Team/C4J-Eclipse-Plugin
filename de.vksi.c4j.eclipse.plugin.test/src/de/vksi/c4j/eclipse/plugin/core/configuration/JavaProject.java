package de.vksi.c4j.eclipse.plugin.core.configuration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.jdt.launching.VMStandin;

public class JavaProject {
	public static final String BIN_FOLDER = "bin";
	public static final String SRC_FOLDER = "src";

	private IProject generalProject;
	private IJavaProject javaProject;

	public JavaProject() {
		this.generalProject = null;
		this.javaProject = null;
	}

	public IJavaProject create(String projectName) throws CoreException {
		this.generalProject = createGeneralProject(projectName);

		IProjectDescription description = this.generalProject.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		this.generalProject.setDescription(description, null);
		this.javaProject = JavaCore.create(this.generalProject);

		setUpJavaBuildPath();

		return this.javaProject;
	}

	private IProject createGeneralProject(String projectName) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		project.create(null);
		project.open(null);
		return project;
	}

	private void setUpJavaBuildPath() throws CoreException, JavaModelException {
		specifyOutputLocation();
		defineClassPathEntries();
		setUpVM();
		addFolderToClassEntries(SRC_FOLDER);
	}

	private void setUpVM() {
		try {
			IVMInstall defaultVMInstall = JavaRuntime.getDefaultVMInstall();
			VMStandin duplicatedStandin = new VMStandin(defaultVMInstall, defaultVMInstall.getId());
			IVMInstall duplicatedVm = duplicatedStandin.convertToRealVM();
			IPath jreContainerPath = JavaRuntime.newJREContainerPath(duplicatedVm);
			IClasspathEntry jreEntry = JavaCore.newContainerEntry(jreContainerPath);

			List<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>();
			newEntries.add(jreEntry);

			for (IClasspathEntry entry : javaProject.getRawClasspath()) {
				newEntries.add(entry);
			}

			this.javaProject
					.setRawClasspath(newEntries.toArray(new IClasspathEntry[newEntries.size()]), null);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

	}

	private void specifyOutputLocation() throws CoreException, JavaModelException {
		IFolder binFolder = this.generalProject.getFolder(BIN_FOLDER);
		binFolder.create(false, true, null);
		this.javaProject.setOutputLocation(binFolder.getFullPath(), null);
	}

	private void defineClassPathEntries() throws JavaModelException {
		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);

		for (LibraryLocation element : locations) {
			entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
		}

		// add libs to project class path
		this.javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
	}

	public void addFolderToClassEntries(String folderName) throws CoreException {

		if (this.javaProject != null) {
			IFolder folder = specifyFolder(folderName);

			IPackageFragmentRoot packageFragmentRoot = this.javaProject.getPackageFragmentRoot(folder);
			IClasspathEntry[] oldEntries = this.javaProject.getRawClasspath();
			IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
			System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
			newEntries[oldEntries.length] = JavaCore.newSourceEntry(packageFragmentRoot.getPath());
			this.javaProject.setRawClasspath(newEntries, null);
		}
	}

	private IFolder specifyFolder(String folderName) throws CoreException {
		IFolder folder = this.generalProject.getFolder(folderName);
		folder.create(false, true, null);
		return folder;
	}

}