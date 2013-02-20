package de.vksi.c4j.eclipse.plugin.ui.quickassist;

import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class JumpToContractControl extends PopupDialog {

	private TreeViewer viewer;
	private List<IType> types;
	private IType selectedContract;
	private DisposeListener focusListener;

	public JumpToContractControl(List<IType> types, Shell parent, int shellStyle, boolean takeFocusOnOpen,
			boolean persistSize, boolean persistLocation, boolean showDialogMenu, boolean showPersistActions,
			String titleText, String infoText) {
		super(parent, shellStyle, takeFocusOnOpen, persistSize, persistLocation, showDialogMenu,
				showPersistActions, titleText, infoText);
		this.types = types;

		setTitleText("Jump to...");
	}

	public JumpToContractControl(List<IType> contracts, Shell shell, DisposeListener focusListener) {
		this(contracts, shell, SWT.RESIZE, true, true, false, true, true, null, null);
		this.focusListener = focusListener;
	}
	
	@Override
	public int open() {
		getFocusControl().addDisposeListener(focusListener);
		return super.open();
	}

	
	@Override
	protected Control createDialogArea(Composite parent) {
		viewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ContentProvider());
		viewer.setLabelProvider(new JumpToContractLabelProvider());
		viewer.setInput(types);

		// Add a double-click listener
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.getFirstElement() instanceof IType) {
					setSelectedContract((IType) selection.getFirstElement());
					getShell().dispose();
				}
			}
		});
		
		return viewer.getControl();
	}

	public IType getSelectedContract() {
		return selectedContract;
	}

	private void setSelectedContract(IType selectedContract) {
		this.selectedContract = selectedContract;
	}
}
