package de.vksi.c4j.eclipse.plugin.util;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import de.vksi.c4j.eclipse.plugin.internal.C4JContractAnnotation;

public class TargetRequestor {
	public IType getTargetOf(IType contract) {
		C4JContractAnnotation contractAnnotation = new C4JContractAnnotation(contract);
		if (contractAnnotation.hasValue()) {
			return contractAnnotation.getTargetClass();
		}

		IType supertype = SupertypeRequestor.getSupertypeOf(contract);
		try {
			if (contract.getSuperclassName() != null && !SupertypeRequestor.isObject(supertype)
					&& !ContractChecker.isContract(supertype)) {
				return supertype;
			}

			if (contract.getSuperInterfaceNames().length > 0) {
				//TODO: do not just return the first interface of the array. Instead, return type of the target member (annotated with '@Target') 
				return SupertypeRequestor.getSuperInterfacesOf(contract)[0]; //reason: one contract has exactly one target! 
			}

		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
