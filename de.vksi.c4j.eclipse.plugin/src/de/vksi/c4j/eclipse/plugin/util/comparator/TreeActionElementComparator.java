package de.vksi.c4j.eclipse.plugin.util.comparator;

import java.io.Serializable;
import java.util.Comparator;

import de.vksi.c4j.eclipse.plugin.ui.TreeActionElement;

public class TreeActionElementComparator implements Comparator<TreeActionElement<?>>, Serializable
{
	private static final long serialVersionUID = -514470499531226824L;

	public int compare(TreeActionElement<?> first, TreeActionElement<?> second)
    {
        return first.getText().compareTo(second.getText());
    }
}
