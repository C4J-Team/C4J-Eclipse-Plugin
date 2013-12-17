package de.vksi.c4j.eclipse.plugin.internal;

import static org.junit.Assert.*;
import static test.util.TestConstants.ANNOTATION_CONTRACT;
import static test.util.TestConstants.PATH_TO_DOT_PROJECT_FILE;
import static test.util.TestConstants.PROJECTNAME;
import static test.util.TestConstants.TODS_CONTRACT;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;

import test.util.JavaProjectLoader;

public class C4JContractAnnotationTest {
	private C4JContractAnnotation c4JContractAnnotation;
	private IJavaProject javaProject;
	private IType contractType;

	@Before
	public void setUp() throws Exception {
		javaProject = JavaProjectLoader.loadProject(PROJECTNAME, PATH_TO_DOT_PROJECT_FILE);
		ICompilationUnit contractCompilationUnit = JavaProjectLoader.getCompilationUnit(javaProject,
				TODS_CONTRACT);
		contractType = JavaProjectLoader.getType(contractCompilationUnit);
		c4JContractAnnotation = new C4JContractAnnotation(contractType);
	}

	@Test
	public void testC4jAnnotationType() {
		c4JContractAnnotation = new C4JContractAnnotation(null);
		assertEquals(ANNOTATION_CONTRACT, c4JContractAnnotation.c4jAnnotationType());
	}

	@Test
	public void testContractAnnotationExists() throws JavaModelException {
		assertTrue(c4JContractAnnotation.exists());
	}

	@Test
	public void testReturnTargetClass() throws JavaModelException {
		assertNotNull(c4JContractAnnotation.getTargetClass());
	}

	@Test
	public void testGetAnnotation() {
		assertEquals(ANNOTATION_CONTRACT, c4JContractAnnotation.getAnnotation().getElementName());
	}

	@Test
	public void testTrueIfHasValue() {
		assertTrue(c4JContractAnnotation.hasValue());
	}

	@Test
	public void testReturnAnnotationValue() throws JavaModelException {
		assertTrue(!c4JContractAnnotation.getAnnotationValue().isEmpty());
	}

}
