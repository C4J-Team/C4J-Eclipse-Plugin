package de.vksi.c4j.eclipse.plugin.ui;

import java.io.Serializable;
import java.util.Comparator;

public class TreeActionElementComparator implements Comparator<TreeActionElement<?>>, Serializable
{
	private static final long serialVersionUID = -514470499531226824L;

	public int compare(TreeActionElement<?> first, TreeActionElement<?> second)
    {
        return first.getText().compareTo(second.getText());
    }
}
