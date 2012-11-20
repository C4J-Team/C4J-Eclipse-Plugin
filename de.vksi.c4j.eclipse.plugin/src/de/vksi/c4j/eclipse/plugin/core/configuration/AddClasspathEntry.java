package de.vksi.c4j.eclipse.plugin.core.configuration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

public class AddClasspathEntry {

	private IJavaProject javaProject;

	public AddClasspathEntry(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public void add(IClasspathEntry entry) throws JavaModelException {
		List<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>();
		newEntries.add(entry);
		add(newEntries);
	}
	
	public void add(List<IClasspathEntry> entries) throws JavaModelException {
		for (IClasspathEntry entry : javaProject.getRawClasspath()) {
			entries.add(entry);
		}
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
	}

}
