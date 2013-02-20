package de.vksi.c4j.eclipse.plugin.util;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class SupertypeRequestor {
	public static boolean isObject(IType matchedType) {
		return Object.class.getName().equals(matchedType.getFullyQualifiedName());
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
}
