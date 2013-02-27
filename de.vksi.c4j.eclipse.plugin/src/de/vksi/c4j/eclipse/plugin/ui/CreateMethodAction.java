package de.vksi.c4j.eclipse.plugin.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import de.vksi.c4j.eclipse.plugin.util.C4JContractTransformer;

public class CreateMethodAction extends CreateNewClassAction {
	private final IType type;
	private MethodDeclaration method;

	public CreateMethodAction(IType type, MethodDeclaration method) {
		assert type != null : "type must not be null";
		assert method != null : "method must not be null";

		this.type = (IType) type;
		this.method = method;
	}

	@Override
	public IMember execute() {
		C4JContractTransformer contractTransformer = new C4JContractTransformer(type);
		try {
			if (method.isConstructor())
				contractTransformer.addContructorStub(method.resolveBinding());
			else
				contractTransformer.addMethodStub(method.resolveBinding());
			
			contractTransformer.applyEdits();
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		return type.getMethod(method.getName().toString(), ((IMethod)method.resolveBinding().getJavaElement()).getParameterTypes());
	}
	

	@Override
	public String getText() {
		return String.format("%s - %s", type.getElementName(), type.getPackageFragment().getElementName());
	}

	public IType getType() {
		return type;
	}
}
