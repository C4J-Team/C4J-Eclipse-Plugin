package de.vksi.c4j.eclipse.plugin.core.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.CPListElement;

import de.vksi.c4j.eclipse.plugin.C4JEclipsePluginActivator;
import de.vksi.c4j.eclipse.plugin.util.comparator.ClasspathEntryComparator;

@SuppressWarnings("restriction")
public class Classpath {
	private static Logger logger = C4JEclipsePluginActivator.getLogManager().getLogger(Classpath.class.getName());

	private IJavaProject javaProject;

	public Classpath(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public void add(IClasspathEntry entry) throws JavaModelException {
		Set<IClasspathEntry> newEntries = new HashSet<IClasspathEntry>();
		newEntries.add(entry);
		add(newEntries);
	}

	public void add(Set<IClasspathEntry> entries) throws JavaModelException {
		entries.addAll(getClasspathEntries());
		List<IClasspathEntry> sortedList = sortClasspathEnties(entries);
		javaProject.setRawClasspath(sortedList.toArray(new IClasspathEntry[sortedList.size()]), null);
	}

	private List<IClasspathEntry> sortClasspathEnties(Set<IClasspathEntry> entries) throws JavaModelException {
		List<IClasspathEntry> sortedList = new ArrayList<IClasspathEntry>();
		sortedList.addAll(entries);
		Collections.sort(sortedList, new ClasspathEntryComparator());
		return sortedList;
	}

	public IPackageFragmentRoot addSourceFolder(IFolder folder) {
		IPackageFragmentRoot packageFragmentRoot = javaProject.getPackageFragmentRoot(folder);
		if (!packageFragmentRoot.exists()) {
			try {
				CoreUtility.createFolder(folder, true, true, null);
				Set<IClasspathEntry> entries = getClasspathEntries();
				Set<IClasspathEntry> existingSrcFolder = extractExistingSourceFolder(entries);
				entries.removeAll(existingSrcFolder);

				entries.add(JavaCore.newSourceEntry(packageFragmentRoot.getPath()));
				entries.addAll(addExclusionPattern(folder, existingSrcFolder));
				List<IClasspathEntry> sortedEntries = sortClasspathEnties(entries);
				javaProject.setRawClasspath(sortedEntries.toArray(new IClasspathEntry[sortedEntries.size()]), null);
			} catch (CoreException e) {
				logger.error("Could not add source folder to classpath", e);
			}
		}
		return packageFragmentRoot;
	}

	private Set<IClasspathEntry> getClasspathEntries() throws JavaModelException {
		Set<IClasspathEntry> entries = new HashSet<IClasspathEntry>();
		for (IClasspathEntry entry : javaProject.getRawClasspath()) {
			entries.add(entry);
		}
		return entries;
	}

	private Set<IClasspathEntry> extractExistingSourceFolder(Set<IClasspathEntry> classpathEntries) {
		Set<IClasspathEntry> scrFolder = new HashSet<IClasspathEntry>();
		for (IClasspathEntry entry : classpathEntries) {
			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				scrFolder.add(entry);
			}
		}
		return scrFolder;
	}

	private Set<IClasspathEntry> addExclusionPattern(IFolder folderToExclude, Set<IClasspathEntry> entries) {
		Set<IClasspathEntry> modifiedScrFolder = new HashSet<IClasspathEntry>();
		for (IClasspathEntry iClasspathEntry : entries) {
			CPListElement cp = CPListElement.createFromExisting(iClasspathEntry, javaProject);
			cp.addToExclusions(folderToExclude.getFullPath());
			modifiedScrFolder.add(cp.getClasspathEntry());
		}
		return modifiedScrFolder;
	}

}
