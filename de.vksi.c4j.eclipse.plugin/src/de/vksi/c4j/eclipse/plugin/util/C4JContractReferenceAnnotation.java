package de.vksi.c4j.eclipse.plugin.util;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CONTRACT_REFERENCE;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;


public class C4JContractReferenceAnnotation extends C4JAnnotation {
	public C4JContractReferenceAnnotation(IType type) {
		super(type);
	}

	public IType getContractClass(){
		if (exists() && hasValue()) {
			String contractClass;
			try {
				contractClass = getAnnotationValue();
				return findContractClass(contractClass);
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String c4jAnnotationType() {
		return ANNOTATION_CONTRACT_REFERENCE;
	}
}
