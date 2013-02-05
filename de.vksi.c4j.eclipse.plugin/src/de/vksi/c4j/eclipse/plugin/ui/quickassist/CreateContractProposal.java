package de.vksi.c4j.eclipse.plugin.ui.quickassist;

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

import de.vksi.c4j.eclipse.plugin.util.C4JContract;
import de.vksi.c4j.eclipse.plugin.util.C4JTarget;

@SuppressWarnings("restriction")
public class CreateContractProposal implements IJavaCompletionProposal {

	private C4JContract contract;
	private C4JTarget target;

	public CreateContractProposal(IInvocationContext context) {
		this.contract = new C4JContract();
		IType type = context.getCompilationUnit().getType(
				context.getCoveringNode().toString());
		this.target = new C4JTarget(type);
	}

	@Override
	public void apply(IDocument document) {
		contract.createContractFor(target.getType());

		if (contract.exists()) {
			try {
				if (!contract.isExternal()) {
					target.addImportsFor(contract.getType());
					target.addContractReferenceAnnotation(contract.getType());
				}
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
		return MessageFormat
				.format("Create C4j Contract for ''{0}''. By using the contract creation wizard you can chose which contract stubs are going to be created for ''{0}''",
						target.getType().getElementName());
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
