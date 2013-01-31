package de.vksi.c4j.eclipse.plugin.quickassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.ValidateEditException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.MalformedTreeException;

import de.vksi.c4j.eclipse.plugin.wizards.NewContractClassCreationWizard;
import de.vksi.c4j.eclipse.plugin.wizards.NewContractClassWizardPage;

@SuppressWarnings("restriction")
public class ContractCreator {
	private static final String CONTRACT = "Contract";
	private static final String RIGHT_ARROW_BRACKET = ">";
	private static final String LEFT_ARROW_BRACKET = "<";
	private static final String DIALOG_TITLE = "New Contract Class";

	private IInvocationContext context;
	private ICompilationUnit compilationUnit;

	public ContractCreator(IInvocationContext context) {
		this.context = context;
		this.compilationUnit = context.getCompilationUnit();
	}

	public void createContractFor(IDocument document) {
		IType createdType = callNewContractClassWizard();

		if (createdType != null) {
			try {
				IType type = compilationUnit.getType(context.getCoveringNode().toString());
				C4JTarget target = new C4JTarget(type, document);
				target.addImportsFor(createdType);
				target.addContractReferenceAnnotation(createdType);
				return;
			} catch (ValidateEditException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (MalformedTreeException e) {
				e.printStackTrace();
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	private IType callNewContractClassWizard() {
		StructuredSelection selection = new StructuredSelection(compilationUnit);

		NewContractClassWizardPage page = new NewContractClassWizardPage();
		page.init(selection);

		NewContractClassCreationWizard wizard = new NewContractClassCreationWizard(page, true);
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

	private void configureWizardPage(NewContractClassWizardPage page) {
		fillInWizardPageName(page);
		fillInWizardPageSuperTypes(page);
	}

	private void fillInWizardPageName(NewContractClassWizardPage page) {
		IType type = compilationUnit.getType(context.getCoveringNode().toString());
		String contractName = getFullyQualifiedName(type);
		page.setTypeName(contractName, true);
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

	private void fillInWizardPageSuperTypes(NewContractClassWizardPage page) {
		IType type = compilationUnit.getType(context.getCoveringNode().toString());

		// TODO: contract von methodenebene erstellen -> if-abfrage muss
		// angepasst werden -> covering node beachten

		try {
			if (type.isInterface()) {
				List<String> interfacesNames = new ArrayList<String>();
				interfacesNames.add(type.getFullyQualifiedParameterizedName());
				page.setSuperInterfaces(interfacesNames, true);
			} else {
				page.setSuperClass(type.getFullyQualifiedParameterizedName(), true);
			}
		} catch (JavaModelException e) {
			page.setSuperInterfaces(new ArrayList<String>(), true);
			page.setSuperClass("", true);
		}
	}
}
