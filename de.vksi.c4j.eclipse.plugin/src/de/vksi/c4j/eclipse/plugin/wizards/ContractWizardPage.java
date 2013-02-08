package de.vksi.c4j.eclipse.plugin.wizards;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CONTRACT;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CONTRACT_REFERENCE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility2;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.jdt.internal.corext.refactoring.util.RefactoringASTParser;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogFieldGroup;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

import de.vksi.c4j.eclipse.plugin.util.C4JContractModifier;

@SuppressWarnings("restriction")
public class ContractWizardPage extends NewTypeWizardPage {

	public final static int INTERNAL_CONTRACT = 0;
	public final static int EXTERNAL_CONTRACT = 1;

	public final static int CONTRACT_STUB = 0;
	public final static int CONTRACT_WITH_TARGET_METHODS = 1;
	public final static int CONTRACT_WITH_ALL_TYPE_HIERARCHY_METHODS = 2;

	private final static String PAGE_NAME = "NewContractClassWizardPage";

	private static final String CREATE_INTERNAL_CONTRACT = "Create internal Contract (@" + ANNOTATION_CONTRACT_REFERENCE + ")";
	private static final String CREATE_EXTERNAL_CONTRACT = "Create external Contract (@" + ANNOTATION_CONTRACT + ")";

	private final static String ONLY_CONTRACT_STUB = "Create only Contract stub";
	private final static String ALL_METHODS_OF_CURRENT_TYPE = "Create Contract stubs for all methods of the target Type";
	private final static String ALL_METHODS_OF_FULL_TYPE_HIERARCHY = "Create Contract stubs for all methods of the target Type Hierachy";

	private SelectionButtonDialogFieldGroup fContractStubsButtons;
	private SelectionButtonDialogFieldGroup fContractTypeButtons;

	protected IStatus fSuperClassOrSuperInterfaceIsSetStatus;
	private IType target;

	public ContractWizardPage() {
		super(true, PAGE_NAME);

		setTitle("Contract Class");
		setDescription("Create a new Contract class");

		String[] contractTypeSelection = new String[] {CREATE_INTERNAL_CONTRACT, CREATE_EXTERNAL_CONTRACT };
		fContractTypeButtons = new SelectionButtonDialogFieldGroup(SWT.RADIO, contractTypeSelection, 1);
		fContractTypeButtons.setLabelText("Which method stubs would you like to create?");

		String[] stubSelections = new String[] { ONLY_CONTRACT_STUB, ALL_METHODS_OF_CURRENT_TYPE, ALL_METHODS_OF_FULL_TYPE_HIERARCHY };
		fContractStubsButtons = new SelectionButtonDialogFieldGroup(SWT.RADIO, stubSelections, 1);
		fContractStubsButtons.setLabelText("Which method stubs would you like to create?");
	}

	public ContractWizardPage(IType target) {
		this();
		this.target = target;
	}

	public void init(IStructuredSelection selection) {
		IJavaElement jelem = getInitialJavaElement(selection);
		initContainerPage(jelem);
		initTypePage(jelem);
		doStatusUpdate();

		setModifiers(Flags.AccPublic, false);
		setContractTypeSelection(true, false, true);
		setContractStubSelection(false, true, false, true);
	}

	private void doStatusUpdate() {
		IStatus[] status = new IStatus[] {
				fContainerStatus,
				isEnclosingTypeSelected() ? fEnclosingTypeStatus
						: fPackageStatus, fTypeNameStatus, fModifierStatus,
				fSuperClassStatus, fSuperInterfacesStatus,
				fSuperClassOrSuperInterfaceIsSetStatus };

		// the mode severe status will be displayed and the OK button
		// enabled/disabled.
		updateStatus(status);
	}

	@Override
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);

		fSuperClassOrSuperInterfaceIsSetStatus = superClassOrInterfaceChanged();

		doStatusUpdate();
	}

	private IStatus superClassOrInterfaceChanged() {
		StatusInfo status = new StatusInfo();
		status.setOK();

		if (getSuperClass().isEmpty() && getSuperInterfaces().isEmpty()) {
			status.setError("Superclass or Interface could not be set");
			return status;
		}

		return status;
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		// pick & choose the wanted UI components
		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);

		createSeparator(composite, nColumns);

		createTypeNameControls(composite, nColumns);
		createModifierControls(composite, nColumns);

		createSuperClassControls(composite, nColumns);
		createSuperInterfacesControls(composite, nColumns);
		
		createSeparator(composite, nColumns);

		createContractTypeSelectionControls(composite, nColumns);
		
		DialogField.createEmptySpace(composite);
		
		createMethodStubSelectionControls(composite, nColumns);

		setControl(composite);

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(composite, IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible)
			setFocus();
	}

	private void createContractTypeSelectionControls(Composite composite,
			int nColumns) {
		Control labelControl = fContractTypeButtons.getLabelControl(composite);
		LayoutUtil.setHorizontalSpan(labelControl, nColumns);

		DialogField.createEmptySpace(composite);
		
		Control buttonGroup = fContractTypeButtons
				.getSelectionButtonsGroup(composite);
		LayoutUtil.setHorizontalSpan(buttonGroup, nColumns - 1);
	}

	private boolean isCreateExternalContract() {
		return fContractTypeButtons.isSelected(EXTERNAL_CONTRACT);
	}

	public void setContractTypeSelection(boolean internalContract, boolean externalContract, boolean canBeModified) {
		fContractTypeButtons.setSelection(INTERNAL_CONTRACT, internalContract);
		fContractTypeButtons.setSelection(EXTERNAL_CONTRACT, externalContract);
		fContractTypeButtons.setEnabled(canBeModified);
	}

	private void createMethodStubSelectionControls(Composite composite,
			int nColumns) {
		Control labelControl = fContractStubsButtons.getLabelControl(composite);
		LayoutUtil.setHorizontalSpan(labelControl, nColumns);

		DialogField.createEmptySpace(composite);

		Control buttonGroup = fContractStubsButtons
				.getSelectionButtonsGroup(composite);
		LayoutUtil.setHorizontalSpan(buttonGroup, nColumns - 1);
	}

	private boolean isCreateAllMethodStubs() {
		return fContractStubsButtons.isSelected(1);
	}

	private boolean isCreateAllMethodStubsOfTypeHierarchy() {
		return fContractStubsButtons.isSelected(2);
	}

	public void setContractStubSelection(boolean onlyContractStub, boolean contractWithTargetMethodStubs, boolean contractWithAllMethodStubsOfTypeHierarchy, boolean canBeModified) {
		fContractStubsButtons.setSelection(CONTRACT_STUB, onlyContractStub);
		fContractStubsButtons.setSelection(CONTRACT_WITH_TARGET_METHODS, contractWithTargetMethodStubs);
		fContractStubsButtons.setSelection(CONTRACT_WITH_ALL_TYPE_HIERARCHY_METHODS, contractWithAllMethodStubsOfTypeHierarchy);
		fContractStubsButtons.setEnabled(canBeModified);
	}

	@Override
	protected void createTypeMembers(IType type, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {

		C4JContractModifier contract = new C4JContractModifier(type);

		contract.addC4JStandardImports();
		
		if(isCreateExternalContract())
			contract.addContractAnnotation(target);
		
		contract.addTargetMember();
		contract.addClassInvariant();

		if (isCreateAllMethodStubs()) {
			List<IMethodBinding> methodsContainedInTarget = getOnlyMethodsContainedInTarget(type);
			contract.addMethodStubs(methodsContainedInTarget
					.toArray(new IMethodBinding[methodsContainedInTarget.size()]));
		} else if (isCreateAllMethodStubsOfTypeHierarchy()) {
			IMethodBinding[] methods = getAllMethodsOfTypeHierarchy(type);
			contract.addMethodStubs(methods);
		}

		contract.applyEdits();

		if (monitor != null) {
			monitor.done();
		}
	}

	private IMethodBinding[] getAllMethodsOfTypeHierarchy(IType type) {
		RefactoringASTParser parser = new RefactoringASTParser(
				ASTProvider.SHARED_AST_LEVEL);
		CompilationUnit fUnit = parser.parse(type.getCompilationUnit(), true);
		ITypeBinding binding;
		try {
			binding = ASTNodes.getTypeBinding(fUnit, type);
			IMethodBinding[] overridable = null;
			if (binding != null) {
				final IPackageBinding pack = binding.getPackage();
				final IMethodBinding[] methods = StubUtility2
						.getOverridableMethods(fUnit.getAST(), binding, false);
				List<IMethodBinding> list = new ArrayList<IMethodBinding>(
						methods.length);
				for (int index = 0; index < methods.length; index++) {
					final IMethodBinding cur = methods[index];
					if (Bindings.isVisibleInHierarchy(cur, pack))
						list.add(cur);
				}
				overridable = list.toArray(new IMethodBinding[list.size()]);
			} else
				overridable = new IMethodBinding[] {};

			return overridable;
		} catch (JavaModelException e) {
			e.printStackTrace();
			return new IMethodBinding[] {};
		}
	}

	private List<IMethodBinding> getOnlyMethodsContainedInTarget(IType type)
			throws JavaModelException {
		String superTypeName = type.getSuperclassName() != null ? type
				.getSuperclassName() : type.getSuperInterfaceNames()[0];
		List<IMethodBinding> methodsContainedInTarget = new ArrayList<IMethodBinding>();
		for (IMethodBinding method : getAllMethodsOfTypeHierarchy(type)) {
			if (method.getDeclaringClass().getName().equals(superTypeName))
				methodsContainedInTarget.add(method);
		}
		return methodsContainedInTarget;
	}
}
