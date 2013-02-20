package de.vksi.c4j.eclipse.plugin.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import de.vksi.c4j.eclipse.plugin.core.configuration.ProjectConvert;

public class ConvertToC4JProjectCommand extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = getSelection(event);

	    Object firstElement = selection.getFirstElement();
	    
	    if (firstElement instanceof IJavaProject) {
	    	try {
	    		new ProjectConvert().toC4JProject((IJavaProject) firstElement);
			} catch (Exception e) {
//				C4jEclipsePlugin.log("Eror converting project to c4j project", e);
			}
	    }
		return null;
	}

	private IStructuredSelection getSelection(ExecutionEvent event) {
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
	    IStructuredSelection selection = (IStructuredSelection) sel;
		return selection;
	}
}
