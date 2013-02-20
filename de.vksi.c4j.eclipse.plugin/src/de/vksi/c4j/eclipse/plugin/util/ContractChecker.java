package de.vksi.c4j.eclipse.plugin.util;

import org.eclipse.jdt.core.IType;

import de.vksi.c4j.eclipse.plugin.internal.C4JContractAnnotation;
import de.vksi.c4j.eclipse.plugin.internal.C4JContractReferenceAnnotation;

public class ContractChecker {

	public static boolean isContract(IType type) {
		if (type != null) {
			C4JContractAnnotation c4jContractAnnotation = new C4JContractAnnotation(type);
			if(c4jContractAnnotation.exists())
				return true;

			C4JContractReferenceAnnotation c4jContractReferenceAnnotation = new C4JContractReferenceAnnotation(type);
			if(c4jContractReferenceAnnotation.exists())
				return false;
			
			IType supertype = SupertypeRequestor.getSupertypeOf(type);
			if(supertype != null){
				return isContract(supertype);
			}

			IType[] superInterfaces = SupertypeRequestor.getSuperInterfacesOf(type);
			if(superInterfaces.length > 0){
				return isContract(superInterfaces[0]);
			}
		}
		return false;
	}
}
