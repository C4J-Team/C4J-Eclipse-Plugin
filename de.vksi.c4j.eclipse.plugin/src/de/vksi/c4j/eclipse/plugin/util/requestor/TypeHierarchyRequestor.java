package de.vksi.c4j.eclipse.plugin.util.requestor;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

public class TypeHierarchyRequestor {
	
	public static boolean isObject(IType type) {
		return type!=null ? Object.class.getName().equals(type.getFullyQualifiedName()) : false;
	}

	public static IType[] getSuperInterfacesOf(IType type) {
		try {
			return type.newSupertypeHierarchy(null).getSuperInterfaces(type);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return new IType[] {};
	}

	public static IType getSupertypeOf(IType type) {
		try {
			return type.newSupertypeHierarchy(null).getSuperclass(type);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static IType[] getTypeHierachy(IJavaElement element) {
		if (element instanceof IType)
			return getTypeHierachy((IType) element);

		if (element instanceof IMethod)
			return getTypeHierachy(((IMethod) element).getDeclaringType());

		return new IType[] {};
	}

	public static IType[] getTypeHierachy(IType type) {
		if (isObject(type))
			return new IType[] {};

		try {
			ITypeHierarchy typeHierarchy = type.newTypeHierarchy(null);
			IType[] allSupertypes = typeHierarchy.getAllSupertypes(type);
			return addGivenTypeToHierarchy(type, allSupertypes);

		} catch (JavaModelException e) {
			return new IType[] {};
		}
	}
	
	private static IType[] addGivenTypeToHierarchy(IType type, IType[] allSupertypes) {
		IType[] allSupertypesInclusiveCurrentType = new IType[allSupertypes.length + 1];

		allSupertypesInclusiveCurrentType[0] = type;
		for (int i = 0; i < allSupertypes.length; i++) {
			allSupertypesInclusiveCurrentType[i + 1] = allSupertypes[i];
		}
		return allSupertypesInclusiveCurrentType;
	}
}
