package de.vksi.c4j.eclipse.plugin.wizards;

import java.io.IOException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

import de.vksi.c4j.eclipse.plugin.internal.C4JPluginSettings;

@SuppressWarnings("restriction")
public class ConvertToC4JWizard extends Wizard implements INewWizard {
	private ConvertToC4JWizardPage page;
	private IJavaProject javaProject;

	public ConvertToC4JWizard(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	@Override
	public void addPages() {
		page = new ConvertToC4JWizardPage(javaProject);
		addPage(page);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("C4J Project Environment");
		setDefaultPageImageDescriptor(IDEWorkbenchPlugin.getIDEImageDescriptor("wizban/new_wiz.png"));
	}

	@Override
	public boolean performFinish() {
		IPackageFragmentRoot configContainer = page.getConfigContainer();
		IFolder libContainer = page.getLibContainer();

		try {
			C4JPluginSettings c4jPluginSettings = new C4JPluginSettings(javaProject);
			c4jPluginSettings.setPathToConfigFiles(configContainer.getPath());
			c4jPluginSettings.setPathToLibFiles(libContainer.getFullPath());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}
}
