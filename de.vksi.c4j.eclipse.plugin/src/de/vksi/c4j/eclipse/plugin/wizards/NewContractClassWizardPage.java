package de.vksi.c4j.eclipse.plugin.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility2;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.jdt.internal.corext.refactoring.util.RefactoringASTParser;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogFieldGroup;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("restriction")
public class NewContractClassWizardPage extends NewTypeWizardPage {

	private final static String PAGE_NAME = "NewContractClassWizardPage";

	private final static String ONLY_CONTRACT_STUB = "Create contract class without method stubs";
	private final static String ALL_METHODS_OF_CURRENT_TYPE = "Create contract stubs for all methods of the current Type";
	private final static String ALL_METHODS_OF_FULL_TYPE_HIERARCHY = "Create contract stubs for all methods of the current Type Hierachy";

	private SelectionButtonDialogFieldGroup fContractStubsButtons;

	protected IStatus fSuperClassOrSuperInterfaceIsSetStatus;

	/**
	 * Creates a new <code>NewClassWizardPage</code>
	 */
	public NewContractClassWizardPage() {
		super(true, PAGE_NAME);

		setTitle("Contract Class");
		setDescription("Create a new Contract class");

		String[] stubSelections = new String[] { ONLY_CONTRACT_STUB, ALL_METHODS_OF_CURRENT_TYPE, ALL_METHODS_OF_FULL_TYPE_HIERARCHY};
		fContractStubsButtons = new SelectionButtonDialogFieldGroup(SWT.RADIO, stubSelections, 1);
		fContractStubsButtons.setLabelText("Which method stubs would you like to create?");
	}


	/**
	 * The wizard owning this page is responsible for calling this method with
	 * the current selection. The selection is used to initialize the fields of
	 * the wizard page.
	 * 
	 * @param selection
	 *            used to initialize the fields
	 */
	public void init(IStructuredSelection selection) {
		IJavaElement jelem = getInitialJavaElement(selection);
		initContainerPage(jelem);
		initTypePage(jelem);
		doStatusUpdate();

		boolean createContractForAllMethods = true;
		IDialogSettings dialogSettings = getDialogSettings();
		if (dialogSettings != null) {
			IDialogSettings section = dialogSettings.getSection(PAGE_NAME);
			if (section != null) {
				createContractForAllMethods = section.getBoolean(ONLY_CONTRACT_STUB);
			}
		}

		setContractStubSelection(createContractForAllMethods, true);
	}

	private void doStatusUpdate() {
		// status of all used components
		IStatus[] status = new IStatus[] { fContainerStatus,
				isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus, fTypeNameStatus,
				fModifierStatus, fSuperClassStatus, fSuperInterfacesStatus,
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
		//no contract support for nested classes
		// createEnclosingTypeControls(composite, nColumns);

		createSeparator(composite, nColumns);

		createTypeNameControls(composite, nColumns);
		createModifierControls(composite, nColumns);

		createSuperClassControls(composite, nColumns);
		createSuperInterfacesControls(composite, nColumns);

		createMethodStubSelectionControls(composite, nColumns);

		// createCommentControls(composite, nColumns);
		// enableCommentControl(true);

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

	public void setContractStubSelection(boolean createContractForAllMethods, boolean canBeModified) {
		fContractStubsButtons.setSelection(0, createContractForAllMethods);
		fContractStubsButtons.setEnabled(canBeModified);
	}


	@Override
	protected void createTypeMembers(IType type, ImportsManager imports, IProgressMonitor monitor)
			throws CoreException {
		ASTParser parser = ASTParser.newParser(ASTProvider.SHARED_AST_LEVEL);
		parser.setSource(type.getCompilationUnit());
		CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);
		
		// create a ASTRewrite
		AST ast = astRoot.getAST();
		ASTRewrite rewriter = ASTRewrite.create(ast);
		
		// for getting insertion position
		TypeDeclaration typeDecl = (TypeDeclaration) astRoot.types().get(0);
		
		// create ListRewrite
		ListRewrite listRewrite = rewriter.getListRewrite(typeDecl,
				TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		
		addImports(imports);
		
		addTargetMember(ast, typeDecl, listRewrite);
		
		addClassInvariant(ast, rewriter, listRewrite);

		if(isCreateOnlyContractStub()){
			System.out.println("ONLY CONTRACT");
		}else if(isCreateAllMethodStubs()){
			System.out.println("ALL METHODS");
			IMethodBinding[] methods = getMethods(type);
			addMethodStubs(type, methods, ast, rewriter, typeDecl, listRewrite);
		}else if(isCreateAllMethodStubsOfTypeHierarchy()){
			System.out.println("ALL METHODS OF HIERARCHY");
			IMethodBinding[] methods = getMethods(type);
			addMethodStubs(type, methods, ast, rewriter, typeDecl, listRewrite);
		}

		TextEdit edits = rewriter.rewriteAST();

		// apply the text edits to the compilation unit
		Document document = new Document(type.getCompilationUnit().getSource());

		try {
			edits.apply(document);
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		// this is the code for adding statements
		type.getCompilationUnit().getBuffer().setContents(document.get());

		// .getDeclaringClass() um classe heraus zu finden

		IDialogSettings dialogSettings = getDialogSettings();
		if (dialogSettings != null) {
			IDialogSettings section = dialogSettings.getSection(PAGE_NAME);
			if (section == null) {
				section = dialogSettings.addNewSection(PAGE_NAME);
			}
			section.put(ONLY_CONTRACT_STUB, isCreateOnlyContractStub());
		}

		if (monitor != null) {
			monitor.done();
		}
	}


	private void addMethodStubs(IType type, IMethodBinding[] methods, AST ast, ASTRewrite rewriter, TypeDeclaration typeDecl,
			ListRewrite listRewrite) throws JavaModelException, CoreException {
		
		ImportRewrite impRewrite = StubUtility.createImportRewrite(type.getCompilationUnit(), true);

		CodeGenerationSettings settings = JavaPreferencesSettings.getCodeGenerationSettings(type
				.getJavaProject());

		for (IMethodBinding method : methods) {
			MethodDeclaration methodStub = StubUtility2.createImplementationStub(type.getCompilationUnit(),
					rewriter, impRewrite, null, method, typeDecl.getName().toString(), settings,
					typeDecl.isInterface());

			Block methodBody = createMethodBody(ast, rewriter, methodStub);

			methodStub.setBody(methodBody);
			listRewrite.insertLast(methodStub, null);
		}
	}


	private Block createMethodBody(AST ast, ASTRewrite rewriter, MethodDeclaration methodStub) {
		Block methodBody = ast.newBlock();
		IfStatement ifPreconditionStatement = createPreCondition(ast, rewriter);
		methodBody.statements().add(ifPreconditionStatement);

		IfStatement ifPostconditionStatement = createPostCondition(ast, rewriter);
		methodBody.statements().add(ifPostconditionStatement);
		
		if (!isReturnTypeVoid(methodStub)) {
			ReturnStatement returnIgnoredStatement = createReturnIgnored(ast);
			methodBody.statements().add(returnIgnoredStatement);
		}
		return methodBody;
	}


	private ReturnStatement createReturnIgnored(AST ast) {
		ReturnStatement returnIgnoredStatement = ast.newReturnStatement();
		MethodInvocation ignored = ast.newMethodInvocation();
		ignored.setName(ast.newSimpleName("ignored"));
		returnIgnoredStatement.setExpression(ignored);
		return returnIgnoredStatement;
	}


	private boolean isReturnTypeVoid(MethodDeclaration methodStub) {
		Type returnType = methodStub.getReturnType2();
		if (returnType.isPrimitiveType()) {
			PrimitiveType primitivReturnType = (PrimitiveType) returnType;
			return primitivReturnType.getPrimitiveTypeCode().equals(PrimitiveType.VOID);
		}
		return false;
	}


	private IfStatement createPostCondition(AST ast, ASTRewrite rewriter) {
		Statement toDoComment;
		Block thenStatementBlock;
		IfStatement ifPostconditionStatement = ast.newIfStatement();
		MethodInvocation postcondition = ast.newMethodInvocation();
		postcondition.setName(ast.newSimpleName("postCondition"));
		ifPostconditionStatement.setExpression(postcondition);
		toDoComment = (Statement) rewriter.createStringPlaceholder(
				"// TODO: write postconditions if required", ASTNode.EMPTY_STATEMENT);
		thenStatementBlock = ast.newBlock();
		thenStatementBlock.statements().add(toDoComment);
		ifPostconditionStatement.setThenStatement(thenStatementBlock);
		return ifPostconditionStatement;
	}


	private IfStatement createPreCondition(AST ast, ASTRewrite rewriter) {
		IfStatement ifPreconditionStatement = ast.newIfStatement();
		MethodInvocation precondition = ast.newMethodInvocation();
		precondition.setName(ast.newSimpleName("preCondition"));
		ifPreconditionStatement.setExpression(precondition);
		Statement toDoComment = (Statement) rewriter.createStringPlaceholder(
				"// TODO: write preconditions if required", ASTNode.EMPTY_STATEMENT);
		Block thenStatementBlock = ast.newBlock();
		thenStatementBlock.statements().add(toDoComment);
		ifPreconditionStatement.setThenStatement(thenStatementBlock);
		return ifPreconditionStatement;
	}


	private void addClassInvariant(AST ast, ASTRewrite rewriter, ListRewrite listRewrite) {
		// ++++++++++++++++++++++create class invariant method
		MethodDeclaration classInvariantMethod = ast.newMethodDeclaration();
		MarkerAnnotation invariantMarkerAnnotation = ast.newMarkerAnnotation();
		invariantMarkerAnnotation.setTypeName(ast.newSimpleName("ClassInvariant"));
		classInvariantMethod.modifiers().add(invariantMarkerAnnotation);
		classInvariantMethod.modifiers().add(ast.newModifier(ModifierKeyword.PUBLIC_KEYWORD));
		classInvariantMethod.setReturnType2(ast.newPrimitiveType(PrimitiveType.VOID));
		classInvariantMethod.setName(ast.newSimpleName("classInvariant"));
		Block classInvariantMethodBlock = ast.newBlock();
		Statement toDoComment = (Statement) rewriter.createStringPlaceholder(
				"// TODO: write invariants if required", ASTNode.EMPTY_STATEMENT);
		classInvariantMethodBlock.statements().add(toDoComment);
		classInvariantMethod.setBody(classInvariantMethodBlock);
		
		listRewrite.insertLast(classInvariantMethod, null);
	}


	private void addTargetMember(AST ast, TypeDeclaration typeDecl, ListRewrite listRewrite) {
		// ++++++++++++++++++++++add target member
		VariableDeclarationFragment targetFragment = ast.newVariableDeclarationFragment();
		targetFragment.setName(ast.newSimpleName("target"));
		
		FieldDeclaration targetDeclaration = ast.newFieldDeclaration(targetFragment);
		
		MarkerAnnotation targetMarkerAnnotation = ast.newMarkerAnnotation();
		targetMarkerAnnotation.setTypeName(ast.newSimpleName("Target"));
		targetDeclaration.modifiers().add(targetMarkerAnnotation);
		targetDeclaration.modifiers().add(ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD));
		
		Type superclassType = typeDecl.getSuperclassType();
		List<Type> superInterfaceTypes = typeDecl.superInterfaceTypes();
		
		Type targetType = null;
		if(superclassType != null){
			targetType = superclassType;
		}else if(!superInterfaceTypes.isEmpty()){
			targetType = superInterfaceTypes.get(0);
		}
		
		Type newType = (Type) ASTNode.copySubtree(ast, targetType);
		targetDeclaration.setType(newType);
		
		listRewrite.insertFirst(targetDeclaration, null);
	}


	private void addImports(ImportsManager imports) {
		// add c4j imports
		imports.addStaticImport("de.vksi.c4j.Condition", "preCondition", false);
		imports.addStaticImport("de.vksi.c4j.Condition", "postCondition", false);
		imports.addStaticImport("de.vksi.c4j.Condition", "ignored", false);
		imports.addImport("de.vksi.c4j.Target");
		imports.addImport("de.vksi.c4j.ClassInvariant");
	}

	private IMethodBinding[] getMethods(IType type) {
		RefactoringASTParser parser = new RefactoringASTParser(ASTProvider.SHARED_AST_LEVEL);
		CompilationUnit fUnit = parser.parse(type.getCompilationUnit(), true);
		ITypeBinding binding;
		try {
			binding = ASTNodes.getTypeBinding(fUnit, type);

			List<IMethodBinding> toImplement = new ArrayList<IMethodBinding>();
			IMethodBinding[] overridable = null;
			if (binding != null) {
				final IPackageBinding pack = binding.getPackage();
				final IMethodBinding[] methods = StubUtility2.getOverridableMethods(fUnit.getAST(), binding,
						false);
				List<IMethodBinding> list = new ArrayList<IMethodBinding>(methods.length);
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
	
}


