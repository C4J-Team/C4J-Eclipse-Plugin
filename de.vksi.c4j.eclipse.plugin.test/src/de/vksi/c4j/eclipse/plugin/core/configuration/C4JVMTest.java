package de.vksi.c4j.eclipse.plugin.core.configuration;

import static org.junit.Assert.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.util.JavaProjectCreator;

public class C4JVMTest {

	private static final String PROJECT_LOC = "${project_loc:virtualTestProject}/";
	private static final String DUMMY_JAR = "dummy.jar";
	private static final String C4J = "C4J";
	private static final String EA_JAVAAGENT = "-ea -javaagent:";
	private static final String PROJECT_NAME = "virtualTestProject";
	private IJavaProject javaProject;
	private C4JVM c4jVM;

	@Before
	public void setUp() throws CoreException {
		this.javaProject = JavaProjectCreator.create(PROJECT_NAME);
		this.c4jVM = new C4JVM(this.javaProject);
	}
	

	@Test
	public void testSetUpC4JVM() throws CoreException {
		this.c4jVM.setUpC4JVM(javaProject.getProject().getFile(DUMMY_JAR));
		
		IVMInstall vmInstall = JavaRuntime.getVMInstall(this.javaProject);
		String nameAfterC4JSetup = vmInstall.getName();
		String vmArguments = arrayToString(vmInstall.getVMArguments());
		
		String expectedArguments = EA_JAVAAGENT + PROJECT_LOC + DUMMY_JAR;
		
		assertTrue(nameAfterC4JSetup.contains(C4J));
		assertEquals(expectedArguments, vmArguments);
	}
	
	private String arrayToString(String[] arr){
		String str = "";
		
		for (String string : arr) {
			str += " " + string;
		}
		
		return str.trim();
	}
	
	@After
	public void tearDown() throws CoreException {
		IProject project = this.javaProject.getProject();
		project.delete(false, null);
	}
}
