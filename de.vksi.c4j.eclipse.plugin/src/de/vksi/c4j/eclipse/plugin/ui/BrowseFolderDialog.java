package de.vksi.c4j.eclipse.plugin.ui;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

@SuppressWarnings("restriction")
public class BrowseFolderDialog {
	private static final String CHOOSE_LIB_CONTAINER_MESSAGE = "Choose a folder";
	private static final String CHOOSE_LIB_CONTAINER_TITLE = "Choose destination folder of C4J libraries";
	
	public IJavaProject javaProject;

	public BrowseFolderDialog(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}
	
	public IFolder openDialog(Shell parentShell){
		Class<?>[] acceptedClasses = new Class[] { IFolder.class, IJavaProject.class };
		TypedElementSelectionValidator validator = new TypedElementSelectionValidator(acceptedClasses, false) {
			@Override
			public boolean isSelectedValid(Object element) {
				return element instanceof IFolder;
			}
		};

		acceptedClasses = new Class[] { IJavaModel.class, IFolder.class, IJavaProject.class };
		ViewerFilter filter = new TypedViewerFilter(acceptedClasses) {
			@Override
			public boolean select(Viewer viewer, Object parent, Object element) {
				if (element instanceof IFolder)
					return true;

				return super.select(viewer, parent, element);
			}
		};

		StandardJavaElementContentProvider provider = new StandardJavaElementContentProvider();
		ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(parentShell, labelProvider,
				provider);
		dialog.setValidator(validator);
		dialog.setComparator(new JavaElementComparator());
		dialog.setTitle(CHOOSE_LIB_CONTAINER_TITLE);
		dialog.setMessage(CHOOSE_LIB_CONTAINER_MESSAGE);
		dialog.addFilter(filter);
		dialog.setInput(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()));
		dialog.setInitialSelection(javaProject);
		dialog.setHelpAvailable(false);

		if (dialog.open() == Window.OK) {
			Object element = dialog.getFirstResult();
			if (element instanceof IFolder)
				return (IFolder) element;
		}
		return null;
	}
}