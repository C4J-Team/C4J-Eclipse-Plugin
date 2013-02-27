package de.vksi.c4j.eclipse.plugin.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import de.vksi.c4j.eclipse.plugin.internal.C4JContractAnnotation;
import de.vksi.c4j.eclipse.plugin.internal.C4JContractReferenceAnnotation;
import de.vksi.c4j.eclipse.plugin.util.C4JTargetTransformer;

@SuppressWarnings("restriction")
public class ContractWizardRunner implements WizardRunner<IType> {
	private static final String CONTRACT = "Contract";
	private static final String DIALOG_TITLE = "New Contract Class";
	private static final String RIGHT_ARROW_BRACKET = ">";
	private static final String LEFT_ARROW_BRACKET = "<";

	private IType target;

	public ContractWizardRunner(IType type) {
		this.target = type;
	}

	public IType run() {
		StructuredSelection selection = new StructuredSelection(target.getCompilationUnit());

		ContractWizardPage page = new ContractWizardPage(target);
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

		if (createdType != null) {
			if (!isExternalContract(createdType)) {
				C4JTargetTransformer targetTransformer = new C4JTargetTransformer(target);
				targetTransformer.addImportsFor(createdType);
				targetTransformer.addContractReferenceAnnotation(createdType);
			}
		}

		return createdType;
	}

	private boolean isExternalContract(IType createdType) {
		return new C4JContractAnnotation(createdType).exists();
	}

	private void configureWizardPage(ContractWizardPage page) {
		fillInWizardPageName(page);
		selectContractType(page);
		fillInWizardPageSuperTypes(page);
	}

	private void selectContractType(ContractWizardPage page) {
		C4JContractReferenceAnnotation contractReference = new C4JContractReferenceAnnotation(target);
		if (contractReference.exists()) {
			page.setContractTypeSelection(false, true, false);
		}

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
