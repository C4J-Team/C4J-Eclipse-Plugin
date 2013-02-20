package de.vksi.c4j.eclipse.plugin.ui.quickassist;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;

import de.vksi.c4j.eclipse.plugin.util.ContractRequestor;

public class C4JQuickAssistProcessor implements IQuickAssistProcessor {

	@Override
	public IJavaCompletionProposal[] getAssists(IInvocationContext context, IProblemLocation[] locations)
			throws CoreException {

		if (locations.length == 0)
			return getQuickAssists(context, locations);
		else
			return getQuickFixes(context, locations);

	}

	private IJavaCompletionProposal[] getQuickAssists(IInvocationContext context, IProblemLocation[] locations) {
		if (context.getCoveringNode() != null) {
			ASTNode currentNode = context.getCoveringNode().getParent();
			if (currentNode != null) {
				if (currentNode.getNodeType() == ASTNode.TYPE_DECLARATION)
					return createAssistFor((TypeDeclaration) currentNode, context);
				else if (currentNode.getNodeType() == ASTNode.METHOD_DECLARATION)
					return createAssistFor((MethodDeclaration) currentNode, context);
			}
		}
		return new IJavaCompletionProposal[] {};
	}

	private IJavaCompletionProposal[] getQuickFixes(IInvocationContext context, IProblemLocation[] locations) {
		// TODO: provide quick fixes (e.g. create contract by entering the
		// contract reference annotation)
		return new IJavaCompletionProposal[] {};
	}

	private IJavaCompletionProposal[] createAssistFor(TypeDeclaration currentNode, IInvocationContext context) {
		// no contract support for nested classes
		if (!isNestedClass(currentNode))
			return new IJavaCompletionProposal[] { new CreateContractProposal(context) };

		return new IJavaCompletionProposal[] {};
	}

	private IJavaCompletionProposal[] createAssistFor(MethodDeclaration currentNode,
			IInvocationContext context) {

		TypeDeclaration typeDeclaration = getTypeDeclaration(currentNode);
		// no contract support for nested classes
		if (!isNestedClass(typeDeclaration)) {
			IType type = context.getCompilationUnit().findPrimaryType();
			ContractRequestor contractRequestor = new ContractRequestor();
			List<IType> contracts = contractRequestor.getContractsFor(type);

			if(!contracts.isEmpty())			
				return new IJavaCompletionProposal[] { new CreateContractMethodProposal(context, contracts) };
		}
		// else: contract does not exist yet

		return new IJavaCompletionProposal[] {};
	}

	private TypeDeclaration getTypeDeclaration(ASTNode currentNode) {
		if (currentNode.getNodeType() == ASTNode.TYPE_DECLARATION)
			return (TypeDeclaration) currentNode;
		else if (currentNode.getNodeType() == ASTNode.METHOD_DECLARATION) {
			return (TypeDeclaration) ((MethodDeclaration) currentNode).getParent();
		}
		return null;
	}

	private boolean isNestedClass(TypeDeclaration type) {
		if (type.getParent().getNodeType() == ASTNode.TYPE_DECLARATION)
			return true;
		return false;
	}

	@Override
	public boolean hasAssists(IInvocationContext context) throws CoreException {
		return false;
	}
}
