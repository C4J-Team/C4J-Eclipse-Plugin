package de.vksi.c4j.eclipse.plugin.core.configuration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;


public class C4JVM {
	private static final String C4j_NAME_POSTIFX = " C4J";
	private static final String C4j_ID_POSTIFX = " c4j";
	
	private DuplicateVM duplicator;
	
	public C4JVM(IJavaProject javaProject){
		this.duplicator = new DuplicateVM(javaProject);
	}
	
	public void setUpC4JVM(String jarLocation) throws CoreException {
		
		this.duplicator.duplicateVM(C4j_NAME_POSTIFX, C4j_ID_POSTIFX);
		this.duplicator.setVMArgument(jarLocation);
		this.duplicator.convertDuplicateToRealVM();
		
	}
}
