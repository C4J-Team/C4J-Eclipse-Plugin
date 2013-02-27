package de.vksi.c4j.eclipse.plugin.util;

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
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

@SuppressWarnings("restriction")
public class C4JTargetTransformer {
	public ICompilationUnit targetCompilationUnit;
	private IDocument document;
	private IType target;

	public C4JTargetTransformer(IType target) {
		this.target = target;
		this.targetCompilationUnit = target.getCompilationUnit();
	}

	public void addImportsFor(IType contract) {
		IJavaElement container = contract.getParent();
		if (container instanceof ICompilationUnit) {
			container = container.getParent();

			ImportRewrite rewrite;
			try {
				rewrite = StubUtility.createImportRewrite(targetCompilationUnit, true);
				if (!container.equals(targetCompilationUnit.getParent())) {
					rewrite.addImport(contract.getFullyQualifiedName('.'));
				}
				
				rewrite.addImport(IMPORT_CONTRACT_REFERENCE);
				
				TextEdit edits = rewrite.rewriteImports(null);
				
				applyEdits(edits);
			} catch (JavaModelException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	public void addContractReferenceAnnotation(IType contract) {
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

		TextEdit edits;
		try {
			edits = rewriter.rewriteAST();
			applyEdits(edits);
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

	}
	
	private void applyEdits(TextEdit edits){
		try {
			this.document = new Document(target.getCompilationUnit().getSource());
			edits.apply(document);
			targetCompilationUnit.getBuffer().setContents(document.get());
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public IType getType() {
		return target;
	}

}