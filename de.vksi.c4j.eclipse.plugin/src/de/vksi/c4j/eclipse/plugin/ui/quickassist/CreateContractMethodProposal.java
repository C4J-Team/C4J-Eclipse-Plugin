package de.vksi.c4j.eclipse.plugin.ui.quickassist;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import de.vksi.c4j.eclipse.plugin.internal.C4JContract;
import de.vksi.c4j.eclipse.plugin.util.C4JContractTransformer;

@SuppressWarnings("restriction")
public class CreateContractMethodProposal implements IJavaCompletionProposal, DisposeListener {

	private static final String DISPLAY_STRING = "Create/Jump to Contract method";

	private List<IType> contracts;
	private MethodDeclaration selectedMethoDeclarationNode;
	private IMethod selectedMethod;
	private JumpToContractControl jumpControl;

	public CreateContractMethodProposal(IInvocationContext context, List<IType> contracts) {
		this.contracts = contracts;
		selectedMethoDeclarationNode = (MethodDeclaration) context.getCoveringNode().getParent();
		selectedMethod = (IMethod) selectedMethoDeclarationNode.resolveBinding().getJavaElement();
	}

	@Override
	public void apply(IDocument document) {
		if (contracts.size() == 1) {
			createContractMethod(contracts.get(0));
			jumpToContractMethod(contracts.get(0));
		} else if (contracts.size() > 1) {
			Shell shell = JavaPlugin.getActiveWorkbenchShell();
			jumpControl = new JumpToContractControl(contracts, shell, this);
			jumpControl.create();
			jumpControl.open();
		}
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
						+ "adding new method-stubs.", selectedMethod.getElementName(), selectedMethod
						.getParent().getElementName());
	}

	@Override
	public String getDisplayString() {
		return DISPLAY_STRING;
	}

	@Override
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_MISC_PUBLIC);
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}

	@Override
	public int getRelevance() {
		return 0;
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
		createContractMethod(jumpControl.getSelectedContract());
		jumpToContractMethod(jumpControl.getSelectedContract());
	}

	private void createContractMethod(IType selectedContract) {
		if (selectedContract != null) {
			C4JContract contract = new C4JContract(selectedContract);
			if (!contract.hasMethod(selectedMethod)) {
				C4JContractTransformer contractTransformer = new C4JContractTransformer(selectedContract);
				try {
					contractTransformer.addMethodStub(selectedMethoDeclarationNode.resolveBinding());
					contractTransformer.applyEdits();
				} catch (JavaModelException e1) {
					e1.printStackTrace();
				} catch (CoreException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void jumpToContractMethod(IType selectedContract) {
		if (selectedContract != null)
			JumpAction.openType(selectedContract.getMethod(selectedMethod.getElementName(),
					selectedMethod.getParameterTypes()));
	}
}
