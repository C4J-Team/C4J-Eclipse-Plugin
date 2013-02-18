package de.vksi.c4j.eclipse.plugin.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;

import de.vksi.c4j.eclipse.plugin.internal.C4JContractAnnotation;


public class ExternalContractSearchRequestor extends SearchRequestor {
	private ExternalContractMap externalContracts = new ExternalContractMap();

	@Override
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		if (match.getElement() instanceof IType) {
			IType matchedType = (IType) match.getElement();
			C4JContractAnnotation contractAnnotation = new C4JContractAnnotation(matchedType);

			if (contractAnnotation.hasValue()) {
				externalContracts.addContractFor(contractAnnotation.getTargetClass(), matchedType);
				return;
			}

			if (matchedType.getSuperclassName() != null && !isObject(matchedType)) {
				externalContracts.addContractFor(getSupertypeOf(matchedType), matchedType);
			}

			if (matchedType.getSuperInterfaceNames().length > 0) {
				IType[] superInterfaces = getSuperInterfacesOf(matchedType);
				for (IType superInterface : superInterfaces) {
					externalContracts.addContractFor(superInterface, matchedType);
				}
			}
		}
	}

	private boolean isObject(IType matchedType) {
		return Object.class.getName().equals(matchedType.getFullyQualifiedName());
	}

	public ExternalContractMap getExternalContracts() {
		return externalContracts;
	}

	private IType[] getSuperInterfacesOf(IType type) {
		try {
			return type.newSupertypeHierarchy(null).getSuperInterfaces(type);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new IType[] {};
	}

	private IType getSupertypeOf(IType type) {
		try {
			return type.newSupertypeHierarchy(null).getSuperclass(type);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
