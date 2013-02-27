package de.vksi.c4j.eclipse.plugin.util;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;


public class ExternalContractSearchRequestor extends SearchRequestor {
	private ExternalContractMap externalContracts = new ExternalContractMap();

	//TODO: check if the logic for searching the target can be replaced by the c4jcontract class
	@Override
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		if (match.getElement() instanceof IType) {
			IType matchedType = (IType) match.getElement();
			
			Requestor targetRequestor = new TargetRequestor();
			List<IType> targets = targetRequestor.getAssociatedMemberOf(matchedType);
			if(!targets.isEmpty())
				externalContracts.addContractFor(targets.get(0), matchedType);
		}
	}

	public ExternalContractMap getExternalContracts() {
		return externalContracts;
	}
}
