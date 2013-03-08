package de.vksi.c4j.eclipse.plugin.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

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

import de.vksi.c4j.eclipse.plugin.core.configuration.ProjectConverter;
import de.vksi.c4j.eclipse.plugin.internal.C4JPluginSettings;

public class C4JProjectWizard extends Wizard implements IExecutableExtension, INewWizard {

	private C4JProjectWizardPageOne fMainPage;
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

		fMainPage = new C4JProjectWizardPageOne();
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
			IJavaProject javaProject = setUpC4Jconfiguration();

			BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
			BasicNewResourceWizard.selectAndReveal(javaProject.getResource(), PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow());

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			return false; // TODO: should open error dialog and log
		} catch (InterruptedException e) {
			return false; // canceled
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	private IJavaProject setUpC4Jconfiguration() throws CoreException, IOException {
		IJavaProject javaProject = fJavaPage.getJavaProject();
		IFolder folder = javaProject.getProject().getFolder(C4JProjectWizardPageOne.SRC_MAIN_RESOURCES);
		C4JPluginSettings c4jPluginSettings = new C4JPluginSettings(javaProject);
		c4jPluginSettings.setPathToConfigFiles(folder.getFullPath());

		new ProjectConverter().convertToC4JProject(javaProject);
		return javaProject;
	}

	public boolean performCancel() {
		fJavaPage.performCancel();
		return true;
	}

}
