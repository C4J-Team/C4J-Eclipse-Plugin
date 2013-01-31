package de.vksi.c4j.eclipse.plugin.quickassist;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CONTRACT_REFERENCE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;

public class C4JQuickAssistProcessor implements IQuickAssistProcessor {

	@Override
	public IJavaCompletionProposal[] getAssists(IInvocationContext context, IProblemLocation[] locations)
			throws CoreException {

		ASTNode currentNode = context.getCoveringNode().getParent();
		if (currentNode != null) {
			if (currentNode.getNodeType() == ASTNode.TYPE_DECLARATION)
				return createAssistFor((TypeDeclaration) currentNode, context);
			else if (currentNode.getNodeType() == ASTNode.METHOD_DECLARATION)
				return createAssistFor((MethodDeclaration) currentNode, context);
		}
		return new IJavaCompletionProposal[] {};
	}

	private IJavaCompletionProposal[] createAssistFor(TypeDeclaration currentNode, IInvocationContext context) {
		//no contract support for nested classes
		if (!hasContractAnnotation(currentNode) && !isNestedClass(currentNode))
			return new IJavaCompletionProposal[] { new CreateContractProposal(context) };

		return new IJavaCompletionProposal[] {};
	}

	private IJavaCompletionProposal[] createAssistFor(MethodDeclaration currentNode,
			IInvocationContext context) {

		TypeDeclaration typeDeclaration = getTypeDeclaration(currentNode);
		//no contract support for nested classes
		if (hasContractAnnotation(typeDeclaration) && !isNestedClass(typeDeclaration)) {

		}
		// else: contract does not exist yet

		return new IJavaCompletionProposal[] {};
	}

	private boolean hasContractAnnotation(TypeDeclaration currentNode) {
		for (Object modifier : currentNode.modifiers()) {
			if (modifier instanceof SingleMemberAnnotation) {
				String annotationName = ((SingleMemberAnnotation) modifier).getTypeName()
						.getFullyQualifiedName();
				return ANNOTATION_CONTRACT_REFERENCE.equals(annotationName);
			}
		}

		return false;
	}

	private TypeDeclaration getTypeDeclaration(ASTNode currentNode) {
		if (currentNode.getNodeType() == ASTNode.TYPE_DECLARATION)
			return (TypeDeclaration) currentNode;
		else if (currentNode.getNodeType() == ASTNode.METHOD_DECLARATION) {
			return (TypeDeclaration) ((MethodDeclaration) currentNode).getParent();
		}
		return null;
	}
	
	private boolean isNestedClass(TypeDeclaration type){
		if(type.getParent().getNodeType() == ASTNode.TYPE_DECLARATION)
			return true;
		return false;
	}

	@Override
	public boolean hasAssists(IInvocationContext context) throws CoreException {
		return false;
	}
}
