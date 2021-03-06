package de.vksi.c4j.eclipse.plugin.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;

import de.vksi.c4j.eclipse.plugin.util.PluginImages;

@SuppressWarnings("restriction")
public class CreateContractWizard extends NewElementWizard {

	private CreateContractWizardPage fPage;
	private boolean fOpenEditorOnFinish;

	public CreateContractWizard(CreateContractWizardPage page, boolean openEditorOnFinish) {
		setDefaultPageImageDescriptor(PluginImages.DESC_NEW_CONTRACT_WIZ);
		setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
		setWindowTitle(NewWizardMessages.NewClassCreationWizard_title);

		fPage = page;
		fOpenEditorOnFinish = openEditorOnFinish;
	}

	public CreateContractWizard() {
		this(null, true);
	}

	@Override
	public void addPages() {
		super.addPages();
		if (fPage == null) {
			fPage = new CreateContractWizardPage();
			fPage.setWizard(this);
			fPage.init(getSelection());
		}
		addPage(fPage);
	}

	@Override
	protected boolean canRunForked() {
		return !fPage.isEnclosingTypeSelected();
	}

	@Override
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		fPage.createType(monitor); // use the full progress monitor
	}

	@Override
	public boolean performFinish() {
		warnAboutTypeCommentDeprecation();
		boolean res = super.performFinish();
		if (res) {
			IResource resource = fPage.getModifiedResource();
			if (resource != null) {
				selectAndReveal(resource);
				if (fOpenEditorOnFinish) {
					openResource((IFile) resource);
				}
			}
		}
		return res;
	}

	@Override
	public IJavaElement getCreatedElement() {
		return fPage.getCreatedType();
	}

}
