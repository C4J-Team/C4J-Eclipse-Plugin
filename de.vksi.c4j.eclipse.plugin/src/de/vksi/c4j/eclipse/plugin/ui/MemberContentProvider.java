package de.vksi.c4j.eclipse.plugin.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;

public class MemberContentProvider implements ITreeContentAndDefaultSelectionProvider {
	private final Map<IType, List<IMethod>> methodsByType;
	private Object[] elements;
	private final ISelection defaultSelection;

	public MemberContentProvider(Collection<IType> types, Collection<IMethod> methods) {
		methodsByType = groupMethodsByType(methods);

		List<IType> sortedTypes = sortTypes(types);
		Set<IType> allTypes = new LinkedHashSet<IType>(sortedTypes);
		allTypes.addAll(sortTypes(methodsByType.keySet()));
		this.elements = allTypes.toArray();

		defaultSelection = getDefaultSelection(sortedTypes);
	}

	public MemberContentProvider(Collection<IType> types, IType typeProposedForSelection) {
		methodsByType = new HashMap<IType, List<IMethod>>();

		List<IType> sortedTypes = sortTypes(types);
		this.elements = sortedTypes.toArray();

		defaultSelection = getDefaultSelection(sortedTypes);
	}

	private Map<IType, List<IMethod>> groupMethodsByType(Collection<IMethod> methods) {
		Map<IType, List<IMethod>> methodsByType = new LinkedHashMap<IType, List<IMethod>>();
		for (IMethod method : methods) {
			IType type = method.getDeclaringType();
			List<IMethod> methodsForType = methodsByType.get(type);
			if (methodsForType == null) {
				methodsForType = new ArrayList<IMethod>();
				methodsByType.put(type, methodsForType);
			}
			methodsForType.add(method);
		}

		for (List<IMethod> typeMethods : methodsByType.values()) {
			Collections.sort(typeMethods, new MethodComparator());
		}

		return methodsByType;
	}

	private List<IType> sortTypes(Collection<IType> types) {
		List<IType> list = new ArrayList<IType>(types);
		Collections.sort(list, new TypeComparator());
		return list;
	}

	private List<TreeActionElement<?>> sortTreeElementActions(Collection<TreeActionElement<?>> actions) {
		List<TreeActionElement<?>> list = new ArrayList<TreeActionElement<?>>(actions);
		Collections.sort(list, new TreeActionElementComparator());
		return list;
	}

	private ISelection getDefaultSelection(List<IType> types) {
		IMember defaultSelectedMember = null;
		if (!types.isEmpty()) {
			defaultSelectedMember = types.get(0);
			List<IMethod> methods = methodsByType.get(defaultSelectedMember);
			if (methods != null && !methods.isEmpty()) {
				defaultSelectedMember = methods.get(0);
			}
		}
		return defaultSelectedMember == null ? null : new StructuredSelection(defaultSelectedMember);
	}

	public Object[] getChildren(Object parentElement) {
		List<IMethod> children = methodsByType.get(parentElement);
		return children == null ? new IMethod[0] : children.toArray();
	}

	public Object getParent(Object element) {
		return element instanceof IType ? null : ((IMethod) element).getDeclaringType();
	}

	public boolean hasChildren(Object element) {
		return element instanceof IType && methodsByType.get(element) != null;
	}

	public Object[] getElements(Object inputElement) {
		return elements;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public ISelection getDefaultSelection() {
		return defaultSelection;
	}

	public MemberContentProvider withAction(TreeActionElement<?> action) {
		List<Object> elements = new ArrayList<Object>();
		Collections.addAll(elements, this.elements);

		addSeparatorElement(elements);

		elements.add(action);

		this.elements = elements.toArray();
		return this;
	}

	public MemberContentProvider withAction(Set<TreeActionElement<?>> actions) {
		List<Object> elements = new ArrayList<Object>();

		elements.addAll(sortTreeElementActions(actions));
		elements.addAll(Arrays.asList(this.elements));

		this.elements = elements.toArray();
		return this;
	}

	private void addSeparatorElement(List<Object> elements) {
		if (elements.isEmpty()) {
			elements.add(new SeparatorElement());
			return;
		}

		if (!elements.isEmpty() && !(elements.get(elements.size() - 1) instanceof SeparatorElement))
			elements.add(new SeparatorElement());
	}
}
