package de.vksi.c4j.eclipse.plugin.internal;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CONTRACT;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class C4JContractAnnotation extends C4JAnnotation {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String c4jAnnotationType() {
		return ANNOTATION_CONTRACT;
	}
}