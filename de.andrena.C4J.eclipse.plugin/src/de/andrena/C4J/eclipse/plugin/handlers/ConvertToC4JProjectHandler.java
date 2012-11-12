package de.andrena.C4J.eclipse.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import de.andrena.C4J.eclipse.plugin.core.configuration.ConvertToC4jProject;

public class ConvertToC4JProjectHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = getSelection(event);

//	    Object firstElement = selection.getFirstElement();
//	    
//	    if (firstElement instanceof IJavaProject) {
//	    	try {
//	    		new ConvertToC4jProject((IJavaProject) firstElement).convert();
//			} catch (Exception e) {
////				C4jEclipsePlugin.log("Eror converting project to c4j project", e);
//			}
//	    }
		return null;
	}

	private IStructuredSelection getSelection(ExecutionEvent event) {
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
	    IStructuredSelection selection = (IStructuredSelection) sel;
		return selection;
	}
}
