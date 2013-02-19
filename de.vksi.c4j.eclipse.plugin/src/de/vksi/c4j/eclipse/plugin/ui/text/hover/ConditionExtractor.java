package de.vksi.c4j.eclipse.plugin.ui.text.hover;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CLASS_INVARIANT;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

import de.vksi.c4j.eclipse.plugin.internal.C4JConditions;
import de.vksi.c4j.eclipse.plugin.util.ContractRequestor;

public class ConditionExtractor {
	private C4JConditions conditions;
	private ContractRequestor contractRequestor;

	public ConditionExtractor() {
		conditions = new C4JConditions();
	}

	public C4JConditions getConditionsOf(IJavaElement element) {
		if (element != null) {
			contractRequestor = new ContractRequestor();
			searchTypeHierachyForContractConditions(element);
		}
		return conditions;
	}

	private void searchTypeHierachyForContractConditions(IJavaElement element) {
		IType[] typeHierachy = getTypeHierachy(element);

		for (int i = typeHierachy.length - 1; i >= 0; i--) {
			searchTypeForContractConditions(element, typeHierachy[i]);
		}
	}

	private IType[] getTypeHierachy(IJavaElement element) {
		if (element instanceof IType)
			return getTypeHierachy((IType) element);

		if (element instanceof IMethod)
			return getTypeHierachy(((IMethod) element).getDeclaringType());

		return new IType[] {};
	}

	private IType[] getTypeHierachy(IType type) {
		if (Object.class.getName().equals(type.getFullyQualifiedName()))
			return new IType[] {};

		try {
			ITypeHierarchy typeHierarchy = type.newTypeHierarchy(null);
			IType[] allSupertypes = typeHierarchy.getAllSupertypes(type);
			return addGivenTypeToHierarchy(type, allSupertypes);

		} catch (JavaModelException e) {
			return new IType[] {};
		}
	}

	private IType[] addGivenTypeToHierarchy(IType type, IType[] allSupertypes) {
		IType[] allSupertypesInclusiveCurrentType = new IType[allSupertypes.length + 1];

		allSupertypesInclusiveCurrentType[0] = type;
		for (int i = 0; i < allSupertypes.length; i++) {
			allSupertypesInclusiveCurrentType[i + 1] = allSupertypes[i];
		}
		return allSupertypesInclusiveCurrentType;
	}

	private void searchTypeForContractConditions(IJavaElement element, IType type) {
//		List<IType> listOfContracts = getContract(type);
		List<IType> listOfContracts = contractRequestor.getContractsFor(type);
		for (IType contract : listOfContracts) {
			IMethod method = getMethodOfInterest(element, contract);
			C4JConditions conditionsToMerge = parseContract(contract, method);

			if (preConditionsAlreadyDefined(type, conditionsToMerge)) {
				addWarningToPreconditions(contract);
				conditionsToMerge.setPreConditions(new ArrayList<String>());
			}

			conditions.mergeWith(conditionsToMerge);
		}
	}

	private boolean preConditionsAlreadyDefined(IType type, C4JConditions conditionsToMerge) {
		return hasSupertype(type) && conditions.hasPreConditions()
				&& !conditionsToMerge.getConditions(C4JConditions.PRE_CONDITIONS).isEmpty();
	}

	private boolean hasSupertype(IType type) {
		try {
			return type.getSuperclassName() != null || type.getSuperInterfaceNames().length > 0;
		} catch (JavaModelException e) {
			return false;
		}
	}

	private IMethod getMethodOfInterest(IJavaElement element, IType contract) {
		IMethod method = null;
		try {
			if (element instanceof IType)
				method = getClassInvariantsMethod(contract);
			else
				method = (IMethod) element;
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return method;
	}

	private IMethod getClassInvariantsMethod(IType contract) throws JavaModelException {
		if (contract != null) {
			for (IMethod method : contract.getMethods()) {
				for (IAnnotation annotation : method.getAnnotations()) {
					if (ANNOTATION_CLASS_INVARIANT.equals(annotation.getElementName())) {
						return method;
					}
				}
			}
		}
		return null;
	}

	private C4JConditions parseContract(IType contract, IMethod method) {
		if (contract != null) {
			ASTNode root = parse(contract);
			MethodVisitor methodVisitor = new MethodVisitor(method);
			root.accept(methodVisitor);
			return methodVisitor.getConditions();
		}
		return new C4JConditions();
	}

	private ASTNode parse(IType contract) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(contract.getCompilationUnit());
		parser.setResolveBindings(false);
		ASTNode root = parser.createAST(null);

		return root;
	}

	private void addWarningToPreconditions(IType type) {
		String relatedContractName = type.getElementName();
		String warning = MessageFormat.format("<br>WARNING: Found strengthening pre-condition "
				+ "in Contract ''{0}'' which is already defined from its super Contract "
				+ "- ignoring the pre-condition", relatedContractName);
		conditions.addWaringToConditions(C4JConditions.PRE_CONDITIONS, warning);
	}
}
