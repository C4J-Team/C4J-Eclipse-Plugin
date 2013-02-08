package de.vksi.c4j.eclipse.plugin.ui.text.hover;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;

import de.vksi.c4j.eclipse.plugin.util.C4JContractAnnotation;

public class ExternalContractSearchRequestor extends SearchRequestor {
	private Map<IType, IType> externalContracts = new HashMap<IType, IType>();

	@Override
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		if (match.getElement() instanceof IType) {
			IType matchedType = (IType) match.getElement();
			C4JContractAnnotation contractAnnotation = new C4JContractAnnotation(matchedType);

			if (contractAnnotation.hasValue()) {
				addExternalContract(contractAnnotation.getTargetClass(), matchedType);
				return;
			}

			if (matchedType.getSuperclassName() != null && !isObject(matchedType)) {
				addExternalContract(getSupertypeOf(matchedType), matchedType);
			}

			if (matchedType.getSuperInterfaceNames().length > 0) {
				IType[] superInterfaces = getSuperInterfacesOf(matchedType);
				for (IType superInterface : superInterfaces) {
					addExternalContract(superInterface, matchedType);
				}
			}
		}
	}

	private boolean isObject(IType matchedType) {
		return Object.class.getName().equals(matchedType.getFullyQualifiedName());
	}

	//TODO: what if one key has many values?!
	public Map<IType, IType> getExternalContracts() {
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

	private void addExternalContract(IType target, IType contract) {
		externalContracts.put(target, contract);
	}

}
