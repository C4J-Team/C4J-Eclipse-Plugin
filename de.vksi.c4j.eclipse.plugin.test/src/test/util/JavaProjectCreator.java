package test.util;

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

public class JavaProjectCreator {
	public static final String BIN_FOLDER = "bin";
	public static final String SRC_FOLDER = "src";

	public static IJavaProject create(String projectName) throws CoreException {
		IProject generalProject;
		IJavaProject javaProject;

		generalProject = createGeneralProject(projectName);

		IProjectDescription description = generalProject.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		generalProject.setDescription(description, null);
		javaProject = JavaCore.create(generalProject);

		configureJavaBuildPathOf(javaProject);

		return javaProject;
	}

	private static IProject createGeneralProject(String projectName) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		project.create(null);
		project.open(null);
		return project;
	}

	private static void configureJavaBuildPathOf(IJavaProject javaProject) throws CoreException,
			JavaModelException {
		setOutputLocationOf(javaProject);
		setClassPathEntriesOf(javaProject);
		setUpVMOf(javaProject);
		addFolderToClassEntries(javaProject, SRC_FOLDER);
	}

	private static void setOutputLocationOf(IJavaProject javaProject) throws CoreException,
			JavaModelException {
		IFolder binFolder = javaProject.getProject().getFolder(BIN_FOLDER);
		binFolder.create(false, true, null);
		javaProject.setOutputLocation(binFolder.getFullPath(), null);
	}

	private static void setClassPathEntriesOf(IJavaProject javaProject) throws JavaModelException {
		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);

		for (LibraryLocation element : locations) {
			entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
		}

		// add libs to project class path
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
	}

	private static void setUpVMOf(IJavaProject javaProject) {
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

			javaProject.setRawClasspath(newEntries.toArray(new IClasspathEntry[newEntries.size()]), null);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

	}

	public static void addFolderToClassEntries(IJavaProject javaProject, String folderName) throws CoreException {
		if (javaProject != null) {
			IFolder folder = setFolder(javaProject, folderName);

			IPackageFragmentRoot packageFragmentRoot = javaProject.getPackageFragmentRoot(folder);

			IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
			IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];

			System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
			newEntries[oldEntries.length] = JavaCore.newSourceEntry(packageFragmentRoot.getPath());

			javaProject.setRawClasspath(newEntries, null);
		}
	}

	private static IFolder setFolder(IJavaProject javaProject, String folderName) throws CoreException {
		IFolder folder = javaProject.getProject().getFolder(folderName);
		folder.create(false, true, null);
		return folder;
	}

}