package de.vksi.c4j.eclipse.plugin.commands;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import de.vksi.c4j.eclipse.plugin.core.configuration.ProjectConverter;

public class ConvertToC4JProjectCommand extends AbstractHandler {

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
					convert(project, structuredSelection.size() == 1);
			}
		}
		return null;
	}

	private ISelection getSelection(ExecutionEvent event) {
		return HandlerUtil.getActiveMenuSelection(event);
	}

	private void convert(final IProject project, boolean isSingle) {
		if (isSingle) {
			IJavaProject javaProject = JavaCore.create(project);
			if (javaProject.exists()) {
				try {
					new ProjectConverter().convertToC4JProject(javaProject);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
