package de.vksi.c4j.eclipse.plugin.wizards;

import java.lang.reflect.InvocationTargetException;

import de.vksi.c4j.eclipse.plugin.core.configuration.C4JResources;
import de.vksi.c4j.eclipse.plugin.core.configuration.ConvertProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

public class C4JProjectWizard extends Wizard implements IExecutableExtension, INewWizard {

	private WizardPageOne fMainPage;
	private NewJavaProjectWizardPageTwo fJavaPage;

	private IConfigurationElement fConfigElement;

	public C4JProjectWizard() {
		setWindowTitle("New C4J Project");
	}

	public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
		fConfigElement = cfig;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	
	}

	public void addPages() {
		super.addPages();

		fMainPage = new WizardPageOne();
		fMainPage.setTitle("Create a C4J Project");
		addPage(fMainPage);

		fJavaPage = new NewJavaProjectWizardPageTwo(fMainPage);
		addPage(fJavaPage);
	}

	public boolean performFinish() {
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException,
					InterruptedException {
				fJavaPage.performFinish(monitor);
				// use the result from the extra page
			}
		};
		try {
			getContainer().run(false, true, op);

			IJavaProject newElement = fJavaPage.getJavaProject();

			IFolder folder = newElement.getProject().getFolder(WizardPageOne.SRC_MAIN_RESOURCES);
			C4JResources c4jRes = new C4JResources();
			c4jRes.copyConfigFilesTo(folder);

			new ConvertProject().toC4JProject(newElement);

			BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
			BasicNewResourceWizard.selectAndReveal(newElement.getResource(), PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow());

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			return false; // TODO: should open error dialog and log
		} catch (InterruptedException e) {
			return false; // canceled
		}
		return true;
	}

	public boolean performCancel() {
		fJavaPage.performCancel();
		return true;
	}

}
