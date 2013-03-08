package de.vksi.c4j.eclipse.plugin.util.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.jdt.core.IClasspathEntry;

public class ClasspathEntryComparator implements Comparator<IClasspathEntry>, Serializable {

	private static final long serialVersionUID = -3484393390280399325L;

	@Override
	public int compare(IClasspathEntry o1, IClasspathEntry o2) {
		return String.valueOf(o1.getEntryKind()).compareTo(String.valueOf(o2.getEntryKind())); 
	}

}
