package de.vksi.c4j.eclipse.plugin.ui.quickassist;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

public class JumpAction {
	public static void openType(IJavaElement element) {
		try {
			IEditorPart javaEditor = JavaUI.openInEditor(element);
			//TODO: refresh/update editor instead of using the save-action 
			javaEditor.doSave(null);
			JavaUI.revealInEditor(javaEditor, element);
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
}
