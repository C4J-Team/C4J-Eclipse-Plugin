/**
 * 
 */
package de.vksi.c4j.eclipse.plugin.ui;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.jdt.core.IType;

public final class TypeComparator implements Comparator<IType>, Serializable
{
	private static final long serialVersionUID = -5220998736699770333L;

	public int compare(IType first, IType second)
    {
        return first.getFullyQualifiedName().compareTo(second.getFullyQualifiedName());
    }
}