package de.vksi.c4j.eclipse.plugin.core.configuration;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

public class Classpath {

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
		for (IClasspathEntry entry : javaProject.getRawClasspath()) {
			entries.add(entry);
		}
		
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
	}

}
