package de.vksi.c4j.eclipse.plugin.ui.quickassist;

import java.text.MessageFormat;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import de.vksi.c4j.eclipse.plugin.internal.C4JTargetFacade;
import de.vksi.c4j.eclipse.plugin.internal.TypeFacade;
import de.vksi.c4j.eclipse.plugin.util.C4JTargetTransformer;
import de.vksi.c4j.eclipse.plugin.util.PluginImages;
import de.vksi.c4j.eclipse.plugin.util.requestor.AssosiatedMemberRequest;
import de.vksi.c4j.eclipse.plugin.util.requestor.AssosiatedMemberRequest.MemberType;

public class CreateContractProposal implements IJavaCompletionProposal {

	private C4JTargetTransformer target;

	public CreateContractProposal(IInvocationContext context) {
		IType type = context.getCompilationUnit().getType(context.getCoveringNode().toString());
		this.target = new C4JTargetTransformer(type);
	}

	@Override
	public void apply(IDocument document) {
		TypeFacade typeFacade = C4JTargetFacade.createFacade(target.getType().getCompilationUnit());
		AssosiatedMemberRequest request = AssosiatedMemberRequest.newCorrespondingMemberRequest() //
				.asCreateRequest() //
		        .withExpectedResultType(MemberType.TYPE) //
		        .setDialogPromtText("Create Contract or jump if it already exists...") //
		        .build();
		IMember assosiatedMember = typeFacade.getAssosiatedType(request);
		JumpAction.openType(assosiatedMember);
	}

	@Override
	public Point getSelection(IDocument document) {
		return null;
	}

	@Override
	public String getAdditionalProposalInfo() {
		return MessageFormat.format("Create C4J Contract for ''{0}''. By using the contract creation "
				+ "wizard you can chose which contract stubs are going to be created "
				+ "for ''{0}''.<br><br>FYI: This functionality does not comprise static "
				+ "code analysis, please make sure your contract(s) is(are) valid.", target.getType()
				.getElementName());
	}

	@Override
	public String getDisplayString() {
		return "Create C4J Contract";
	}

	@Override
	public Image getImage() {
		return PluginImages.DESC_NEW_CONTRACT.createImage();
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
