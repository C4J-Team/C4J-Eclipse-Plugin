package de.vksi.c4j.eclipse.plugin.ui.text.hover;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CLASS_INVARIANT;

import java.util.Iterator;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import de.vksi.c4j.eclipse.plugin.util.C4JConditions;

public class MethodVisitor extends ASTVisitor {
	private static final String VOID = "void";
	private C4JConditions conditions;
	private IMethod targetMethod;

	public MethodVisitor(IMethod targetMethod) {
		this.targetMethod = targetMethod;
		conditions = new C4JConditions();
	}

	@Override
	public boolean visit(MethodDeclaration method) {
		if (matchesTargetMethod(method)) {
			if(isClassInvariant()){
				AssertStatementVisitor assertVisitor = new AssertStatementVisitor();
				method.accept(assertVisitor);
				conditions.addInvariantConditions(assertVisitor.getConditions());
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
		Iterator<?> paramIterator = method.parameters().iterator();
		
		while(paramIterator.hasNext()){
			Object currParam = paramIterator.next();
			if (currParam instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration param = (SingleVariableDeclaration) currParam;
				signature += param.getType().toString();
			}
			
			signature += paramIterator.hasNext() ? ", " : "";
		}
		
		signature += ")";
		
		return signature;
	}

	public C4JConditions getConditions() {
		return conditions;
	}
}
