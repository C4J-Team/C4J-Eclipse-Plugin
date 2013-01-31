package de.vksi.c4j.eclipse.plugin.quickassist;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CONTRACT_REFERENCE;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.IMPORT_CONTRACT_REFERENCE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.corext.ValidateEditException;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

@SuppressWarnings("restriction")
public class C4JTarget {
	public ICompilationUnit compilationUnit;
	private IType target;
	private IDocument document;

	public C4JTarget(IType target, IDocument document) {
		this.target = target;
		this.document = document;
		this.compilationUnit = target.getCompilationUnit();
	}
	
	public void addImportsFor(IType createdType) throws ValidateEditException, CoreException {
	IJavaElement container = createdType.getParent();
	if (container instanceof ICompilationUnit) {
		container = container.getParent();

		ImportRewrite rewrite = StubUtility.createImportRewrite(compilationUnit, true);
		if (!container.equals(compilationUnit.getParent())) {
			rewrite.addImport(createdType.getFullyQualifiedName('.'));
		}

		rewrite.addImport(IMPORT_CONTRACT_REFERENCE);

		JavaModelUtil.applyEdit(compilationUnit, rewrite.rewriteImports(null), false, null);
	}
}

	public void addContractReferenceAnnotation(IType contract)
			throws JavaModelException, MalformedTreeException, BadLocationException {
		ASTParser parser = ASTParser.newParser(ASTProvider.SHARED_AST_LEVEL);
		parser.setSource(compilationUnit);
		CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);
		
		AST ast = astRoot.getAST();
		ASTRewrite rewriter = ASTRewrite.create(ast);

		TypeDeclaration typeDecl = (TypeDeclaration) astRoot.types().get(0);
		ListRewrite listRewrite = rewriter.getListRewrite(typeDecl, TypeDeclaration.MODIFIERS2_PROPERTY);

		SingleMemberAnnotation contractRefAnnotation = ast.newSingleMemberAnnotation();
		contractRefAnnotation.setTypeName(ast.newSimpleName(ANNOTATION_CONTRACT_REFERENCE));
		TypeLiteral typeLiteral = ast.newTypeLiteral();
		typeLiteral.setType(ast.newSimpleType(ast.newSimpleName(contract.getElementName())));
		contractRefAnnotation.setValue(typeLiteral);

		listRewrite.insertFirst(contractRefAnnotation, null);

		TextEdit edits = rewriter.rewriteAST();
		
		
		edits.apply(document);

		compilationUnit.getBuffer().setContents(document.get());
	}
	
}