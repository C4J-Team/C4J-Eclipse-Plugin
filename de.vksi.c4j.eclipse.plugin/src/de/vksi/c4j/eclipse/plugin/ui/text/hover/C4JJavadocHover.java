package de.vksi.c4j.eclipse.plugin.ui.text.hover;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocBrowserInformationControlInput;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;

import de.vksi.c4j.eclipse.plugin.internal.C4JConditions;

@SuppressWarnings("restriction")
public class C4JJavadocHover extends JavadocHover {
	
	@Override
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		JavadocBrowserInformationControlInput hoverInfo = (JavadocBrowserInformationControlInput) super
				.getHoverInfo2(textViewer, hoverRegion);
		if (hoverInfo == null)
			return super.getHoverInfo2(textViewer, hoverRegion);

		String html = hoverInfo.getHtml();
		IJavaElement currElement = hoverInfo.getElement();
		
		C4JConditions conditions = new ConditionExtractor().getConditionsOf(currElement);
		html = injectConditionsIntoJavaDocHtml(html, conditions);

		JavadocBrowserInformationControlInput newHtml = new JavadocBrowserInformationControlInput(
				(JavadocBrowserInformationControlInput) hoverInfo.getPrevious(), hoverInfo.getElement(),
				html, hoverInfo.getLeadingImageWidth());

		return newHtml;
	}

	private String injectConditionsIntoJavaDocHtml(String javaDocHtml, C4JConditions conditions) {
		String finalHtml = javaDocHtml;
		String allConditions = "";
		
		if(!conditions.getConditions(C4JConditions.INVARIANT_CONDITIONS).isEmpty())
			allConditions += generateConditionsHtml(C4JConditions.INVARIANT_CONDITIONS, conditions);
		
		if(!conditions.getConditions(C4JConditions.PRE_CONDITIONS).isEmpty())
			allConditions += generateConditionsHtml(C4JConditions.PRE_CONDITIONS, conditions);
		
		if(!conditions.getConditions(C4JConditions.POST_CONDITIONS).isEmpty())
			allConditions += generateConditionsHtml(C4JConditions.POST_CONDITIONS, conditions);
		
		if (!allConditions.isEmpty()) {
			finalHtml = javaDocHtml.substring(0, javaDocHtml.lastIndexOf("</body>"));
			finalHtml += allConditions;
			finalHtml += "</dl></body></html>";
		}
		
		return finalHtml;
	}
	
	private String generateConditionsHtml(String kindOfConditions, C4JConditions conditions) {
		boolean noConditions = conditions.getConditions(kindOfConditions).isEmpty();
		if (noConditions)
			return "";

		String kindOfConditionsHtml = "<dl><dt>" + kindOfConditions + "</dt>";

		String conditionsHtml = "";
		for (String condition : conditions.getConditions(kindOfConditions)) {
			conditionsHtml += "<dd>" + condition + "</dd>";
		}

		conditionsHtml += "</dl>";
		
		return kindOfConditionsHtml + conditionsHtml;
	}
}
