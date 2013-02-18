package de.vksi.c4j.eclipse.plugin.ui.quickassist;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.typehierarchy.HierarchyInformationControl;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public class CreateContractMethodProposal implements IJavaCompletionProposal {

	private IInvocationContext context;

	public CreateContractMethodProposal(IInvocationContext context) {
		this.context = context;
	}

	@Override
	public void apply(IDocument document) {
		//TODO: jump to contract -> see moreUnit: http://moreunit.git.sourceforge.net/git/gitweb.cgi?p=moreunit/moreunit;a=blob;f=org.moreunit.core/src/org/moreunit/core/commands/JumpActionExecutor.java;h=6ab8599670b6d5e9971b00acb92d242e266e464b;hb=refs/heads/master
		
	}

	@Override
	public Point getSelection(IDocument document) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAdditionalProposalInfo() {
		return "detail info";
	}

	@Override
	public String getDisplayString() {
		return "Create Contract Method";
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextInformation getContextInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRelevance() {
		// TODO Auto-generated method stub
		return 0;
	}

}
