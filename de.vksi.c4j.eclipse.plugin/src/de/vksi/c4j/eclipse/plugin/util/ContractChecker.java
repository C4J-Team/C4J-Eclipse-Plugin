package de.vksi.c4j.eclipse.plugin.util;

import org.eclipse.jdt.core.IType;

import de.vksi.c4j.eclipse.plugin.internal.C4JContractAnnotation;
import de.vksi.c4j.eclipse.plugin.internal.C4JContractReferenceAnnotation;
import de.vksi.c4j.eclipse.plugin.util.requestor.TypeHierarchyRequestor;

public class ContractChecker {

	public static boolean isContract(IType type) {
		if (type != null) {
			if (hasContractAnnotation(type))
				return true;
			if(hasContractReferenceAnnotation(type))
				return false;
			if(checkSuperTypeForContract(type))
				return true;
			if(checkSuperInterfacesForContract(type))
				return true;
		}
		return false;
	}

	private static boolean checkSuperTypeForContract(IType type) {
		IType supertype = TypeHierarchyRequestor.getSupertypeOf(type);
		if (supertype != null && !TypeHierarchyRequestor.isObject(supertype)) {
			C4JContractReferenceAnnotation c4jContractReferenceAnnotation = new C4JContractReferenceAnnotation(supertype);
			if(c4jContractReferenceAnnotation.exists() && type.equals(c4jContractReferenceAnnotation.getContractClass()))
				return true;
			
			C4JContractAnnotation c4jContractAnnotation = new C4JContractAnnotation(supertype);
			if(c4jContractAnnotation.exists())
				return true;
		}
		return false;
	}
	
	private static boolean checkSuperInterfacesForContract(IType type) {
		IType[] superInterfaces = TypeHierarchyRequestor.getSuperInterfacesOf(type);
		if (superInterfaces.length > 0) {
			C4JContractReferenceAnnotation c4jContractReferenceAnnotation = new C4JContractReferenceAnnotation(superInterfaces[0]);
			if(c4jContractReferenceAnnotation.exists() && type.equals(c4jContractReferenceAnnotation.getContractClass()))
				return true;
			
			C4JContractAnnotation c4jContractAnnotation = new C4JContractAnnotation(superInterfaces[0]);
			if(c4jContractAnnotation.exists())
				return true;
		}
		return false;
	}

	private static boolean hasContractReferenceAnnotation(IType type) {
		C4JContractReferenceAnnotation c4jContractReferenceAnnotation = new C4JContractReferenceAnnotation(type);
		return c4jContractReferenceAnnotation.exists();
	}

	private static boolean hasContractAnnotation(IType type) {
		C4JContractAnnotation c4jContractAnnotation = new C4JContractAnnotation(type);
		return c4jContractAnnotation.exists();
	}
}
