package de.vksi.c4j.eclipse.plugin.util;

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
	
	public void addPreConditions(List<String> preConditions){
		if (canAddPreConditions())
			map.put(PRE_CONDITIONS, new ArrayList<String>(preConditions));
	}
	
	
	public void addPostConditions(List<String> postConditions){
		if (!postConditions.isEmpty()) {
			Set<String> mergedPostConditions = new HashSet<String>();
			mergedPostConditions.addAll(getConditions(C4JConditions.POST_CONDITIONS));
			mergedPostConditions.addAll(postConditions);

			map.put(POST_CONDITIONS, new ArrayList<String>(mergedPostConditions));
		}
	}
	
	public void addInvariantConditions(List<String> invariantConditions){
		if (!invariantConditions.isEmpty()) {
			Set<String> mergedInvariantConditions = new HashSet<String>();
			mergedInvariantConditions.addAll(getConditions(C4JConditions.INVARIANT_CONDITIONS));
			mergedInvariantConditions.addAll(invariantConditions);

			map.put(INVARIANT_CONDITIONS, new ArrayList<String>(mergedInvariantConditions));
		}
	}
	
	public List<String> getConditions(String kindOfCondition){
		return map.get(kindOfCondition);
	}

	public boolean canAddPreConditions(){
		return getConditions(PRE_CONDITIONS).isEmpty();
	}

	public void addWaringToConditions(String kindOfConditions, String warning){
		getConditions(kindOfConditions).add(warning);
	}
	
	private void init(){
		map.put(PRE_CONDITIONS, new ArrayList<String>());
		map.put(POST_CONDITIONS, new ArrayList<String>());
		map.put(INVARIANT_CONDITIONS, new ArrayList<String>());
	}
}
