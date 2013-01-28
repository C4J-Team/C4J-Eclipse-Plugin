package de.vksi.c4j.eclipse.plugin.text.hover;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocBrowserInformationControlInput;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;

import de.vksi.c4j.eclipse.plugin.util.ConditionExtractor;
import de.vksi.c4j.eclipse.plugin.util.Conditions;

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
		
		if (currElement instanceof IType) {
			Conditions conditionsOfMethod = ConditionExtractor.getConditionsOf((IType) currElement);
			String invariantConditions = generateConditionsHtml(Conditions.INVARIANT_CONDITIONS,
					conditionsOfMethod);
			html = insertConditionsIntoJavaDocHtml(html, invariantConditions);
		} else if (currElement instanceof IMethod) {
			Conditions conditionsOfMethod = ConditionExtractor.getConditionsOf((IMethod) currElement);
			String preConditionsHtml = generateConditionsHtml(Conditions.PRE_CONDITIONS, conditionsOfMethod);
			String postConditionsHtml = generateConditionsHtml(Conditions.POST_CONDITIONS, conditionsOfMethod);
			html = insertConditionsIntoJavaDocHtml(html, preConditionsHtml, postConditionsHtml);
		} 

		JavadocBrowserInformationControlInput newHtml = new JavadocBrowserInformationControlInput(
				(JavadocBrowserInformationControlInput) hoverInfo.getPrevious(), hoverInfo.getElement(),
				html, hoverInfo.getLeadingImageWidth());

		return newHtml;
	}

	private String insertConditionsIntoJavaDocHtml(String javaDocHtml, String... conditionsHtml) {
		String finalHtml = javaDocHtml;
		String allConditions = "";

		for (String currConditionHtml : conditionsHtml) {
			allConditions += currConditionHtml;
		}

		if (!allConditions.isEmpty()) {
			finalHtml = javaDocHtml.substring(0, javaDocHtml.lastIndexOf("</body>"));
			finalHtml += allConditions;
			finalHtml += "</dl></body></html>";
		}

		return finalHtml;
	}

	private String generateConditionsHtml(String kindOfConditions, Conditions conditions) {
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
