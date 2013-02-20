package de.vksi.c4j.eclipse.plugin.ui.quickassist;

import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ContentProvider implements ITreeContentProvider {
	private List<IType> contracts;

	@Override
	public void dispose() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.contracts = (List<IType>) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return contracts.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}
}
