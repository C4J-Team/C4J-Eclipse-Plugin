package de.vksi.c4j.eclipse.plugin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IType;

public class ExternalContractMap {
	private Map<IType, List<IType>> externalContracts;
	
	public ExternalContractMap(){
		externalContracts = new HashMap<IType, List<IType>>(); 
	}
	
	public List<IType> getContractsFor(IType target){
		return externalContracts.containsKey(target) ? externalContracts.get(target) : new ArrayList<IType>();
	}
	
	public void addContractFor(IType target, IType contract) {
		if(contract == null || target == null)
			return;
		
		List<IType> listOfContract = new ArrayList<IType>();
		listOfContract.add(contract);
		addContractsFor(target, listOfContract);
	}

	public void addContractsFor(IType target, List<IType> contracts){
		if(contracts == null || target == null)
			return;
		
		if(externalContracts.containsKey(target))
			externalContracts.get(target).addAll(contracts);
		else
			externalContracts.put(target, contracts);
	}

	public boolean containsTarget(IType target){
		return externalContracts.containsKey(target);
	}
	
}
