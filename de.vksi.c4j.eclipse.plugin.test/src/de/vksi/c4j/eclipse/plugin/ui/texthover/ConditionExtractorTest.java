package de.vksi.c4j.eclipse.plugin.ui.texthover;

import static org.junit.Assert.assertTrue;
import static test.util.TestConstants.PATH_TO_DOT_PROJECT_FILE;
import static test.util.TestConstants.PROJECTNAME;
import static test.util.TestConstants.TARGET_SOURCEFILE_DOI_0;
import static test.util.TestConstants.TARGET_SOURCEFILE_DOI_1;
import static test.util.TestConstants.TARGET_SOURCEFILE_DOI_2;
import static test.util.TestConstants.TARGET_SOURCEFILE_DOI_3;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.junit.Before;
import org.junit.Test;

import test.util.JavaProjectLoader;
import de.vksi.c4j.eclipse.plugin.internal.C4JConditions;
import de.vksi.c4j.eclipse.plugin.ui.texthover.ConditionExtractor;

public class ConditionExtractorTest {


	
	//Target Method
	private static final String METHOD_NAME = "push";
	private static final String METHOD_SIGNATURE = "(QT;)V"; //see: Signature javaDoc

	private IJavaProject javaProject;

	@Before
	public void setUp() throws Exception {
		javaProject = JavaProjectLoader.loadProject(PROJECTNAME, PATH_TO_DOT_PROJECT_FILE);
	}
	
	@Test
	public void testGetConitionsOfTargetMethod_DepthOfInheritance_0() throws Exception {
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, TARGET_SOURCEFILE_DOI_0);
		IMethod method = JavaProjectLoader.getMethod(targetCompUnit, METHOD_NAME, METHOD_SIGNATURE);

		C4JConditions conditionsOfMethod = new ConditionExtractor().getConditionsOf(method);
		
		List<String> expectedPreConditions = new ArrayList<String>();
		expectedPreConditions.add("item != null : \"item != null\"");
		expectedPreConditions.add("!isFull() : \"not isFull\"");
		
		List<String> expectedPostConditions = new ArrayList<String>();
		expectedPostConditions.add("top() == item : \"item set\"");
		expectedPostConditions.add("size() == old(size()) + 1 : \"size = old size + 1\"");
		expectedPostConditions.add("!isEmpty() : \"not isEmpty\"");
		
		assertTrue(conditionsOfMethod.getConditions(C4JConditions.PRE_CONDITIONS).containsAll(expectedPreConditions));
		assertTrue(conditionsOfMethod.getConditions(C4JConditions.POST_CONDITIONS).containsAll(expectedPostConditions));
	}

	
	@Test
	public void testGetConitionsOfTargetMethod_DepthOfInheritance_1() throws Exception {
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, TARGET_SOURCEFILE_DOI_1);
		IMethod method = JavaProjectLoader.getMethod(targetCompUnit, METHOD_NAME, METHOD_SIGNATURE);
		
		C4JConditions conditionsOfMethod = new ConditionExtractor().getConditionsOf(method);
		
		List<String> expectedPreConditions = new ArrayList<String>();
		expectedPreConditions.add("item != null : \"item != null\"");
		expectedPreConditions.add("!isFull() : \"not isFull\"");
		
		List<String> expectedPostConditions = new ArrayList<String>();
		expectedPostConditions.add("top() == item : \"item set\"");
		expectedPostConditions.add("size() == old(size()) + 1 : \"size = old size + 1\"");
		expectedPostConditions.add("!isEmpty() : \"not isEmpty\"");
		
		assertTrue(conditionsOfMethod.getConditions(C4JConditions.PRE_CONDITIONS).containsAll(expectedPreConditions));
		assertTrue(conditionsOfMethod.getConditions(C4JConditions.POST_CONDITIONS).containsAll(expectedPostConditions));
	}

	@Test
	public void testGetConitionsOfTargetMethod_DepthOfInheritance_2() throws Exception {
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, TARGET_SOURCEFILE_DOI_2);
		IMethod method = JavaProjectLoader.getMethod(targetCompUnit, METHOD_NAME, METHOD_SIGNATURE);
		
		C4JConditions conditionsOfMethod = new ConditionExtractor().getConditionsOf(method);
		
		List<String> expectedPreConditions = new ArrayList<String>();
		expectedPreConditions.add("item != null : \"item != null\"");
		expectedPreConditions.add("!isFull() : \"not isFull\"");
		
		List<String> expectedPostConditions = new ArrayList<String>();
		expectedPostConditions.add("top() == item : \"item set\"");
		expectedPostConditions.add("size() == old(size()) + 1 : \"size = old size + 1\"");
		expectedPostConditions.add("!isEmpty() : \"not isEmpty\"");
		
		assertTrue(conditionsOfMethod.getConditions(C4JConditions.PRE_CONDITIONS).containsAll(expectedPreConditions));
		assertTrue(conditionsOfMethod.getConditions(C4JConditions.POST_CONDITIONS).containsAll(expectedPostConditions));
	}

	@Test
	public void testGetConitionsOfTargetMethod_DepthOfInheritance_3_PreConditionViolation() throws Exception {
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, TARGET_SOURCEFILE_DOI_3);
		IMethod method = JavaProjectLoader.getMethod(targetCompUnit, METHOD_NAME, METHOD_SIGNATURE);
		
		C4JConditions conditionsOfMethod = new ConditionExtractor().getConditionsOf(method);
		
		List<String> expectedPreConditions = new ArrayList<String>();
		expectedPreConditions.add("item != null : \"item != null\"");
		expectedPreConditions.add("!isFull() : \"not isFull\"");
		expectedPreConditions.add("<br>WARNING: Found strengthening pre-condition in Contract 'StackDepthOfInheritance_3_Contract' which is already defined from its super Contract - ignoring the pre-condition");
		
		List<String> expectedPostConditions = new ArrayList<String>();
		expectedPostConditions.add("top() == item : \"item set\"");
		expectedPostConditions.add("size() == old(size()) + 1 : \"size = old size + 1\"");
		expectedPostConditions.add("!isEmpty() : \"not isEmpty\"");
		expectedPostConditions.add("\"newCondition\".equals(\"newCondition\") : \"some additional condition\"");
		
		assertTrue(conditionsOfMethod.getConditions(C4JConditions.PRE_CONDITIONS).containsAll(expectedPreConditions));
		assertTrue(conditionsOfMethod.getConditions(C4JConditions.POST_CONDITIONS).containsAll(expectedPostConditions));
	}
	
	@Test
	public void testReturnEmptyConditionsIfTargetMethodIsNull() throws Exception {
		IMethod method = null;
		C4JConditions conditionsOfMethod = new ConditionExtractor().getConditionsOf(method);

		assertTrue(conditionsOfMethod.getConditions(C4JConditions.PRE_CONDITIONS).isEmpty());
		assertTrue(conditionsOfMethod.getConditions(C4JConditions.POST_CONDITIONS).isEmpty());
	}
	
	@Test
	public void testGetInvariantsOfTargetType_DepthOfInheritance_0() throws Exception {
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, TARGET_SOURCEFILE_DOI_0);
		IType type = JavaProjectLoader.getType(targetCompUnit);
		
		C4JConditions conditionsOfMethod = new ConditionExtractor().getConditionsOf(type);
		
		List<String> expectedInvariantConditions = new ArrayList<String>();
		expectedInvariantConditions.add("capacity() == constCapacity : \"capacity is immutable\"");
		expectedInvariantConditions.add("capacity() > 0 : \"capacity > 0\"");
		expectedInvariantConditions.add("size() >= 0 : \"size >= 0\"");
		expectedInvariantConditions.add("size() <= capacity() : \"size <= capacity\"");
		expectedInvariantConditions.add("top() != null : \"if not isEmpty then top != null\"");
		
		assertTrue(conditionsOfMethod.getConditions(C4JConditions.INVARIANT_CONDITIONS).containsAll(expectedInvariantConditions));
	}

	@Test
	public void testGetInvariantsOfTargetType_DepthOfInheritance_3() throws Exception {
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, TARGET_SOURCEFILE_DOI_3);
		IType type = JavaProjectLoader.getType(targetCompUnit);
		
		C4JConditions conditionsOfMethod = new ConditionExtractor().getConditionsOf(type);
		
		List<String> expectedInvariantConditions = new ArrayList<String>();
		expectedInvariantConditions.add("capacity() == constCapacity : \"capacity is immutable\"");
		expectedInvariantConditions.add("capacity() > 0 : \"capacity > 0\"");
		expectedInvariantConditions.add("size() >= 0 : \"size >= 0\"");
		expectedInvariantConditions.add("size() <= capacity() : \"size <= capacity\"");
		expectedInvariantConditions.add("top() != null : \"if not isEmpty then top != null\"");
		expectedInvariantConditions.add("\"foo\".equals(\"baa\") : \"some additional condition\"");
		
		assertTrue(conditionsOfMethod.getConditions(C4JConditions.INVARIANT_CONDITIONS).containsAll(expectedInvariantConditions));
	}
	
	@Test
	public void testReturnEmptyConditionsIfTargetTypeIsNull() throws Exception {
		IType type = null;
		C4JConditions conditionsOfMethod = new ConditionExtractor().getConditionsOf(type);

		assertTrue(conditionsOfMethod.getConditions(C4JConditions.INVARIANT_CONDITIONS).isEmpty());
	}
}
