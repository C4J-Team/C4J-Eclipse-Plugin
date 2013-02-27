package de.vksi.c4j.eclipse.plugin.ui.texthover;

import de.vksi.c4j.eclipse.plugin.internal.C4JConditions;

public class C4JHtmlInjector {
	public static String injectConditionsIntoHtml(String javaDocHtml, C4JConditions conditions) {
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
	
	private static String generateConditionsHtml(String kindOfConditions, C4JConditions conditions) {
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
