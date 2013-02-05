package de.vksi.c4j.eclipse.plugin.ui.text.hover;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CLASS_INVARIANT;

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

import de.vksi.c4j.eclipse.plugin.util.C4JConditions;
import de.vksi.c4j.eclipse.plugin.util.C4JContractReferenceAnnotation;

public class ConditionExtractor {
	private C4JConditions conditions = new C4JConditions();
	private Map<IType, IType> externalContracts;

	public C4JConditions getConditionsOf(IJavaElement element) {
		if (element != null) {
			searchForExternalContracts();
			searchTypeHierachyForContractConditions(element);
		}
		return conditions;
	}

	private void searchForExternalContracts() {
		ExternalContractScanner externalContractScanner = new ExternalContractScanner();
		externalContractScanner.scan();
		externalContracts = externalContractScanner.getExternalContracts();
	}

	private void searchTypeHierachyForContractConditions(IJavaElement element) {
		IType[] typeHierachy = getTypeHierachy(element);

		for (int i = typeHierachy.length - 1; i >= 0; i--) {
			C4JConditions conditionsToMerge = searchTypeForContractConditions(element, typeHierachy[i]);
			IType relatedContract = typeHierachy[i];
			mergeConditions(conditionsToMerge, relatedContract);
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

	private C4JConditions searchTypeForContractConditions(IJavaElement element, IType type) {
		C4JContractReferenceAnnotation contractReference = new C4JContractReferenceAnnotation(type);

		IType contract = contractReference.exists() ? contractReference.getContractClass()
				: externalContracts.get(type);

		IMethod method = getMethodOfInterest(element, contract);

		return contract != null ? parseContract(contract, method) : new C4JConditions();
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
		ASTNode root = parse(contract);
		MethodVisitor methodVisitor = new MethodVisitor(method);
		root.accept(methodVisitor);
		return methodVisitor.getConditions();
	}

	private ASTNode parse(IType contract) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(contract.getCompilationUnit());
		parser.setResolveBindings(false);
		ASTNode root = parser.createAST(null);

		return root;
	}

	private void mergeConditions(C4JConditions conditionsToMerge, IType relatedContract) {
		if (conditions.canAddPreConditions())
			conditions.addPreConditions(conditionsToMerge.getConditions(C4JConditions.PRE_CONDITIONS));
		else {
			if (!conditionsToMerge.getConditions(C4JConditions.PRE_CONDITIONS).isEmpty()) {
				String relatedContractName = relatedContract.getElementName();
				String warning = MessageFormat.format("<br>WARNING: Found strengthening pre-condition "
						+ "in Contract ''{0}'' which is already defined from its super Contract "
						+ "- ignoring the pre-condition", relatedContractName);
				conditions.addWaringToConditions(C4JConditions.PRE_CONDITIONS, warning);
			}
		}

		conditions.addPostConditions(conditionsToMerge.getConditions(C4JConditions.POST_CONDITIONS));
		conditions
				.addInvariantConditions(conditionsToMerge.getConditions(C4JConditions.INVARIANT_CONDITIONS));
	}
}
