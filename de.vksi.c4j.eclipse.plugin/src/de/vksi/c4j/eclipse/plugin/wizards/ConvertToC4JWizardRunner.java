package de.vksi.c4j.eclipse.plugin.wizards;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import de.vksi.c4j.eclipse.plugin.C4JEclipsePluginActivator;
import de.vksi.c4j.eclipse.plugin.core.configuration.ProjectConverter;

@SuppressWarnings("restriction")
public class ConvertToC4JWizardRunner implements WizardRunner<Boolean> {
	private static Logger logger = C4JEclipsePluginActivator.getLogManager().getLogger(ConvertToC4JWizardRunner.class.getName());

	private static final String MESSAGE = "Set up C4J-Project environment";
	private static final String TITLE = "Convert to C4J-Project";
	private IJavaProject javaProject;
	private IStructuredSelection selection;

	public ConvertToC4JWizardRunner(IJavaProject javaProject, IStructuredSelection selection) {
		this.javaProject = javaProject;
		this.selection = selection;
	}

	@Override
	public Boolean run() {
		ConvertToC4JWizard convertToC4JWizard = new ConvertToC4JWizard(javaProject);
		convertToC4JWizard.init(PlatformUI.getWorkbench(), selection);
		Shell parent = JavaPlugin.getActiveWorkbenchShell();
		WizardDialog dialog = new WizardDialog(parent, convertToC4JWizard);
		dialog.setHelpAvailable(false);
		dialog.setPageSize(300, 150);
		dialog.create();
		dialog.setTitle(TITLE);
		dialog.setMessage(MESSAGE);

		if (dialog.open() == Window.OK)
			return convertProject();

		return false;
	}

	private boolean convertProject() {
		try {
			//TODO: use ProgressMonitor to provide feedback
			return new ProjectConverter().convertToC4JProject(javaProject);
		} catch (Exception e) {
			logger.error("Could not convert into C4J-Project", e);
		} 
		return false;
	}
}
