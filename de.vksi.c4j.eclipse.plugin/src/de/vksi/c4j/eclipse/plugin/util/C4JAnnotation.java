package de.vksi.c4j.eclipse.plugin.util;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public abstract class C4JAnnotation {

	private static final int FIRST_MEMBER_VALUE_PAIR = 0;
	private IType type;

	public C4JAnnotation(IType type) {
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
				if (c4jAnnotationType().equals(annotation.getElementName())) {
					return annotation;
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	public abstract String c4jAnnotationType();

	public boolean hasValue() {
		if (exists()) {
			try {
				if(getAnnotation().getMemberValuePairs().length > 0){
					return true;
				}
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	public String getAnnotationValue() throws JavaModelException {
		if (hasValue()) {
			IMemberValuePair memberValuePair = getAnnotation().getMemberValuePairs()[FIRST_MEMBER_VALUE_PAIR];
			return memberValuePair.getValue().toString();
		}
		return "";
	}

	protected IType findContractClass(String contractClass) throws JavaModelException {
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