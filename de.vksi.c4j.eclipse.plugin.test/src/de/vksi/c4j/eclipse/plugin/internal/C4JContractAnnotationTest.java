package de.vksi.c4j.eclipse.plugin.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import test.util.JavaProjectLoader;

@RunWith(MockitoJUnitRunner.class)
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
	public void testReturnNullIfHasNoValueTargetClass() throws JavaModelException {
		assertEquals(null, c4JContractAnnotation.getTargetClass());
	}

	@Test
	public void testGetAnnotation() {
		assertEquals(ANNOTATION_CONTRACT, c4JContractAnnotation.getAnnotation().getElementName());
	}

	@Test
	public void testFalseIfHasNoValue() {
		assertFalse(c4JContractAnnotation.hasValue());
	}

	@Test
	public void testReturnEmptyStringIfHasNoAnnotationValue() throws JavaModelException {
		assertEquals("", c4JContractAnnotation.getAnnotationValue());
	}

}
