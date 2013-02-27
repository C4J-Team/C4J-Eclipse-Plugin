package de.vksi.c4j.eclipse.plugin.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;

public class ExternalContractScanner {
	private ExternalContractMap externalContracts = new ExternalContractMap();

	//TODO: try to scope the search to "type hierarchy" -> like JDTSearchProvider.searchMethodReference(referenceList, method, scope, iJavaProject);
	public void scan() {
		SearchPattern pattern = SearchPattern.createPattern("Contract", IJavaSearchConstants.TYPE,
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
