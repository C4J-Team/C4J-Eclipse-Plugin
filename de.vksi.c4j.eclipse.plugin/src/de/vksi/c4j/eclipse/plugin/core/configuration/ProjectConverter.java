package de.vksi.c4j.eclipse.plugin.core.configuration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

public class ProjectConverter {

	private static final String LIB_FOLDER_NAME = "libs";
	private static final String CONFIG_FOLDER_NAME = "config";
	private IFolder libFolder;
	private IFolder configFolder;
	private IJavaProject javaProject;
	private C4JVM c4jVM;

	public void convertToC4JProject(IJavaProject javaProject) throws CoreException {
		this.javaProject = javaProject;
		libFolder = createFolder(LIB_FOLDER_NAME);
		configFolder = createFolder(CONFIG_FOLDER_NAME);

		C4JResources c4jResources = new C4JResources();
		c4jResources.copyLibrariesTo(libFolder);
		c4jResources.copyConfigFilesTo(configFolder); //TODO: do not add config files if project is created by c4j wizard
		c4jResources.addJarsToClasspath(javaProject);
		IFile localC4J = c4jResources.getLocalC4Jjar();
		
		c4jVM = new C4JVM(javaProject);
		c4jVM.setUpC4JVM(localC4J);
	}

	private IFolder createFolder(String folderName) throws CoreException {
		IProject generalProject = javaProject.getProject();
		IFolder folder = generalProject.getFolder(folderName);

		if (!folder.exists()) 
			folder.create(false, true, null);

		return folder;
	}
}
