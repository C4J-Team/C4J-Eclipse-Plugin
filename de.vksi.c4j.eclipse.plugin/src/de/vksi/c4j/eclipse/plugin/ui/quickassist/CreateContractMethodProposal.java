package de.vksi.c4j.eclipse.plugin.ui.quickassist;

import java.text.MessageFormat;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import de.vksi.c4j.eclipse.plugin.internal.TargetFacade;
import de.vksi.c4j.eclipse.plugin.internal.TypeFacade;
import de.vksi.c4j.eclipse.plugin.util.AssosiatedMemberRequest;
import de.vksi.c4j.eclipse.plugin.util.AssosiatedMemberRequest.MemberType;
import de.vksi.c4j.eclipse.plugin.util.PluginImages;

public class CreateContractMethodProposal implements IJavaCompletionProposal {

	private static final String DISPLAY_STRING = "Create/Jump to Contract method";

	private MethodDeclaration selectedMethoDeclarationNode;
	private IMethod selectedMethod;

	private IType target;

	public CreateContractMethodProposal(IInvocationContext context) {
		this.target = context.getCompilationUnit().findPrimaryType();
		selectedMethoDeclarationNode = (MethodDeclaration) context.getCoveringNode().getParent();
		setSelectedMethod((IMethod) selectedMethoDeclarationNode.resolveBinding().getJavaElement());
	}

	@Override
	public void apply(IDocument document) {
		TypeFacade tf = TargetFacade.createFacade(target.getCompilationUnit());
		AssosiatedMemberRequest request = AssosiatedMemberRequest.newCorrespondingMemberRequest() //
				.asCreateRequest() //
		        .withExpectedResultType(MemberType.METHOD) //
		        .withCurrentMethodDeclaration(selectedMethoDeclarationNode) //
		        .setDialogPromtText("Create method in Contract or jump if it already exists...") //
		        .build();
		IMember assosiatedMember = tf.getAssosiatedType(request);
		JumpAction.openType(assosiatedMember);
	}

	@Override
	public Point getSelection(IDocument document) {
		return null;
	}

	@Override
	public String getAdditionalProposalInfo() {
		return MessageFormat
				.format("Create C4J Contract method-stub for ''{0}'' or jump to related method-stub if exists. "
						+ "If the target class ''{1}'' is guarded by more than one contract, you will have an "
						+ "option to select the desired contract. <br><br>FYI: This functionality does not "
						+ "comprise static code analysis, please make sure your contract(s) is(are) valid  after "
						+ "adding new method-stubs.", getSelectedMethod().getElementName(), getSelectedMethod()
						.getParent().getElementName());
	}

	@Override
	public String getDisplayString() {
		return DISPLAY_STRING;
	}

	@Override
	public Image getImage() {
		return PluginImages.DESC_CONTRACT_METHOD.createImage();
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}

	@Override
	public int getRelevance() {
		return 0;
	}

	public IMethod getSelectedMethod() {
		return selectedMethod;
	}

	private void setSelectedMethod(IMethod selectedMethod) {
		this.selectedMethod = selectedMethod;
	}
}
