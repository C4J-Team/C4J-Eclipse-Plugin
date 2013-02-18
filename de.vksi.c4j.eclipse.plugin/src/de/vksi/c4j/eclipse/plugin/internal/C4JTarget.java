package de.vksi.c4j.eclipse.plugin.internal;

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
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

@SuppressWarnings("restriction")
public class C4JTarget {
	public ICompilationUnit targetCompilationUnit;
	private IDocument document;
	private IType target;

	public C4JTarget(IType target) {
		this.target = target;
		this.targetCompilationUnit = target.getCompilationUnit();
	}

	public void addImportsFor(IType contract) throws ValidateEditException, CoreException,
			MalformedTreeException, BadLocationException {
		IJavaElement container = contract.getParent();
		if (container instanceof ICompilationUnit) {
			container = container.getParent();

			ImportRewrite rewrite = StubUtility.createImportRewrite(targetCompilationUnit, true);
			if (!container.equals(targetCompilationUnit.getParent())) {
				rewrite.addImport(contract.getFullyQualifiedName('.'));
			}

			rewrite.addImport(IMPORT_CONTRACT_REFERENCE);

			TextEdit edits = rewrite.rewriteImports(null);

			applyEdits(edits);
		}
	}

	public void addContractReferenceAnnotation(IType contract) throws JavaModelException,
			MalformedTreeException, BadLocationException {
		ASTParser parser = ASTParser.newParser(ASTProvider.SHARED_AST_LEVEL);
		parser.setSource(targetCompilationUnit);
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

		applyEdits(edits);
	}
	
	private void applyEdits(TextEdit edits) throws JavaModelException, BadLocationException {
		this.document = new Document(target.getCompilationUnit().getSource());
		edits.apply(document);

		targetCompilationUnit.getBuffer().setContents(document.get());
	}

	public IType getType() {
		return target;
	}

}