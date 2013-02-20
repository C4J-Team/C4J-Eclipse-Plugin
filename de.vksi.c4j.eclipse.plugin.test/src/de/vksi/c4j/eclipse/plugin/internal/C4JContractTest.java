package de.vksi.c4j.eclipse.plugin.internal;

import static org.junit.Assert.*;
import static test.util.TestConstants.TODS;
import static test.util.TestConstants.CONTRACT_SOURCEFILE_DOI_0;
import static test.util.TestConstants.PATH_TO_DOT_PROJECT_FILE;
import static test.util.TestConstants.PROJECTNAME;
import static test.util.TestConstants.TODS_CONTRACT;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;

import test.util.JavaProjectLoader;

public class C4JContractTest {
	private C4JContract c4jcontract;
	private IJavaProject javaProject;
	private IType contractType;
	
	@Before
	public void setUp() throws Exception {
		javaProject = JavaProjectLoader.loadProject(PROJECTNAME, PATH_TO_DOT_PROJECT_FILE);
		ICompilationUnit contractCompilationUnit = JavaProjectLoader.getCompilationUnit(javaProject, TODS_CONTRACT);
		contractType = JavaProjectLoader.getType(contractCompilationUnit);
		c4jcontract = new C4JContract(contractType);
	}

	@Test
	public void testHasMethod() throws JavaModelException {
		for (IMethod method : contractType.getMethods()) {
			assertTrue(c4jcontract.hasMethod(method));
		}
	}

	@Test
	public void testGetTarget() throws JavaModelException {
		ICompilationUnit targetCompilationUnit = JavaProjectLoader.getCompilationUnit(javaProject, TODS);
		IType targetType = JavaProjectLoader.getType(targetCompilationUnit);
		
		assertEquals(targetType, c4jcontract.getTarget());
	}

	@Test
	public void testGetTargetSetByInstantiation() throws JavaModelException {
		ICompilationUnit targetCompilationUnit = JavaProjectLoader.getCompilationUnit(javaProject, TODS);
		IType targetType = JavaProjectLoader.getType(targetCompilationUnit);
		
		c4jcontract = new C4JContract(targetType, contractType);
		
		assertEquals(targetType, c4jcontract.getTarget());
	}

	@Test
	public void testExists() {
		assertTrue(c4jcontract.exists());
	}

	@Test
	public void testNotExists() {
		c4jcontract = new C4JContract(null);
		assertFalse(c4jcontract.exists());
	}

	@Test
	public void testIsExternal() {
		assertTrue(c4jcontract.isExternal());
	}
	
	@Test
	public void testIsNotExternal() throws JavaModelException {
		ICompilationUnit contractCompilationUnit = JavaProjectLoader.getCompilationUnit(javaProject, CONTRACT_SOURCEFILE_DOI_0);
		IType internalContractType = JavaProjectLoader.getType(contractCompilationUnit);
		
		c4jcontract = new C4JContract(internalContractType);
		assertFalse(c4jcontract.isExternal());
	}

	@Test
	public void testGetContract() {
		assertEquals(contractType, c4jcontract.getContract());
	}

}
