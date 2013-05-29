package de.vksi.c4j.eclipse.plugin.util;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CLASS_INVARIANT;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CONTRACT;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_TARGET;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.IMPORT_CLASSINVARIANT;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.IMPORT_CONTRACT_ANNOTATION;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.IMPORT_IGNORED;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.IMPORT_POST_CONDITIONS;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.IMPORT_PRE_CONDITIONS;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.IMPORT_TARGET;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.RETURN_IGNORED;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility2;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import de.vksi.c4j.eclipse.plugin.C4JEclipsePluginActivator;

@SuppressWarnings("restriction")
public class C4JContractTransformer {
	private static Logger logger = C4JEclipsePluginActivator.getLogManager().getLogger(C4JContractTransformer.class.getName());
	private static final String PRE_CONDITION_TODO_COMMENT = "// TODO: write preconditions if required";
	private static final String POST_CONDITION_TODO_COMMENT = "// TODO: write postconditions if required";
	private static final String CLASS_INVARIANTS_TODO_COMMENT = "// TODO: write invariants if required";
	private static final String CLASS_INVARIANT_METHOD = "classInvariant";
	private static final String TARGET_MEMBER = "target";

	private IType contract;
	private CompilationUnit astRoot;
	private AST ast;
	private ASTRewrite rewriter;
	private TypeDeclaration typeDecl;
	private ListRewrite listRewrite;
	private ImportRewrite importRewrite;

	public C4JContractTransformer(IType contract) {
		assert contract != null : "contract must not be null";
		this.contract = contract;
		init();
	}

	private void init() {
		ASTParser parser = ASTParser.newParser(ASTProvider.SHARED_AST_LEVEL);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(contract.getCompilationUnit());
		parser.setResolveBindings(false);
		astRoot = (CompilationUnit) parser.createAST(null);

		// create a ASTRewrite
		ast = astRoot.getAST();
		rewriter = ASTRewrite.create(ast);

		// for getting insertion position
		typeDecl = (TypeDeclaration) astRoot.types().get(0);

		// create ListRewrite
		listRewrite = rewriter.getListRewrite(typeDecl, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);

		try {
			importRewrite = StubUtility.createImportRewrite(contract.getCompilationUnit(), true);
		} catch (JavaModelException e) {
			logger.error("Could not create imports of " + contract.getElementName());
		}
	}

	private void addImport(String qualifiedTypeName) {
		importRewrite.addImport(qualifiedTypeName);
	}

	private void addStaticImport(String qualifiedTypeName, boolean isField) {
		String simpleName = qualifiedTypeName.substring(qualifiedTypeName.lastIndexOf(".") + 1,
				qualifiedTypeName.length());
		String declaringTypeName = qualifiedTypeName.replace("." + simpleName, "");
		importRewrite.addStaticImport(declaringTypeName, simpleName, isField);
	}

	public void addContractAnnotation() {
		ListRewrite listRewrite = rewriter.getListRewrite(typeDecl, TypeDeclaration.MODIFIERS2_PROPERTY);
		MarkerAnnotation contractRefAnnotation = ast.newMarkerAnnotation();
		contractRefAnnotation.setTypeName(ast.newSimpleName(ANNOTATION_CONTRACT));
		addImport(IMPORT_CONTRACT_ANNOTATION);
		listRewrite.insertFirst(contractRefAnnotation, null);
	}

	@SuppressWarnings("unchecked")
	public void addContractAnnotation(IType target) {
		if (target == null) {
			addContractAnnotation();
			return;
		}

		ListRewrite listRewrite = rewriter.getListRewrite(typeDecl, TypeDeclaration.MODIFIERS2_PROPERTY);

		NormalAnnotation contractRefAnnotation = ast.newNormalAnnotation();
		contractRefAnnotation.setTypeName(ast.newSimpleName(ANNOTATION_CONTRACT));

		MemberValuePair annotationValue = ast.newMemberValuePair();
		annotationValue.setName(ast.newSimpleName("forTarget"));
		TypeLiteral typeLiteral = ast.newTypeLiteral();
		typeLiteral.setType(ast.newSimpleType(ast.newSimpleName(target.getElementName())));
		annotationValue.setValue(typeLiteral);

		contractRefAnnotation.values().add(annotationValue);

		addImport(IMPORT_CONTRACT_ANNOTATION);

		listRewrite.insertFirst(contractRefAnnotation, null);

	}

	@SuppressWarnings("unchecked")
	public void addTargetMember() {
		VariableDeclarationFragment targetFragment = ast.newVariableDeclarationFragment();
		targetFragment.setName(ast.newSimpleName(TARGET_MEMBER));

		FieldDeclaration targetDeclaration = ast.newFieldDeclaration(targetFragment);

		MarkerAnnotation targetMarkerAnnotation = ast.newMarkerAnnotation();
		targetMarkerAnnotation.setTypeName(ast.newSimpleName(ANNOTATION_TARGET));
		targetDeclaration.modifiers().add(targetMarkerAnnotation);
		targetDeclaration.modifiers().add(ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD));

		Type superclassType = typeDecl.getSuperclassType();
		List<Type> superInterfaceTypes = typeDecl.superInterfaceTypes();

		//TODO: handle the case if both, supertype AND superinterface, are set
		Type targetType = null;
		if (superclassType != null) {
			targetType = superclassType;
		} else if (!superInterfaceTypes.isEmpty()) {
			targetType = superInterfaceTypes.get(0);
		}
		
		Type newType = (Type) ASTNode.copySubtree(ast, targetType);
		targetDeclaration.setType(newType);
		
		addImport(IMPORT_TARGET);
		listRewrite.insertFirst(targetDeclaration, null);
	}

	@SuppressWarnings("unchecked")
	public void addClassInvariant() {
		MethodDeclaration classInvariantMethod = ast.newMethodDeclaration();
		MarkerAnnotation invariantMarkerAnnotation = ast.newMarkerAnnotation();
		invariantMarkerAnnotation.setTypeName(ast.newSimpleName(ANNOTATION_CLASS_INVARIANT));
		classInvariantMethod.modifiers().add(invariantMarkerAnnotation);
		classInvariantMethod.modifiers().add(ast.newModifier(ModifierKeyword.PUBLIC_KEYWORD));
		classInvariantMethod.setReturnType2(ast.newPrimitiveType(PrimitiveType.VOID));
		classInvariantMethod.setName(ast.newSimpleName(CLASS_INVARIANT_METHOD));
		Block classInvariantMethodBlock = ast.newBlock();
		Statement toDoComment = (Statement) rewriter.createStringPlaceholder(CLASS_INVARIANTS_TODO_COMMENT,
				ASTNode.EMPTY_STATEMENT);
		classInvariantMethodBlock.statements().add(toDoComment);
		classInvariantMethod.setBody(classInvariantMethodBlock);

		addImport(IMPORT_CLASSINVARIANT);
		listRewrite.insertLast(classInvariantMethod, null);
	}

	public void addMethodStubs(List<IMethodBinding> methods) throws JavaModelException, CoreException {
		for (IMethodBinding method : methods) {
			addMethodStub(method);
		}
	}

	public void addMethodStub(IMethodBinding method) throws JavaModelException, CoreException {
		CodeGenerationSettings settings = JavaPreferencesSettings.getCodeGenerationSettings(contract
				.getJavaProject());

		MethodDeclaration methodStub = StubUtility2.createImplementationStub(contract.getCompilationUnit(),
				rewriter, importRewrite, null, method, typeDecl.getName().toString(), settings,
				typeDecl.isInterface());

		Block methodBody = createMethodBody(methodStub);

		methodStub.setBody(methodBody);
		
		listRewrite.insertLast(methodStub, null);
	}
	
	@SuppressWarnings("unchecked")
	public void addContructorStub(IMethodBinding method) throws JavaModelException, CoreException {
		CodeGenerationSettings settings = JavaPreferencesSettings.getCodeGenerationSettings(contract
				.getJavaProject());
		
		MethodDeclaration constructor = StubUtility2.createConstructorStub(contract.getCompilationUnit(), rewriter, importRewrite, null, method, typeDecl.getName().toString(), ModifierKeyword.PUBLIC_KEYWORD.toFlagValue(), true, true, settings);
		
		Block constructorBody = createMethodBody(constructor);
		
		SuperConstructorInvocation superContructorInvovation = ast.newSuperConstructorInvocation();
		
		List<SingleVariableDeclaration> svd = (List<SingleVariableDeclaration>) constructor.getStructuralProperty(MethodDeclaration.PARAMETERS_PROPERTY);
		for (SingleVariableDeclaration singleVariableDeclaration : svd) {
			SimpleName varName = ast.newSimpleName(singleVariableDeclaration.getName().toString());
			superContructorInvovation.arguments().add(varName);
		}
		
		constructorBody.statements().add(0, superContructorInvovation);
		
		constructor.setBody(constructorBody);
		listRewrite.insertLast(constructor, null); //TODO: insert after classInvariants
	}

	@SuppressWarnings("unchecked")
	private Block createMethodBody(MethodDeclaration methodStub) {
		Block methodBody = ast.newBlock();
		IfStatement ifPreconditionStatement = createPreCondition();
		methodBody.statements().add(ifPreconditionStatement);
		addStaticImport(IMPORT_PRE_CONDITIONS, false);
		
		IfStatement ifPostconditionStatement = createPostCondition();
		methodBody.statements().add(ifPostconditionStatement);
		addStaticImport(IMPORT_POST_CONDITIONS, false);

		if (!isReturnTypeVoid(methodStub)) {
			ReturnStatement returnIgnoredStatement = createReturnIgnored();
			methodBody.statements().add(returnIgnoredStatement);
			addStaticImport(IMPORT_IGNORED, false);
		}
		return methodBody;
	}

	@SuppressWarnings("unchecked")
	private IfStatement createPostCondition() {
		Statement toDoComment;
		Block thenStatementBlock;
		IfStatement ifPostconditionStatement = ast.newIfStatement();
		MethodInvocation postcondition = ast.newMethodInvocation();
		postcondition.setName(ast.newSimpleName("postCondition"));
		ifPostconditionStatement.setExpression(postcondition);
		toDoComment = (Statement) rewriter.createStringPlaceholder(POST_CONDITION_TODO_COMMENT,
				ASTNode.EMPTY_STATEMENT);
		thenStatementBlock = ast.newBlock();
		thenStatementBlock.statements().add(toDoComment);
		ifPostconditionStatement.setThenStatement(thenStatementBlock);
		return ifPostconditionStatement;
	}

	@SuppressWarnings("unchecked")
	private IfStatement createPreCondition() {
		IfStatement ifPreconditionStatement = ast.newIfStatement();
		MethodInvocation precondition = ast.newMethodInvocation();
		precondition.setName(ast.newSimpleName("preCondition"));
		ifPreconditionStatement.setExpression(precondition);
		Statement toDoComment = (Statement) rewriter.createStringPlaceholder(PRE_CONDITION_TODO_COMMENT,
				ASTNode.EMPTY_STATEMENT);
		Block thenStatementBlock = ast.newBlock();
		thenStatementBlock.statements().add(toDoComment);
		ifPreconditionStatement.setThenStatement(thenStatementBlock);
		return ifPreconditionStatement;
	}

	private ReturnStatement createReturnIgnored() {
		ReturnStatement returnIgnoredStatement = ast.newReturnStatement();
		MethodInvocation ignored = ast.newMethodInvocation();
		ignored.setName(ast.newSimpleName(RETURN_IGNORED));
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

	public void applyEdits() {
		try {
			TextEdit edits = rewriter.rewriteAST();
			applyEdits(edits);

			TextEdit importEdit = importRewrite.rewriteImports(null);
			applyEdits(importEdit);
			//TODO: implement undo functionality if creation/modification fails
		} catch (Exception e) {
			logger.error("Could not apply code manipulation to " + contract.getElementName(), e);
		} 
	}

	private void applyEdits(TextEdit edits) throws BadLocationException, CoreException {
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		IPath path = astRoot.getJavaElement().getPath();
		try {
			bufferManager.connect(path, LocationKind.NORMALIZE , null); 
			ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path, LocationKind.NORMALIZE);
			IDocument document = textFileBuffer.getDocument();
			edits.apply(document);
			
			textFileBuffer.commit(null, false); 

		} finally {
			bufferManager.disconnect(path, LocationKind.NORMALIZE, null);
		}
	}
}
