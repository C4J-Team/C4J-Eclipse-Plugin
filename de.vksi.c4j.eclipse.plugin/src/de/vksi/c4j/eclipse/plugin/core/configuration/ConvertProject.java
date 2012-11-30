package de.vksi.c4j.eclipse.plugin.core.configuration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

public class ConvertProject {

	private static final String LIB_FOLDER_NAME = "libs";
	private IFolder libFolder;
	private IJavaProject javaProject;
	private C4JVM c4jVM;

	public void toC4JProject(IJavaProject javaProject) throws CoreException {
		this.javaProject = javaProject;
		this.libFolder = createLibFolder();

		C4JResources c4jLibs = new C4JResources();
		c4jLibs.copyLibrariesTo(libFolder);
		c4jLibs.addJarsToClasspath(javaProject);
		String c4JLocation = c4jLibs.getPathToLocalC4JJar();
		
		this.c4jVM = new C4JVM(this.javaProject);
		this.c4jVM.setUpC4JVM(c4JLocation);
	}

	private IFolder createLibFolder() throws CoreException {
		IProject generalProject = this.javaProject.getProject();
		IFolder folder = generalProject.getFolder(LIB_FOLDER_NAME);

		if (!folder.exists()) {
			folder.create(false, true, null);
		}

		return folder;
	}
}
