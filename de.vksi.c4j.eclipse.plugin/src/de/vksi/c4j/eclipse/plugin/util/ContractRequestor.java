package de.vksi.c4j.eclipse.plugin.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IType;

import de.vksi.c4j.eclipse.plugin.internal.C4JContractReferenceAnnotation;

public class ContractRequestor  implements Requestor {
	private ExternalContractMap externalContracts;

	public List<IType> getAssociatedMemberOf(IType target){
		externalContracts = searchForExternalContracts();
		
		C4JContractReferenceAnnotation contractReference = new C4JContractReferenceAnnotation(target);
		List<IType> listOfContracts = new ArrayList<IType>();

		if (contractReference.exists())
			listOfContracts.add(contractReference.getContractClass());
		
		listOfContracts.addAll(externalContracts.getContractsFor(target));

		return listOfContracts;
	}
	
	private ExternalContractMap searchForExternalContracts() {
		ExternalContractScanner externalContractScanner = new ExternalContractScanner();
		externalContractScanner.scan();
		return externalContractScanner.getExternalContracts();
	}
}
