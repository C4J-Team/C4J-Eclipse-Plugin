package de.vksi.c4j.eclipse.plugin.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class C4JConditions {
	public static final String PRE_CONDITIONS = "Preconditions";
	public static final String POST_CONDITIONS = "Postconditions";
	public static final String INVARIANT_CONDITIONS = "Invariants";

	private Map<String, List<String>> map;

	public C4JConditions() {
		map = new HashMap<String, List<String>>();
		init();
	}

	public void setPreConditions(List<String> preConditions) {
		map.put(PRE_CONDITIONS, preConditions);
	}

	public void setPostConditions(List<String> postConditions) {
		map.put(POST_CONDITIONS, postConditions);
	}

	public void setInvariantConditions(List<String> invariantConditions) {
		map.put(INVARIANT_CONDITIONS, invariantConditions);
	}

	public List<String> getConditions(String kindOfCondition) {
		return map.get(kindOfCondition);
	}

	public boolean hasPreConditions() {
		return !getConditions(PRE_CONDITIONS).isEmpty();
	}

	public void addWaringToConditions(String kindOfConditions, String warning) {
		getConditions(kindOfConditions).add(warning);
	}

	public void mergeWith(C4JConditions conditionsToMerge) {
		addInvariantConditions(conditionsToMerge.getConditions(C4JConditions.INVARIANT_CONDITIONS));
		addPreConditions(conditionsToMerge.getConditions(C4JConditions.PRE_CONDITIONS));
		addPostConditions(conditionsToMerge.getConditions(C4JConditions.POST_CONDITIONS));
	}

	private void init() {
		map.put(PRE_CONDITIONS, new ArrayList<String>());
		map.put(POST_CONDITIONS, new ArrayList<String>());
		map.put(INVARIANT_CONDITIONS, new ArrayList<String>());
	}

	private void addPreConditions(List<String> preConditions) {
		if (!preConditions.isEmpty()) {
			Set<String> mergedPostConditions = new HashSet<String>();
			mergedPostConditions.addAll(getConditions(C4JConditions.PRE_CONDITIONS));
			mergedPostConditions.addAll(preConditions);

			map.put(PRE_CONDITIONS, new ArrayList<String>(mergedPostConditions));
		}
	}

	private void addPostConditions(List<String> postConditions) {
		if (!postConditions.isEmpty()) {
			Set<String> mergedPostConditions = new HashSet<String>();
			mergedPostConditions.addAll(getConditions(C4JConditions.POST_CONDITIONS));
			mergedPostConditions.addAll(postConditions);

			map.put(POST_CONDITIONS, new ArrayList<String>(mergedPostConditions));
		}
	}

	private void addInvariantConditions(List<String> invariantConditions) {
		if (!invariantConditions.isEmpty()) {
			Set<String> mergedInvariantConditions = new HashSet<String>();
			mergedInvariantConditions.addAll(getConditions(C4JConditions.INVARIANT_CONDITIONS));
			mergedInvariantConditions.addAll(invariantConditions);

			map.put(INVARIANT_CONDITIONS, new ArrayList<String>(mergedInvariantConditions));
		}
	}
}
