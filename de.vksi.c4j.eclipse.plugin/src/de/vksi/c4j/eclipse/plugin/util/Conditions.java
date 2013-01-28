package de.vksi.c4j.eclipse.plugin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Conditions {
	public static final String PRE_CONDITIONS = "Preconditions";
	public static final String POST_CONDITIONS = "Postconditions";
	public static final String INVARIANT_CONDITIONS = "Invariants";
	
	private Map<String, List<String>> map;
	
	public Conditions() {
		map = new HashMap<String, List<String>>();
		setPreConditions(new ArrayList<String>());
		setPostConditions(new ArrayList<String>());
		setInvariantConditions(new ArrayList<String>());
	}
	
	public void setPreConditions(List<String> preConditions){
		map.put(PRE_CONDITIONS, preConditions);
	}
	
	public void setPostConditions(List<String> postConditions){
		map.put(POST_CONDITIONS, postConditions);
	}
	
	public void setInvariantConditions(List<String> invariantConditions){
		map.put(INVARIANT_CONDITIONS, invariantConditions);
	}
	
	public List<String> getConditions(String kindOfCondition){
		return map.get(kindOfCondition);
	}
}
