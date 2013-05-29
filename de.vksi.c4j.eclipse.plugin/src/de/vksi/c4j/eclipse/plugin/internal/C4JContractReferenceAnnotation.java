package de.vksi.c4j.eclipse.plugin.internal;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CONTRACT_REFERENCE;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import de.vksi.c4j.eclipse.plugin.C4JEclipsePluginActivator;

public class C4JContractReferenceAnnotation extends C4JAnnotation {
	private static Logger logger = C4JEclipsePluginActivator.getLogManager().getLogger(C4JContractReferenceAnnotation.class.getName());
	
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
				logger.error("Could not extract Contract-Class from ContractReference-Annotation in " + getType().getElementName(), e);
			}
		}
		return null;
	}

	@Override
	public String c4jAnnotationType() {
		return ANNOTATION_CONTRACT_REFERENCE;
	}
}
