package de.vksi.c4j.eclipse.plugin.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;

import de.vksi.c4j.eclipse.plugin.util.requestor.ExternalContractSearchRequestor;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CONTRACT;

public class ExternalContractScanner {
	private ExternalContractMap externalContracts = new ExternalContractMap();

	//TODO: scope search to "type hierarchy" -> like JDTSearchProvider.searchMethodReference(referenceList, method, scope, iJavaProject);
	public void scan() {
		SearchPattern pattern = SearchPattern.createPattern(ANNOTATION_CONTRACT, IJavaSearchConstants.TYPE,
				IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE, SearchPattern.R_EXACT_MATCH);

		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();

		ExternalContractSearchRequestor externalContractRequestor = new ExternalContractSearchRequestor();

		SearchEngine searchEngine = new SearchEngine();
		try {
			SearchParticipant[] participants = new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};
			searchEngine.search(pattern, participants, scope, externalContractRequestor, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		externalContracts = externalContractRequestor.getExternalContracts();
	}

	public ExternalContractMap getExternalContracts() {
		return externalContracts;
	}
	
	
}
