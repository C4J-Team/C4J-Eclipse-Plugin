package de.vksi.c4j.eclipse.plugin.util;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CLASS_INVARIANT;

public class MethodVisitor extends ASTVisitor {
	private static final String VOID = "void";
	private Conditions conditions;
	private IMethod targetMethod;

	public MethodVisitor(IMethod targetMethod) {
		this.targetMethod = targetMethod;
		conditions = new Conditions();
	}

	@Override
	public boolean visit(MethodDeclaration method) {
		if (matchesTargetMethod(method)) {
			if(isClassInvariant()){
				AssertStatementVisitor assertVisitor = new AssertStatementVisitor();
				method.accept(assertVisitor);
				conditions.setInvariantConditions(assertVisitor.getConditions());
				return false;
			}

			IfStatementVisitor ifStatement = new IfStatementVisitor();
			method.accept(ifStatement);
			conditions = ifStatement.getConditions();
		}

		// false: children of this node should be skipped
		return false;
	}

	private boolean isClassInvariant() {
		try {
			for (IAnnotation annotation : targetMethod.getAnnotations()) {
				if (ANNOTATION_CLASS_INVARIANT.equals(annotation.getElementName())) {
					return true;
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean isConstructor(IMethod method) {
		try {
			return targetMethod.isConstructor();
		} catch (Exception e) {
			return false;
		}
	}

	private boolean matchesTargetMethod(MethodDeclaration method) {
		if (targetMethod == null)
			return false;

		try {
			String contractMethodName = method.getName().toString();
			String contractMethodSignature = createMethodSignature(method);

			String targetMethodName = targetMethod.getElementName();
			String targetMethodSignature = Signature.getSignatureSimpleName(targetMethod.getSignature());

			if (isConstructor(targetMethod))
				return (method.isConstructor() && contractMethodSignature.equals(targetMethodSignature));
			else
				return (contractMethodName.equals(targetMethodName) && contractMethodSignature
						.equals(targetMethodSignature));

		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return false;
	}

	private String createMethodSignature(MethodDeclaration method) {
		String signature = method.getReturnType2() != null ? method.getReturnType2().toString() : VOID;
		signature += " (";
		for (Object currParam : method.parameters()) {
			if (currParam instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration param = (SingleVariableDeclaration) currParam;
				signature += param.getType().toString() + " ";
			}
		}
		signature = signature.trim() + ")";
		return signature;
	}

	public Conditions getConditions() {
		return conditions;
	}
}
