package de.vksi.c4j.eclipse.plugin.commandhandler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.HandlerUtil;

public class Context {

	private static final int FIRST_TYPE = 0;
	private ExecutionEvent event;

	public Context(ExecutionEvent event) {
		this.event = event;
	}

	public IEditorPart getActiveEditorPart() {
		return HandlerUtil.getActiveEditor(event);
	}

	public ICompilationUnit getCompilationUnit() {
		IFile file = (IFile) getActiveEditorPart().getEditorInput().getAdapter(IFile.class);
		return JavaCore.createCompilationUnitFrom(file);
	}

	public ITextSelection getSelection() {
		IWorkbenchPartSite site = getActiveEditorPart().getSite();
		ISelectionProvider selectionProvider = site.getSelectionProvider();
		return (ITextSelection) selectionProvider.getSelection();
	}

	public IJavaElement getJavaElement() {
		try {
			IJavaElement element = getCompilationUnit().getElementAt(getSelection().getOffset());
			return element != null ? element : (IType)getCompilationUnit().getTypes()[FIRST_TYPE];
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}
}
