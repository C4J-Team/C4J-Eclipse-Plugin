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

import de.vksi.c4j.eclipse.plugin.util.C4JContractTransformer;

@SuppressWarnings("restriction")
public class CreateContractWizardPage extends NewTypeWizardPage {
	private static final String SUPERTYPE_NOT_SET = "Superclass or Interface not set";
	
	public final static int INTERNAL_CONTRACT = 0;
	public final static int EXTERNAL_CONTRACT = 1;

	public final static int CONTRACT_STUB = 0;
	public final static int CONTRACT_WITH_TARGET_METHODS = 1;
	public final static int CONTRACT_WITH_ALL_TYPE_HIERARCHY_METHODS = 2;

	private final static String PAGE_NAME = "NewContractClassWizardPage";

	private static final String CREATE_INTERNAL_CONTRACT = "Create internal Contract (@"
			+ ANNOTATION_CONTRACT_REFERENCE + ")";
	private static final String CREATE_EXTERNAL_CONTRACT = "Create external Contract (@"
			+ ANNOTATION_CONTRACT + ")";

	private final static String ONLY_CONTRACT_STUB = "Create only Contract stub";
	private final static String ALL_METHODS_OF_CURRENT_TYPE = "Create Contract stubs for all methods of the target Type";
	private final static String ALL_METHODS_OF_FULL_TYPE_HIERARCHY = "Create Contract stubs for all methods of the target Type Hierachy";

	private SelectionButtonDialogFieldGroup fContractStubsButtons;
	private SelectionButtonDialogFieldGroup fContractTypeButtons;

	protected IStatus fSuperClassOrSuperInterfaceIsSetStatus;
	private IType target;

	public CreateContractWizardPage() {
		super(true, PAGE_NAME);

		setTitle("Contract Class");
		setDescription("Create a new Contract class");

		String[] contractTypeSelection = new String[] { CREATE_INTERNAL_CONTRACT, CREATE_EXTERNAL_CONTRACT };
		fContractTypeButtons = new SelectionButtonDialogFieldGroup(SWT.RADIO, contractTypeSelection, 1);
		fContractTypeButtons.setLabelText("Which kind of Contract would you like to create?");

		String[] stubSelections = new String[] { ONLY_CONTRACT_STUB, ALL_METHODS_OF_CURRENT_TYPE,
				ALL_METHODS_OF_FULL_TYPE_HIERARCHY };
		fContractStubsButtons = new SelectionButtonDialogFieldGroup(SWT.RADIO, stubSelections, 1);
		fContractStubsButtons.setLabelText("Which method stubs would you like to create?");
	}

	public CreateContractWizardPage(IType target) {
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

	@Override
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		fSuperClassOrSuperInterfaceIsSetStatus = superClassOrInterfaceChanged();
		doStatusUpdate();
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

	public void setContractTypeSelection(boolean internalContract, boolean externalContract,
			boolean canBeModified) {
		fContractTypeButtons.setSelection(INTERNAL_CONTRACT, internalContract);
		fContractTypeButtons.setSelection(EXTERNAL_CONTRACT, externalContract);
		fContractTypeButtons.setEnabled(canBeModified);
	}

	public void setContractStubSelection(boolean onlyContractStub, boolean contractWithTargetMethodStubs,
			boolean contractWithAllMethodStubsOfTypeHierarchy, boolean canBeModified) {
		fContractStubsButtons.setSelection(CONTRACT_STUB, onlyContractStub);
		fContractStubsButtons.setSelection(CONTRACT_WITH_TARGET_METHODS, contractWithTargetMethodStubs);
		fContractStubsButtons.setSelection(CONTRACT_WITH_ALL_TYPE_HIERARCHY_METHODS,
				contractWithAllMethodStubsOfTypeHierarchy);
		fContractStubsButtons.setEnabled(canBeModified);
	}
	
	private void doStatusUpdate() {
		IStatus[] status = new IStatus[] { fContainerStatus,
				isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus, //
				fTypeNameStatus, //
				fModifierStatus, //
				fSuperClassStatus, //
				fSuperInterfacesStatus, //
				fSuperClassOrSuperInterfaceIsSetStatus };

		updateStatus(status);
	}

	private IStatus superClassOrInterfaceChanged() {
		StatusInfo status = new StatusInfo();
		status.setOK();

		if (getSuperClass().isEmpty() && getSuperInterfaces().isEmpty())
			status.setError(SUPERTYPE_NOT_SET);

		return status;
	}

	private void createContractTypeSelectionControls(Composite composite, int nColumns) {
		Control labelControl = fContractTypeButtons.getLabelControl(composite);
		LayoutUtil.setHorizontalSpan(labelControl, nColumns);

		DialogField.createEmptySpace(composite);

		Control buttonGroup = fContractTypeButtons.getSelectionButtonsGroup(composite);
		LayoutUtil.setHorizontalSpan(buttonGroup, nColumns - 1);
	}

	private boolean isCreateExternalContract() {
		return fContractTypeButtons.isSelected(EXTERNAL_CONTRACT);
	}

	private void createMethodStubSelectionControls(Composite composite, int nColumns) {
		Control labelControl = fContractStubsButtons.getLabelControl(composite);
		LayoutUtil.setHorizontalSpan(labelControl, nColumns);

		DialogField.createEmptySpace(composite);

		Control buttonGroup = fContractStubsButtons.getSelectionButtonsGroup(composite);
		LayoutUtil.setHorizontalSpan(buttonGroup, nColumns - 1);
	}

	private boolean isCreateOnlyContractStub() {
		return fContractStubsButtons.isSelected(0);
	}

	private boolean isCreateAllMethodStubs() {
		return fContractStubsButtons.isSelected(1);
	}

	private boolean isCreateAllMethodStubsOfTypeHierarchy() {
		return fContractStubsButtons.isSelected(2);
	}

	@Override
	protected void createTypeMembers(IType type, ImportsManager imports, IProgressMonitor monitor)
			throws CoreException {

		C4JContractTransformer contract = new C4JContractTransformer(type);

		if (isCreateExternalContract())
			contract.addContractAnnotation(target);

		contract.addTargetMember();
		contract.addClassInvariant();
		
		if (!isCreateOnlyContractStub()) 
			createConstructorStubs(type, contract);

		if (isCreateAllMethodStubs()) 
			contract.addMethodStubs(getOverridableMethodsOfTarget(type));
		 else if (isCreateAllMethodStubsOfTypeHierarchy()) 
			contract.addMethodStubs(getAllOverridableMethods(type));

		contract.applyEdits();

		if (monitor != null)
			monitor.done();
	}

	private void createConstructorStubs(IType type, C4JContractTransformer contract)
			throws JavaModelException, CoreException {
		CompilationUnit fUnit = getAstRoot(type);
		ITypeBinding binding = ASTNodes.getTypeBinding(fUnit, type);
		
		IMethodBinding[] visibleConstructors = StubUtility2.getVisibleConstructors(binding, true, false);
		
		for (IMethodBinding constructor : visibleConstructors) {
			contract.addContructorStub(constructor);
		}
	}

	private List<IMethodBinding> getOverridableMethodsOfTarget(IType type) throws JavaModelException {
		List<IMethodBinding> methodsContainedInTarget = new ArrayList<IMethodBinding>();
		for (IMethodBinding method : getAllOverridableMethods(type)) {
			if (isTargetDeclaringClass(method))
				methodsContainedInTarget.add(method);
		}
		return methodsContainedInTarget;
	}

	private boolean isTargetDeclaringClass(IMethodBinding method) {
		return method.getDeclaringClass().getName().equals(target.getElementName());
	}

	private List<IMethodBinding> getAllOverridableMethods(IType type) throws JavaModelException {
		CompilationUnit fUnit = getAstRoot(type);
		ITypeBinding typeBinding = ASTNodes.getTypeBinding(fUnit, type);
		
		if (typeBinding != null) {
			final IMethodBinding[] methods = StubUtility2.getOverridableMethods(fUnit.getAST(), typeBinding,
					false);
			return getMethodsVisibleInTypeHierarchy(typeBinding, methods);
		} else
			return new ArrayList<IMethodBinding>();
	}

	private List<IMethodBinding> getMethodsVisibleInTypeHierarchy(ITypeBinding typeBinding,
			final IMethodBinding[] methods) {
		List<IMethodBinding> list = new ArrayList<IMethodBinding>(methods.length);
		for (IMethodBinding method : methods) {
			if (Bindings.isVisibleInHierarchy(method, typeBinding.getPackage()))
				list.add(method);
		}
		return list;
	}

	private CompilationUnit getAstRoot(IType type) {
		RefactoringASTParser parser = new RefactoringASTParser(ASTProvider.SHARED_AST_LEVEL);
		CompilationUnit fUnit = parser.parse(type.getCompilationUnit(), true);
		return fUnit;
	}
}
