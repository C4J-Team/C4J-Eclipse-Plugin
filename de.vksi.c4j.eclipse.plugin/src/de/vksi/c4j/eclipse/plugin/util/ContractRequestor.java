package de.vksi.c4j.eclipse.plugin.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IType;

import de.vksi.c4j.eclipse.plugin.internal.C4JContractReferenceAnnotation;

public class ContractRequestor {
	private ExternalContractMap externalContracts;
	
	public ContractRequestor(){
		externalContracts = searchForExternalContracts();
	}

	public List<IType> getContractsFor(IType target){
		C4JContractReferenceAnnotation contractReference = new C4JContractReferenceAnnotation(target);
		List<IType> listOfContracts = new ArrayList<IType>();

		if (contractReference.exists())
			listOfContracts.add(contractReference.getContractClass());
		
		searchForExternalContracts();
		listOfContracts.addAll(externalContracts.getContractsFor(target));

		return listOfContracts;
	}
	
	private ExternalContractMap searchForExternalContracts() {
		ExternalContractScanner externalContractScanner = new ExternalContractScanner();
		externalContractScanner.scan();
		return externalContractScanner.getExternalContracts();
	}
}
