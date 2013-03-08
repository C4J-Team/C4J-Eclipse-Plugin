package de.vksi.c4j.eclipse.plugin.wizards;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.DEFAULT_CONFIG_CONTAINER;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.DEFAULT_LIB_CONTAINER;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.ui.wizards.NewElementWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.vksi.c4j.eclipse.plugin.core.configuration.Classpath;
import de.vksi.c4j.eclipse.plugin.ui.BrowseFolderDialog;
import de.vksi.c4j.eclipse.plugin.ui.BrowseSourceFolderDialog;

@SuppressWarnings("restriction")
public class ConvertToC4JWizardPage extends NewElementWizardPage {
	private static final String PAGE_NAME = "PAGE_NAME";
	private static final String PAGE_TITLE = "Choose folder for C4J...";
	private static final String LIB_CONTAINER_LABEL = "Library files";
	private static final String CONFIG_CONTAINER_LABEL = "Configuration files";

	private static final String CONFIG_CONTAINER = "c4j.config.container";
	private static final String LIB_CONTAINER = "c4j.lib.container";

	private IStatus fConfigContainerStatus;
	private IStatus fLibContainerStatus;
	private StringButtonDialogField fConfigContainerDialogField;
	private StringButtonDialogField fLibContainerDialogField;
	private IJavaProject javaProject;

	public ConvertToC4JWizardPage(IJavaProject javaProject) {
		super(PAGE_NAME);
		this.javaProject = javaProject;

		fConfigContainerStatus = new StatusInfo();
		fLibContainerStatus = new StatusInfo();

		ConfigContainerFieldAdapter adapter = new ConfigContainerFieldAdapter();
		fConfigContainerDialogField = new StringButtonDialogField(adapter);
		fConfigContainerDialogField.setDialogFieldListener(adapter);
		fConfigContainerDialogField.setLabelText(getConfigContainerLabel());
		fConfigContainerDialogField.setButtonLabel(NewWizardMessages.NewContainerWizardPage_container_button);

		LibContainerFieldAdapter libContainerAdapter = new LibContainerFieldAdapter();
		fLibContainerDialogField = new StringButtonDialogField(libContainerAdapter);
		fLibContainerDialogField.setDialogFieldListener(libContainerAdapter);
		fLibContainerDialogField.setLabelText(getLibContainerLabel());
		fLibContainerDialogField.setButtonLabel(NewWizardMessages.NewContainerWizardPage_container_button);

		IFolder configFolder = javaProject.getProject().getFolder(DEFAULT_CONFIG_CONTAINER);
		IPackageFragmentRoot configContainer = javaProject.getPackageFragmentRoot(configFolder);
		setConfigContainer(configContainer, true);

		IFolder libFolder = javaProject.getProject().getFolder(DEFAULT_LIB_CONTAINER);
		setLibContainerRoot(libFolder, true);
	}

	protected String getConfigContainerLabel() {
		return CONFIG_CONTAINER_LABEL;
	}

	protected String getLibContainerLabel() {
		return LIB_CONTAINER_LABEL;
	}

	protected int getMaxFieldWidth() {
		return convertWidthInCharsToPixels(40);
	}

	protected void createConfigContainerControls(Composite parent, int nColumns) {
		fConfigContainerDialogField.doFillIntoGrid(parent, nColumns);
		LayoutUtil.setWidthHint(fConfigContainerDialogField.getTextControl(null), getMaxFieldWidth());
	}

	protected void createLibContainerControls(Composite parent, int nColumns) {
		fLibContainerDialogField.doFillIntoGrid(parent, nColumns);
		LayoutUtil.setWidthHint(fLibContainerDialogField.getTextControl(null), getMaxFieldWidth());
	}

	protected void setFocusOnConfigContainer() {
		fConfigContainerDialogField.setFocus();
	}

	// -------- ConfigContainerFieldAdapter --------
	private class ConfigContainerFieldAdapter implements IStringButtonAdapter, IDialogFieldListener {

		// -------- IStringButtonAdapter
		public void changeControlPressed(DialogField field) {
			configContainerChangeControlPressed(field);
		}

		// -------- IDialogFieldListener
		public void dialogFieldChanged(DialogField field) {
			configContainerDialogFieldChanged(field);
		}
	}

	private void configContainerChangeControlPressed(DialogField field) {
		if (field == fConfigContainerDialogField) {
			IPackageFragmentRoot root = chooseConfigContainer();
			if (root != null) {
				setConfigContainer(root, true);
			}
		}
	}

	private void configContainerDialogFieldChanged(DialogField field) {
		if (field == fConfigContainerDialogField) {
			fConfigContainerStatus = configContainerChanged();
		}
		handleFieldChanged(CONFIG_CONTAINER);
	}

	// -------- LibContainerFieldAdapter --------
	private class LibContainerFieldAdapter implements IStringButtonAdapter, IDialogFieldListener {

		public void changeControlPressed(DialogField field) {
			libContainerChangeControlPressed(field);
		}

		// -------- IDialogFieldListener
		public void dialogFieldChanged(DialogField field) {
			libContainerDialogFieldChanged(field);
		}
	}

	private void libContainerChangeControlPressed(DialogField field) {
		if (field == fLibContainerDialogField) {
			IFolder folder = chooseLibContainer();
			if (folder != null) {
				setLibContainerRoot(folder, true);
			}
		}
	}

	private void libContainerDialogFieldChanged(DialogField field) {
		if (field == fLibContainerDialogField) {
			fLibContainerStatus = libContainerChanged();
		}
		// tell all others
		handleFieldChanged(LIB_CONTAINER);
	}

	// ----------- validation ----------

	protected IStatus configContainerChanged() {
		StatusInfo status = new StatusInfo();

		String str = getConfigContainerText();
		if (str.length() == 0 && isValidPath(str))
			status.setError("Configuration folder must not be empty");
		else if (!isValidPath(str))
			status.setError("Path to configuration folder is not valid");

		return status;
	}

	protected IStatus libContainerChanged() {
		StatusInfo status = new StatusInfo();

		String str = getLibContainerText();
		if (str.length() == 0)
			status.setError("Library folder must not be empty");
		else if (!isValidPath(str))
			status.setError("Path to library folder is not valid");

		return status;
	}

	// -------- update message ----------------
	protected void handleFieldChanged(String fieldName) {
		if (CONFIG_CONTAINER.equals(fieldName))
			fLibContainerStatus = libContainerChanged();
		if (LIB_CONTAINER.equals(fieldName))
			fConfigContainerStatus = configContainerChanged();

		updateStatus(new IStatus[] { fConfigContainerStatus, fLibContainerStatus });
	}

	public IPackageFragmentRoot getConfigContainer() {
		String configContainerPath = getConfigContainerText();
		if (isValidPath(configContainerPath)) {
			IFolder folder = javaProject.getProject().getFolder(configContainerPath);
			return new Classpath(javaProject).addSourceFolder(folder);
		}
		return null;
	}

	public IFolder getLibContainer() {
		String configContainerPath = getLibContainerText();
		IFolder folder = null;
		if (isValidPath(configContainerPath)) {
			folder = javaProject.getProject().getFolder(configContainerPath);
			if(!folder.exists())
				createFolder(folder);
		}
		return folder;
	}

	private boolean createFolder(IFolder folder) {
		try {
			CoreUtility.createFolder(folder, true, true, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return folder.exists();
	}

	private boolean isValidPath(String path) {
		if (path.contains("."))
			return false;
		return new Path(path).isValidPath(path);
	}

	public String getConfigContainerText() {
		return fConfigContainerDialogField.getText();
	}

	public void setConfigContainer(IPackageFragmentRoot root, boolean canBeModified) {
		String str = (root == null) ? "" : root.getPath().makeRelative().removeFirstSegments(1).toString(); //$NON-NLS-1$
		fConfigContainerDialogField.setText(str);
		fConfigContainerDialogField.setEnabled(canBeModified);
	}

	public String getLibContainerText() {
		return fLibContainerDialogField.getText();
	}

	public void setLibContainerRoot(IFolder folder, boolean canBeModified) {
		String str = (folder == null) ? "" : folder.getProjectRelativePath().toString();
		fLibContainerDialogField.setText(str);
		fLibContainerDialogField.setEnabled(canBeModified);
	}

	/**
	 * Opens a selection dialog that allows to select a source container.
	 */
	protected IPackageFragmentRoot chooseConfigContainer() {
		BrowseSourceFolderDialog browser = new BrowseSourceFolderDialog(javaProject);
		return browser.openDialog(getShell());
	}

	protected IFolder chooseLibContainer() {
		BrowseFolderDialog browser = new BrowseFolderDialog(javaProject);
		return browser.openDialog(getShell());
	}

	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		int nColumns = 3;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		DialogField.createEmptySpace(composite);

		Label label = new Label(composite, SWT.WRAP);
		label.setText(PAGE_TITLE);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(60);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);

		DialogField.createEmptySpace(composite, 3);
		createConfigContainerControls(composite, nColumns);
		DialogField.createEmptySpace(composite, 3);
		createLibContainerControls(composite, nColumns);

		setControl(composite);
		Dialog.applyDialogFont(composite);
	}

}
