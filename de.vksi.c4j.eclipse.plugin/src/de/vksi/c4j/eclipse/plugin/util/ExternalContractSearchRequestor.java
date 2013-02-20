package de.vksi.c4j.eclipse.plugin.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;


public class ExternalContractSearchRequestor extends SearchRequestor {
	private ExternalContractMap externalContracts = new ExternalContractMap();

	@Override
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		if (match.getElement() instanceof IType) {
			IType matchedType = (IType) match.getElement();
			
			TargetRequestor tr = new TargetRequestor();
			IType target = tr.getTargetOf(matchedType);
			if(target != null)
				externalContracts.addContractFor(target, matchedType);
		}
	}

	public ExternalContractMap getExternalContracts() {
		return externalContracts;
	}
}
