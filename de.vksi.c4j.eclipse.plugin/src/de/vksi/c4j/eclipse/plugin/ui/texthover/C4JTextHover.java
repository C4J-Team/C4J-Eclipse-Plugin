package de.vksi.c4j.eclipse.plugin.ui.texthover;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocBrowserInformationControlInput;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;

import de.vksi.c4j.eclipse.plugin.internal.C4JConditions;

@SuppressWarnings("restriction")
public class C4JTextHover extends JavadocHover {
	
	@Override
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		JavadocBrowserInformationControlInput standardHoverInfo = getStandardHoverInfo(textViewer, hoverRegion);

		 JavadocBrowserInformationControlInput createC4JHoverInfo = createC4JHoverInfo(standardHoverInfo);
		 
		 return createC4JHoverInfo != null ? createC4JHoverInfo : super.getHoverInfo2(textViewer, hoverRegion);
	}

	private JavadocBrowserInformationControlInput getStandardHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		JavadocBrowserInformationControlInput standardHoverInfo = (JavadocBrowserInformationControlInput) super.getHoverInfo2(textViewer, hoverRegion);
		return standardHoverInfo;
	}

	public JavadocBrowserInformationControlInput createC4JHoverInfo(JavadocBrowserInformationControlInput hoverInfo) {
		if (hoverInfo == null)
			return null;
		
		String c4jHtmlTextHover = createC4JHtmlTextHover(hoverInfo);
		
		JavadocBrowserInformationControlInput c4jHoverInfo = new JavadocBrowserInformationControlInput(
				(JavadocBrowserInformationControlInput) hoverInfo.getPrevious(), hoverInfo.getElement(),
				c4jHtmlTextHover, hoverInfo.getLeadingImageWidth());
		return c4jHoverInfo;
	}
	
	private String createC4JHtmlTextHover(JavadocBrowserInformationControlInput standardHoverInfo) {
		String htmlTextHover = standardHoverInfo.getHtml();
		IJavaElement selectedElement = standardHoverInfo.getElement();
		
		C4JConditions conditions = new ConditionExtractor().getConditionsOf(selectedElement);
		htmlTextHover = C4JHtmlInjector.injectConditionsIntoHtml(htmlTextHover, conditions);
		return htmlTextHover;
	}
}
