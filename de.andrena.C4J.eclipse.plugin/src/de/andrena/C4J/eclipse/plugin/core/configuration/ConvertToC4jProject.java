package de.andrena.C4J.eclipse.plugin.core.configuration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jdt.launching.IVMInstall;

public class ConvertToC4jProject {

	private static final String LIB_FOLDER_NAME = "lib";
	private IJavaProject javaProject;

	public ConvertToC4jProject(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}
	
	public void convert() throws CoreException {
		
//		IFolder libFolder = createLibFolder(javaProject);
//		C4jJars c4jJars = new C4jJars(libFolder);
//		c4jJars.copyToLibFolder();
//		String c4JLocation = c4jJars.getC4jJarLocation();
//		c4jJars.addToClasspath(javaProject);
//		addVmParameters(javaProject, c4JLocation);
	}

	private void addVmParameters(IJavaProject javaProject, String c4JLocation) throws CoreException {
//		IVMInstall removedVm = new RemoveJreContainer(javaProject).remove();
//		if (removedVm != null) {
//			new DuplicateVm(removedVm).addVmParameters(c4JLocation).addJreClasspathEntryTo(javaProject);
//		} 
	}

	private IFolder createLibFolder(IJavaProject javaProject)
			throws CoreException {
//		IProject project = javaProject.getProject();
//		IFolder libFolder = project.getFolder(LIB_FOLDER_NAME);
//		if (!libFolder.exists()) {
//			libFolder.create(false, true, null);
//		}
//		return libFolder;
		
		return null;
	}
}
