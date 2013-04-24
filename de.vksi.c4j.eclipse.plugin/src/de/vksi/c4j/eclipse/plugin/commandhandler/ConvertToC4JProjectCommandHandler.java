package de.vksi.c4j.eclipse.plugin.commandhandler;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import de.vksi.c4j.eclipse.plugin.wizards.ConvertToC4JWizardRunner;

public class ConvertToC4JProjectCommandHandler extends AbstractHandler {
	//TODO: refactor
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = getSelection(event);

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			for (Iterator<?> it = structuredSelection.iterator(); it.hasNext();) {
				Object element = it.next();
				IProject project = null;
				if (element instanceof IProject)
					project = (IProject) element;
				else if (element instanceof IAdaptable)
					project = (IProject) ((IAdaptable) element).getAdapter(IProject.class);

				if (project != null)
					convert(project, structuredSelection);
			}
		}

		return null;
	}

	private ISelection getSelection(ExecutionEvent event) {
		return HandlerUtil.getActiveMenuSelection(event);
	}

	private void convert(final IProject project, IStructuredSelection selection) {
		if (selection.size() == 1) {
			IJavaProject javaProject = JavaCore.create(project);
			if (javaProject.exists()) {
				try {
					new ConvertToC4JWizardRunner(javaProject, selection).run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
