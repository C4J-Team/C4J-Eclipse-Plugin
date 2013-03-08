package de.vksi.c4j.eclipse.plugin.util.requestor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import de.vksi.c4j.eclipse.plugin.internal.C4JContractAnnotation;
import de.vksi.c4j.eclipse.plugin.util.ContractChecker;

public class TargetRequestor implements Requestor {
	public List<IType> getAssociatedMemberOf(IType contract) {
		List<IType> target = new ArrayList<IType>();
		
		C4JContractAnnotation contractAnnotation = new C4JContractAnnotation(contract);
		if (contractAnnotation.hasValue()) {
			target.add(contractAnnotation.getTargetClass());
			return target;
		}

		IType supertype = TypeHierarchyRequestor.getSupertypeOf(contract);
		try {
			if (contract.getSuperclassName() != null && !TypeHierarchyRequestor.isObject(supertype)
					&& !ContractChecker.isContract(supertype)) {
				target.add(supertype);
				return target;
			}

			if (contract.getSuperInterfaceNames().length > 0) {
				//TODO: do not just return the first interface of the array. Instead, return type of the target member (annotated with '@Target') 
				target.add(TypeHierarchyRequestor.getSuperInterfacesOf(contract)[0]);//reason: one contract has exactly one target!
				return target;  
			}

		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return target;
	}
}
