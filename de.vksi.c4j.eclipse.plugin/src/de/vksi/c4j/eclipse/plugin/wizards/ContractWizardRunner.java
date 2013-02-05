package de.vksi.c4j.eclipse.plugin.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
public class ContractWizardRunner {
	private static final String CONTRACT = "Contract";
	private static final String DIALOG_TITLE = "New Contract Class";
	private static final String RIGHT_ARROW_BRACKET = ">";
	private static final String LEFT_ARROW_BRACKET = "<";
	
	private IType target;

	public ContractWizardRunner(IType type){
		this.target = type;
	}
	
	public IType runWizard(){
		StructuredSelection selection = new StructuredSelection(target.getCompilationUnit());

		ContractWizardPage page = new ContractWizardPage();
		page.init(selection);

		ContractCreationWizard wizard = new ContractCreationWizard(page, true);
		wizard.init(JavaPlugin.getDefault().getWorkbench(), selection);

		Shell shell = JavaPlugin.getActiveWorkbenchShell();
		WizardDialog dialog = new WizardDialog(shell, wizard);

		PixelConverter converter = new PixelConverter(JFaceResources.getDialogFont());
		dialog.setMinimumPageSize(converter.convertWidthInCharsToPixels(70),
				converter.convertHeightInCharsToPixels(20));
		dialog.create();
		dialog.getShell().setText(DIALOG_TITLE);

		configureWizardPage(page);
		IType createdType = null;

		if (dialog.open() == Window.OK) {
			createdType = (IType) wizard.getCreatedElement();
		}
		return createdType;
	}
	
	private void configureWizardPage(ContractWizardPage page) {
		fillInWizardPageName(page);
		fillInWizardPageSuperTypes(page);
	}

	private void fillInWizardPageName(ContractWizardPage page) {
		String contractName = getFullyQualifiedName(target);
		page.setTypeName(contractName, true);
	}

	private void fillInWizardPageSuperTypes(ContractWizardPage page) {
		try {
			if (target.isInterface()) {
				List<String> interfacesNames = new ArrayList<String>();
				interfacesNames.add(target.getFullyQualifiedParameterizedName());
				page.setSuperInterfaces(interfacesNames, true);
			} else {
				page.setSuperClass(target.getFullyQualifiedParameterizedName(), true);
			}
		} catch (JavaModelException e) {
			page.setSuperInterfaces(new ArrayList<String>(), true);
			page.setSuperClass("", true);
		}
	}
	
	private String getFullyQualifiedName(IType type) {
		String param = "";
		try {
			String typeName = type.getFullyQualifiedParameterizedName();
			if (isParameterized(typeName)) {
				param = getParameter(typeName);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		return type.getElementName() + CONTRACT + param;
	}
	
	private String getParameter(String typeName) {
		String param = typeName.substring(typeName.indexOf(LEFT_ARROW_BRACKET),
				typeName.indexOf(RIGHT_ARROW_BRACKET) + 1);
		return param;
	}
	
	private boolean isParameterized(String fullyQualifiedName) {
		return fullyQualifiedName.contains(LEFT_ARROW_BRACKET);
	}
	
}
