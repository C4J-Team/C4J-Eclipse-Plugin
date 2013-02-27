package de.vksi.c4j.eclipse.plugin.ui;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;

public interface ITreeContentAndDefaultSelectionProvider extends ITreeContentProvider
{
    ISelection getDefaultSelection();
}
