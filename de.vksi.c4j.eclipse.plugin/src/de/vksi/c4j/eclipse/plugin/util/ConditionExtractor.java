package de.vksi.c4j.eclipse.plugin.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CLASS_INVARIANT;

public class ConditionExtractor {

	public static Conditions getConditionsOf(IType type) {
		if (type == null)
			return new Conditions();
		
		IType[] typeHierachy = getTypeHierachy(type);
		
		Conditions conditions = parseTypeHierachy(null, typeHierachy);
		
		return conditions;
	}

	public static Conditions getConditionsOf(IMethod method) {
		if (method == null)
			return new Conditions();
		
		IType[] typeHierachy = getTypeHierachy(method);
		
		Conditions conditions = parseTypeHierachy(method, typeHierachy);

		return conditions;
	}

	private static Conditions parseTypeHierachy(IMethod method, IType[] typeHierachy) {
		Conditions conditions = new Conditions();
		Conditions conditionsToMerge;
		
		for (int i = typeHierachy.length - 1; i >= 0; i--) {
			conditionsToMerge = parseTypes(method, typeHierachy[i]);
			mergeConditions(conditions, conditionsToMerge);
		}

		return conditions;
	}

	private static Conditions parseTypes(IMethod method, IType type) {
		ContractReferenceAnnotation contractReference = new ContractReferenceAnnotation(type);

		if (contractReference.exists()) {
			try {
				IType contract = contractReference.getContractClass();
				ASTNode root = parse(contract);
				
				if(method == null)
					method = getClassInvariantsMethod(contract);
				
				MethodVisitor methodVisitor = new MethodVisitor(method);
				root.accept(methodVisitor);
				return methodVisitor.getConditions();

			} catch (JavaModelException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				// contract not found
				e.printStackTrace();
			}
		}
		return new Conditions();
	}

	private static IMethod getClassInvariantsMethod(IType contract) throws JavaModelException {
		for (IMethod method : contract.getMethods()) {
			for (IAnnotation annotation : method.getAnnotations()) {
				if (ANNOTATION_CLASS_INVARIANT.equals(annotation.getElementName())) {
					return method;
				}
			}
		}
		return null;
	}

	private static void mergeConditions(Conditions finalConditions, Conditions conditionsToMerge) {
		if (!conditionsToMerge.getConditions(Conditions.PRE_CONDITIONS).isEmpty()) {
			finalConditions.setPreConditions(conditionsToMerge.getConditions(Conditions.PRE_CONDITIONS));
		}

		if (!conditionsToMerge.getConditions(Conditions.POST_CONDITIONS).isEmpty()) {
			Set<String> mergedPostConditions = new HashSet<String>();
			mergedPostConditions.addAll(finalConditions.getConditions(Conditions.POST_CONDITIONS));
			mergedPostConditions.addAll(conditionsToMerge.getConditions(Conditions.POST_CONDITIONS));

			finalConditions.setPostConditions(new ArrayList<String>(mergedPostConditions));
		}

		if (!conditionsToMerge.getConditions(Conditions.INVARIANT_CONDITIONS).isEmpty()) {
			Set<String> mergedInvariantConditions = new HashSet<String>();
			mergedInvariantConditions.addAll(finalConditions.getConditions(Conditions.INVARIANT_CONDITIONS));
			mergedInvariantConditions
					.addAll(conditionsToMerge.getConditions(Conditions.INVARIANT_CONDITIONS));

			finalConditions.setInvariantConditions(new ArrayList<String>(mergedInvariantConditions));
		}

	}
	
	private static IType[] getTypeHierachy(IMethod method) {
		IType type = (IType) method.getAncestor(IJavaElement.TYPE);
		return getTypeHierachy(type);
	}

	private static IType[] getTypeHierachy(IType type) {
		try {
			ITypeHierarchy typeHierarchy = type.newTypeHierarchy(null);
			IType[] allSupertypes = typeHierarchy.getAllSupertypes(type);
			IType[] allSupertypesInclusiveCurrentType = new IType[allSupertypes.length+1];
			
			allSupertypesInclusiveCurrentType[0] = type;
			for (int i = 0; i < allSupertypes.length; i++) {
				allSupertypesInclusiveCurrentType[i+1] = allSupertypes[i];
			}
			
			return allSupertypesInclusiveCurrentType;

		} catch (JavaModelException e) {
			return new IType[0];
		}

	}

	private static ASTNode parse(IType contract) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(contract.getCompilationUnit());
		parser.setResolveBindings(false);
		ASTNode root = parser.createAST(null);

		return root;
	}
}
