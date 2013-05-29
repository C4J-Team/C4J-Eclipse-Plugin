package de.vksi.c4j.eclipse.plugin.internal;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CONTRACT;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import de.vksi.c4j.eclipse.plugin.C4JEclipsePluginActivator;

public class C4JContractAnnotation extends C4JAnnotation {
	private static Logger logger = C4JEclipsePluginActivator.getLogManager().getLogger(C4JAnnotation.class.getName());
	
	public C4JContractAnnotation(IType type) {
		super(type);
	}

	public IType getTargetClass() {
		if (exists() && hasValue()) {
			String targetClass;
			try {
				targetClass = getAnnotationValue();
				return findContractClass(targetClass);
			} catch (JavaModelException e) {
				logger.error("Could not extract Target-Class from Contract-Annotation in " + getType().getElementName(), e);
			}
		}
		return null;
	}

	@Override
	public String c4jAnnotationType() {
		return ANNOTATION_CONTRACT;
	}
}