package de.vksi.c4j.eclipse.plugin.core.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

import de.vksi.c4j.eclipse.plugin.C4JEclipsePluginActivator;

public class C4JResources {
	private static final String C4J_JAR = "c4j";
	private static final String JAR_FILE_EXTENSION = "jar";
	private static final String RESOURCES_LIBS = "resources/libs/";
	private static final String RESOURCES_CONFIG = "resources/config/";

	private IFolder destinationFolder;
	
	
	/**
	 * @param libFolder
	 */
	public void copyConfigFilesTo(IFolder libFolder) {
		this.destinationFolder = libFolder;
		
		List<String> filesToCopy = getC4JResourcesFromBundle(RESOURCES_CONFIG);
		
		for (String file : filesToCopy) {
			if (!fileExists(file))
				copy(file);
		}
	}
	
	public void copyLibrariesTo(IFolder libFolder) {
		this.destinationFolder = libFolder;

		List<String> filesToCopy = getC4JResourcesFromBundle(RESOURCES_LIBS);

		for (String file : filesToCopy) {
			if (!fileExists(file))
				copy(file);
		}
	}

	private List<String> getC4JResourcesFromBundle(String pathToResources) {
		List<String> filesToCopy = new ArrayList<String>();
	
		Bundle c4jPluginBundle = C4JEclipsePluginActivator.getDefault().getBundle();
		Enumeration<String> elementsInDirectory = c4jPluginBundle.getBundleContext().getBundle().getEntryPaths(pathToResources);

		while (elementsInDirectory.hasMoreElements()) {
			String element = (String) elementsInDirectory.nextElement();
			if (!element.endsWith("/")) {
				filesToCopy.add(element);
			}
		}
		return filesToCopy;
	}

	private boolean fileExists(String fileName) {
		String file = removePathInformation(fileName);
		
		IFile jarFile = this.destinationFolder.getFile(file);

		return jarFile.exists();
	}

	private String removePathInformation(String fileName) {
		int index = fileName.lastIndexOf("/");
		String file = fileName.substring(index+1, fileName.length());
		return file;
	}
	
	private void copy(String file) {
		String fileName = removePathInformation(file);
		IFile destinationFile = this.destinationFolder.getFile(fileName);
		InputStream inputStream = null;

		try {
			Path pathToLib = new Path(file);
			Bundle c4jPluginBundle = C4JEclipsePluginActivator.getDefault().getBundle();
			inputStream = FileLocator.openStream(c4jPluginBundle, pathToLib, false);
			destinationFile.create(inputStream, false, null);
		} catch (Exception e) {
			// C4JEclipsePluginActivator.log("Error creating jar "+jarFilename,
			// e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// C4JEclipsePluginActivator.log("Error closing stream "+jarFilename,
					// e);
				}
			}
		}
	}
	
	public String getPathToLocalC4JJar() {
		List<IFile> resourceFiles = getFilesFrom(this.destinationFolder);

		for (IFile file : resourceFiles) {
			if (isLocalC4JJar(file))
				return file.getLocation().toString();
		}

		return "";
	}


	
	private boolean isLocalC4JJar(IFile file) {
		String extension = file.getFileExtension();
		String fileName = file.toString().toLowerCase();
		if (JAR_FILE_EXTENSION.equals(extension) && fileName.contains(C4J_JAR)) {
			return true;
		}

		return false;
	}
	
	private List<IFile> getFilesFrom(IFolder folder) {
		List<IFile> resourceFiles = new ArrayList<IFile>();
		IResource[] resources = getAllMemberResourcesFrom(folder);

		for (IResource res : resources) {
			switch (res.getType()) {
			case IResource.FILE:
				resourceFiles.add((IFile) res);
				break;
			}
		}

		return resourceFiles;
	}

	private IResource[] getAllMemberResourcesFrom(IFolder folder) {
		IResource[] resources = null;
		try {
			resources = folder.members();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return resources;
	}

	public void addJarsToClasspath(IJavaProject javaProject) throws JavaModelException {
		new AddClasspathEntry(javaProject).add(this.getClassPathEntries());
	}

	private List<IClasspathEntry> getClassPathEntries() {
		List<IClasspathEntry> classPathEntries = new ArrayList<IClasspathEntry>();
		List<IFile> resourceFiles = getFilesFrom(this.destinationFolder);
		
		for (IFile file : resourceFiles) {
			String extension = file.getFileExtension();
			
			if (JAR_FILE_EXTENSION.equals(extension)) {
				classPathEntries.add(JavaCore.newLibraryEntry(file.getFullPath(), null, null));
			}
		}
		
		return classPathEntries;
	}
}
