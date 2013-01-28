package de.vksi.c4j.eclipse.plugin.util;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CONTRACT_REFERENCE;

public class ContractReferenceAnnotation {
	private static final int FIRST_MEMBER_VALUE_PAIR = 0;
	private IType type;

	public ContractReferenceAnnotation(IType type) {
		this.type = type;
	}

	public boolean exists() {
		return getAnnotation() != null;
	}

	public IAnnotation getAnnotation() {
		if (type == null)
			return null;

		try {
			IAnnotation[] annotations = type.getAnnotations();
			for (IAnnotation annotation : annotations) {
				if (ANNOTATION_CONTRACT_REFERENCE.equals(annotation.getElementName())) {
					return annotation;
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public String getAnnotationValue() throws JavaModelException {
		if (exists()) {
			IMemberValuePair memberValuePair = getAnnotation().getMemberValuePairs()[FIRST_MEMBER_VALUE_PAIR];
			return memberValuePair.getValue().toString();
		}
		return "";
	}

	public IType getContractClass() throws JavaModelException {
		if (exists()) {
			String contractClass = getAnnotationValue();
			return findContractClass(contractClass);
		}
		return null;
	}

	private IType findContractClass(String contractClass) throws JavaModelException {
		IType contractType = findTypeByImports(contractClass);
		// if contract-compilation unit is located in the same package, an
		// import statement will not be required
		return contractType != null ? contractType : findTypeInSamePackage(contractClass);
	}

	private IType findTypeByImports(String contract) throws JavaModelException {
		IImportDeclaration[] imports = type.getCompilationUnit().getImports();
		for (IImportDeclaration currentImport : imports) {
			if (currentImport.getElementName().endsWith("." + contract)
					|| currentImport.getElementName().equals(contract)) {
				return type.getJavaProject().findType(currentImport.getElementName());
			}
		}
		return null;
	}
	
	private IType findTypeInSamePackage(String contract) throws JavaModelException {
		IPackageFragment[] packageFragments = type.getJavaProject().getPackageFragments();
		for (IPackageFragment packageFragment : packageFragments) {
			if (type.getPackageFragment().getElementName().equals(packageFragment.getElementName())) {
				ICompilationUnit compilationUnitInSamePackage = packageFragment.getCompilationUnit(contract + ".java");
				if (compilationUnitInSamePackage.exists()) {
					return compilationUnitInSamePackage.getTypes()[0];
				}
			}
		}
		return null;
	}
}
