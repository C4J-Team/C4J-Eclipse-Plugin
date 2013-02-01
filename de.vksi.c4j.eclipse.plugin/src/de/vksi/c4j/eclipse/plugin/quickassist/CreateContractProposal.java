package de.vksi.c4j.eclipse.plugin.quickassist;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.corext.ValidateEditException;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.MalformedTreeException;

@SuppressWarnings("restriction")
public class CreateContractProposal implements IJavaCompletionProposal {

	private ContractCreator contractCreator;
	private IType type;

	public CreateContractProposal(IInvocationContext context) {
		this.contractCreator = new ContractCreator();
		this.type = context.getCompilationUnit().getType(context.getCoveringNode().toString());
	}

	@Override
	public void apply(IDocument document) {
		IType contract = contractCreator.createContractFor(type);
		
		if (contract != null) {
			try {
				C4JTarget target = new C4JTarget(type);
				target.addImportsFor(contract);
				target.addContractReferenceAnnotation(contract);
			} catch (ValidateEditException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (MalformedTreeException e) {
				e.printStackTrace();
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Point getSelection(IDocument document) {
		return null;
	}

	@Override
	public String getAdditionalProposalInfo() {
		return MessageFormat.format("Create C4j Contract for ''{0}''. By using the contract creation wizard you can chose which contract stubs are going to be created for ''{0}''", this.type.getElementName());
	}

	@Override
	public String getDisplayString() {
		return "Create C4J Contract";
	}

	@Override
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CLASS);
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}

	@Override
	public int getRelevance() {
		return 0;
	}
}
