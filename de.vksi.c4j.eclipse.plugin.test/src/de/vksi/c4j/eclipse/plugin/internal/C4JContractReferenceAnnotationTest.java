package de.vksi.c4j.eclipse.plugin.internal;

import static org.junit.Assert.*;
import static test.util.TestConstants.ANNOTATION_CONTRACT_REFERENCE;
import static test.util.TestConstants.PATH_TO_DOT_PROJECT_FILE;
import static test.util.TestConstants.PROJECTNAME;
import static test.util.TestConstants.TARGET_SOURCEFILE_DOI_0;
import static test.util.TestConstants.CONTRACT_SOURCEFILE_DOI_0;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;

import test.util.JavaProjectLoader;

public class C4JContractReferenceAnnotationTest {
	private C4JContractReferenceAnnotation c4jContractRefAnnotation;
	private IJavaProject javaProject;
	private IType targetType;
	
	@Before
	public void setUp() throws Exception {
		javaProject = JavaProjectLoader.loadProject(PROJECTNAME, PATH_TO_DOT_PROJECT_FILE);
		ICompilationUnit targetCompilationUnit = JavaProjectLoader.getCompilationUnit(javaProject, TARGET_SOURCEFILE_DOI_0);
		targetType = JavaProjectLoader.getType(targetCompilationUnit);
		c4jContractRefAnnotation = new C4JContractReferenceAnnotation(targetType);
	}

	@Test
	public void testC4jAnnotationType() {
		assertEquals(ANNOTATION_CONTRACT_REFERENCE, c4jContractRefAnnotation.c4jAnnotationType());
	}

	@Test
	public void testGetContractClass() throws JavaModelException {
		ICompilationUnit contractCompilationUnit = JavaProjectLoader.getCompilationUnit(javaProject, CONTRACT_SOURCEFILE_DOI_0);
		IType contractType = JavaProjectLoader.getType(contractCompilationUnit);
		
		assertEquals(contractType, c4jContractRefAnnotation.getContractClass());
	}

	@Test
	public void testReturnNullIfNotExistAndNotHasValueContractClass() throws JavaModelException {
		c4jContractRefAnnotation = new C4JContractReferenceAnnotation(null);
		assertEquals(null, c4jContractRefAnnotation.getContractClass());
	}

	@Test
	public void testExists() {
		assertTrue(c4jContractRefAnnotation.exists());
	}

	@Test
	public void testGetAnnotation() {
		assertEquals(ANNOTATION_CONTRACT_REFERENCE, c4jContractRefAnnotation.getAnnotation().getElementName());
	}

	@Test
	public void testHasValue() {
		assertTrue(c4jContractRefAnnotation.hasValue());
	}

	@Test
	public void testGetAnnotationValue() throws JavaModelException {
		ICompilationUnit contractCompilationUnit = JavaProjectLoader.getCompilationUnit(javaProject, CONTRACT_SOURCEFILE_DOI_0);
		IType contractType = JavaProjectLoader.getType(contractCompilationUnit);
		
		assertEquals(contractType.getElementName(), c4jContractRefAnnotation.getAnnotationValue());
	}
	

}
