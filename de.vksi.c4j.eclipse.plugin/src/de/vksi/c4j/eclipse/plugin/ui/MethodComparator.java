package de.vksi.c4j.eclipse.plugin.ui;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;

import org.eclipse.jdt.core.IMethod;

public class MethodComparator implements Comparator<IMethod>, Serializable
{
	private static final long serialVersionUID = 8349424365273152793L;

	public int compare(IMethod method1, IMethod method2)
    {
        return Collator.getInstance().compare(method1.getElementName(), method2.getElementName());
    }
}
