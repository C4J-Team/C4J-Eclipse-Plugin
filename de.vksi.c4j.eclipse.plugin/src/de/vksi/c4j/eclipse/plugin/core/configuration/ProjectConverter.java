package de.vksi.c4j.eclipse.plugin.core.configuration;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.DEFAULT_CONFIG_CONTAINER;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.DEFAULT_LIB_CONTAINER;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;

import de.vksi.c4j.eclipse.plugin.internal.C4JPluginSettings;

public class ProjectConverter {
	private IJavaProject javaProject;

	public boolean convertToC4JProject(IJavaProject javaProject) throws CoreException, IOException {
		this.javaProject = javaProject;
		C4JPluginSettings c4jPluginSettings = new C4JPluginSettings(javaProject);

		IFolder configFolder = setUpConfigFolder(c4jPluginSettings);
		IFolder libFolder = setUpLibFolder(c4jPluginSettings);

		C4JResources c4jResources = new C4JResources();
		c4jResources.copyLibrariesTo(libFolder);
		c4jResources.copyConfigFilesTo(configFolder);
		c4jResources.addJarsToClasspath(javaProject);
		IFile localC4J = c4jResources.getLocalC4Jjar();

		C4JVM c4jVM = new C4JVM(javaProject);
		c4jVM.setUpC4JVM(localC4J);

		return true;
	}

	private IFolder setUpConfigFolder(C4JPluginSettings c4jPluginSettings) throws IOException, CoreException {
		if (c4jPluginSettings.getPathToConfigFiles() != null)
			return createSrcFolder(c4jPluginSettings.getPathToConfigFiles());
		else {
			IFolder folder = createSrcFolder(new Path(DEFAULT_CONFIG_CONTAINER));
			c4jPluginSettings.setPathToConfigFiles(folder.getFullPath());
			return folder;
		}
	}

	private IFolder setUpLibFolder(C4JPluginSettings c4jPluginSettings) throws IOException, CoreException {
		if (c4jPluginSettings.getPathToLibFiles() != null)
			return createFolder(c4jPluginSettings.getPathToLibFiles());
		else {
			IFolder folder = createFolder(new Path(DEFAULT_LIB_CONTAINER));
			c4jPluginSettings.setPathToLibFiles(folder.getFullPath());
			return folder;
		}
	}

	private IFolder createFolder(IPath path) throws CoreException {
		IFolder folder = javaProject.getProject().getFolder(path);

		if (!folder.exists())
			folder.create(true, true, null);

		return folder;
	}

	private IFolder createSrcFolder(IPath path) {
		IFolder folder = javaProject.getProject().getFolder(path);
		
		if (!folder.exists())
			new Classpath(javaProject).addSourceFolder(folder);
		
		return folder;
	}
}
