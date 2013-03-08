package de.vksi.c4j.eclipse.plugin.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

import de.vksi.c4j.eclipse.plugin.internal.TypeFacade;
import de.vksi.c4j.eclipse.plugin.ui.quickassist.JumpAction;
import de.vksi.c4j.eclipse.plugin.util.requestor.AssosiatedMemberRequest;
import de.vksi.c4j.eclipse.plugin.util.requestor.AssosiatedMemberRequest.MemberType;

public class JumpToContractCommand extends AbstractHandler {

	private static final String JUMP_DIALOG_TITLE = "Jump to...";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		Context context = new Context(event);
		IJavaElement javaElement = context.getJavaElement();

		if (javaElement instanceof IType) {
			IType type = (IType) Platform.getAdapterManager().getAdapter(javaElement, IType.class);
			TypeFacade typeFacade = TypeFacade.createFacade(type.getCompilationUnit());
			AssosiatedMemberRequest request = AssosiatedMemberRequest.newCorrespondingMemberRequest() //
					.setDialogPromtText(JUMP_DIALOG_TITLE) //
					.withExpectedResultType(MemberType.TYPE) //
					.build();
			jump(typeFacade, request);
		} else if (javaElement instanceof IMethod) {
			IMethod method = (IMethod) Platform.getAdapterManager().getAdapter(javaElement, IMethod.class);
			TypeFacade typeFacade = TypeFacade.createFacade(method.getCompilationUnit());
			AssosiatedMemberRequest request = AssosiatedMemberRequest.newCorrespondingMemberRequest() //
					.setDialogPromtText(JUMP_DIALOG_TITLE) //
					.withExpectedResultType(MemberType.METHOD) //
					.withCurrentMethod(method) //
					.build();
			jump(typeFacade, request);
		}

		return null;
	}

	private void jump(TypeFacade typeFacade, AssosiatedMemberRequest request) {
		IMember assosiatedMember = typeFacade.getAssosiatedType(request);
		JumpAction.openType(assosiatedMember);
	}
}
