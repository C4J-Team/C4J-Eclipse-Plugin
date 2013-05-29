package de.vksi.c4j.eclipse.plugin.core.configuration;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.C4J_GLOBAL;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.C4J_JAR;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.C4J_LOCAL;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.C4J_PURE_REGISTRY;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.CHANGELOG;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.JAVASSIST_JAR;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.LOG4J_JAR;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.LOG4J_PROPERTIES;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

import de.vksi.c4j.eclipse.plugin.C4JEclipsePluginActivator;

public class C4JResources {
	private static Logger logger = C4JEclipsePluginActivator.getLogManager().getLogger(C4JResources.class.getName());
	private static final String JAR_FILE_EXTENSION = "jar";
	private static final String LIBS_PATH = "resources/libs/";
	private static final String CONFIG_PATH = "resources/config/";
	private static final HashMap<String, List<String>> resourceMap = create();
	private IFolder destConfigFolder;
	private IFolder destLibFolder;

	private static HashMap<String, List<String>> create() {
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		map.put(LIBS_PATH, Arrays.asList(C4J_JAR, JAVASSIST_JAR, LOG4J_JAR, CHANGELOG));
		map.put(CONFIG_PATH, Arrays.asList(C4J_GLOBAL, C4J_LOCAL, C4J_PURE_REGISTRY, LOG4J_PROPERTIES));
		return map;
	}

	public void copyConfigFilesTo(IFolder destFolder) {
		destConfigFolder = destFolder;
		copyFiles(destConfigFolder, getResourceURLsFromBundle(CONFIG_PATH));
	}

	public void copyLibrariesTo(IFolder destFolder) {
		destLibFolder = destFolder;
		copyFiles(destLibFolder, getResourceURLsFromBundle(LIBS_PATH));
	}

	public IFile getLocalC4Jjar() {
		IFile c4jJar = destLibFolder.getFile(C4J_JAR);
		return c4jJar.exists() ? c4jJar : null;
	}

	public void addJarsToClasspath(IJavaProject javaProject) throws JavaModelException {
		new Classpath(javaProject).add(getClassPathEntries());
	}

	private void copyFiles(IFolder destFolder, List<URL> paths) {
		for (URL path : paths) {
			copy(destFolder, path);
		}
	}

	private void copy(IFolder destFolder, URL url) {
		IFile destFileHandle = getResourceHandle(destFolder, url.getFile());
		if (!destFileHandle.exists()) {
			createResource(url, destFileHandle);
		}
	}

	private void createResource(URL url, IFile destFileHandle) {
		try {
			destFileHandle.create(url.openStream(), false, null);
		} catch (Exception e) {
			logger.error("Could not create C4J resource " + destFileHandle.getName(), e);
		}
	}

	private IFile getResourceHandle(IFolder destFolder, String fileName) {
		fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());

		return destFolder.getFile(fileName);
	}

	private List<URL> getResourceURLsFromBundle(String pathToResources) {
		List<URL> urls = new ArrayList<URL>();
		Bundle c4jPluginBundle = C4JEclipsePluginActivator.getDefault().getBundle();

		for (String res : resourceMap.get(pathToResources)) {
			urls.add(c4jPluginBundle.getEntry(pathToResources + res));
		}

		return urls;
	}

	private Set<IClasspathEntry> getClassPathEntries() {
		Set<IClasspathEntry> classPathEntries = new HashSet<IClasspathEntry>();

		for (String res : resourceMap.get(LIBS_PATH)) {
			IFile resourceHandle = getResourceHandle(destLibFolder, res);
			if (resourceHandle.exists() && JAR_FILE_EXTENSION.equalsIgnoreCase(resourceHandle.getFileExtension()))
				classPathEntries.add(JavaCore.newLibraryEntry(resourceHandle.getFullPath(), null, null));
		}
		return classPathEntries;
	}
}
