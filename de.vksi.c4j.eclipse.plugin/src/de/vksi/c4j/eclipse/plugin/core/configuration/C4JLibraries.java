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

class C4JLibraries {
	private static final String C4J_JAR = "c4j";
	private static final String JAR_FILE_EXTENSION = "jar";
	private static final String RESOURCES_LIBS = "resources/libs/";

	private IFolder destinationLibFolder;
	
	
	public void copyToProjectFolder(IFolder libFolder) {
		this.destinationLibFolder = libFolder;

		List<String> filesToCopy = getC4JLibsFromBundle(RESOURCES_LIBS);

		for (String file : filesToCopy) {
			if (!fileExists(file))
				copy(file);
		}
	}

	private List<String> getC4JLibsFromBundle(String pathToLibs) {
		List<String> filesToCopy = new ArrayList<String>();
	
		Bundle c4jPluginBundle = C4JEclipsePluginActivator.getDefault().getBundle();
		Enumeration<String> elementsInDirectory = c4jPluginBundle.getBundleContext().getBundle().getEntryPaths(pathToLibs);

		while (elementsInDirectory.hasMoreElements()) {
			String element = (String) elementsInDirectory.nextElement();
			if (!element.endsWith("/")) {
				filesToCopy.add(element.replace(RESOURCES_LIBS, ""));
			}
		}
		return filesToCopy;
	}

	private boolean fileExists(String fileName) {
		IFile jarFile = this.destinationLibFolder.getFile(fileName);

		return jarFile.exists();
	}
	
	private void copy(String file) {
		IFile destinationFile = this.destinationLibFolder.getFile(file);
		InputStream inputStream = null;

		try {
			Path pathToLib = new Path(RESOURCES_LIBS + file);
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
		List<IFile> resourceFiles = getResourceFilesFrom(this.destinationLibFolder);

		for (IFile file : resourceFiles) {
			if (isLocalC4JJar(file))
				return file.getLocation().toString();
		}

		return "";
	}

	private List<IFile> getResourceFilesFrom(IFolder folder) {
		List<IFile> resourceFiles = new ArrayList<IFile>();
		IResource[] resources = getResourcesFrom(folder);

		for (IResource res : resources) {
			switch (res.getType()) {
			case IResource.FILE:
				resourceFiles.add((IFile) res);
				break;
			}
		}

		return resourceFiles;
	}
	
	private boolean isLocalC4JJar(IFile file) {
		String extension = file.getFileExtension();
		String fileName = file.toString().toLowerCase();
		if (JAR_FILE_EXTENSION.equals(extension) && fileName.contains(C4J_JAR)) {
			return true;
		}

		return false;
	}
	
	private IResource[] getResourcesFrom(IFolder folder) {
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
		List<IFile> resourceFiles = getResourceFilesFrom(this.destinationLibFolder);
		
		for (IFile file : resourceFiles) {
			String extension = file.getFileExtension();
			
			if (JAR_FILE_EXTENSION.equals(extension)) {
				classPathEntries.add(JavaCore.newLibraryEntry(file.getFullPath(), null, null));
			}
		}
		
		return classPathEntries;
	}
}
