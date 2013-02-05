package de.vksi.c4j.eclipse.plugin.util;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CLASS_INVARIANT;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CONTRACT;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_TARGET;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.IMPORT_CLASSINVARIANT;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.IMPORT_IGNORED;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.IMPORT_POST_CONDITIONS;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.IMPORT_PRE_CONDITIONS;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.IMPORT_TARGET;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.RETURN_IGNORED;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

@SuppressWarnings("restriction")
public class C4JContractModifier {
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

	public C4JContractModifier(IType contract) {
		this.contract = contract;
		init(contract);
	}

	private void init(IType contract) {
		ASTParser parser = ASTParser.newParser(ASTProvider.SHARED_AST_LEVEL);
		parser.setSource(contract.getCompilationUnit());
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
			e.printStackTrace();
		}
	}

	public void addC4JStandardImports() {
		addStaticImport(IMPORT_PRE_CONDITIONS, false);
		addStaticImport(IMPORT_POST_CONDITIONS, false);
		addStaticImport(IMPORT_IGNORED, false);
		addImport(IMPORT_TARGET);
		addImport(IMPORT_CLASSINVARIANT);
	}
	
	private void addImport(String qualifiedTypeName){
		importRewrite.addImport(qualifiedTypeName);
	}

	private void addStaticImport(String qualifiedTypeName, boolean isField){
		String simpleName = qualifiedTypeName.substring(qualifiedTypeName.lastIndexOf(".")+1, qualifiedTypeName.length());
		String declaringTypeName = qualifiedTypeName.replace("." + simpleName, "");
		importRewrite.addStaticImport(declaringTypeName, simpleName, isField);
	}
	
	public void addContractAnnotation() {

		ListRewrite listRewrite = rewriter.getListRewrite(typeDecl, TypeDeclaration.MODIFIERS2_PROPERTY);
				
		MarkerAnnotation contractRefAnnotation = ast.newMarkerAnnotation();
		contractRefAnnotation.setTypeName(ast.newSimpleName(ANNOTATION_CONTRACT));
		
		addImport("de.vksi.c4j.Contract");

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

		Type targetType = null;
		if (superclassType != null) {
			targetType = superclassType;
		} else if (!superInterfaceTypes.isEmpty()) {
			targetType = superInterfaceTypes.get(0);
		}

		Type newType = (Type) ASTNode.copySubtree(ast, targetType);
		targetDeclaration.setType(newType);

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
		Statement toDoComment = (Statement) rewriter.createStringPlaceholder(
				CLASS_INVARIANTS_TODO_COMMENT, ASTNode.EMPTY_STATEMENT);
		classInvariantMethodBlock.statements().add(toDoComment);
		classInvariantMethod.setBody(classInvariantMethodBlock);

		listRewrite.insertLast(classInvariantMethod, null);
	}
	
	public void addMethodStubs(IMethodBinding[] methods) throws JavaModelException, CoreException {

		CodeGenerationSettings settings = JavaPreferencesSettings.getCodeGenerationSettings(contract
				.getJavaProject());

		for (IMethodBinding method : methods) {
			MethodDeclaration methodStub = StubUtility2.createImplementationStub(contract
					.getCompilationUnit(), rewriter, importRewrite, null, method, typeDecl.getName()
					.toString(), settings, typeDecl.isInterface());

			Block methodBody = createMethodBody(methodStub);

			methodStub.setBody(methodBody);
			listRewrite.insertLast(methodStub, null);
		}
	}

	@SuppressWarnings("unchecked")
	private Block createMethodBody(MethodDeclaration methodStub) {
		Block methodBody = ast.newBlock();
		IfStatement ifPreconditionStatement = createPreCondition();
		methodBody.statements().add(ifPreconditionStatement);

		IfStatement ifPostconditionStatement = createPostCondition();
		methodBody.statements().add(ifPostconditionStatement);

		if (!isReturnTypeVoid(methodStub)) {
			ReturnStatement returnIgnoredStatement = createReturnIgnored();
			methodBody.statements().add(returnIgnoredStatement);
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
		toDoComment = (Statement) rewriter.createStringPlaceholder(
				POST_CONDITION_TODO_COMMENT, ASTNode.EMPTY_STATEMENT);
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
		Statement toDoComment = (Statement) rewriter.createStringPlaceholder(
				PRE_CONDITION_TODO_COMMENT, ASTNode.EMPTY_STATEMENT);
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
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//load new ast
		init(contract);
	}

	private void applyEdits(TextEdit edits) throws JavaModelException, BadLocationException {
		IDocument document = new Document(contract.getCompilationUnit().getSource());
		edits.apply(document);

		contract.getCompilationUnit().getBuffer().setContents(document.get());
	}
}
