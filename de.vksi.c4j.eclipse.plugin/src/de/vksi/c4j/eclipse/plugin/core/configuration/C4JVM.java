package de.vksi.c4j.eclipse.plugin.core.configuration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;


public class C4JVM {
	private static final String C4J_NAME_POSTIFX = " C4J";
	private static final String C4J_ID_POSTIFX = " c4j";
	private static final String EA_JAVAAGENT = "-ea -javaagent: ";
	
	private VMDuplicator duplicator;
	private String currentVMName = "";
	private String currentVMID = "";
	
	public C4JVM(IJavaProject javaProject){
		this.duplicator = new VMDuplicator(javaProject);
		getNameAndIDfromCurrentVM(javaProject);
	}
	
	public void setUpC4JVM(String jarLocation) throws CoreException {
		this.duplicator.duplicateVM(this.currentVMName + C4J_NAME_POSTIFX, this.currentVMID + C4J_ID_POSTIFX);
		this.duplicator.setVMArgumentForDuplicate(EA_JAVAAGENT + jarLocation);
		this.duplicator.convertDuplicateToRealVM();
	}
	
	private void getNameAndIDfromCurrentVM(IJavaProject javaProject) {
		try {
			IVMInstall vmInstall = JavaRuntime.getVMInstall(javaProject);
			this.currentVMID = vmInstall.getId();
			this.currentVMName = vmInstall.getName();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
