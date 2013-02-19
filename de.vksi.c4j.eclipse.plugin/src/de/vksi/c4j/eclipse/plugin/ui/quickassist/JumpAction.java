package de.vksi.c4j.eclipse.plugin.ui.quickassist;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

public class JumpAction {
	public static void openType(IJavaElement element) {
		try {
			JavaUI.openInEditor(element);
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
}
