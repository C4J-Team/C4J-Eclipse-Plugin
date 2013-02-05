package de.vksi.c4j.eclipse.plugin.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.junit.Before;
import org.junit.Test;

import de.vksi.c4j.eclipse.plugin.ui.text.hover.ConditionExtractor;

import test.util.JavaProjectLoader;

public class ConditionExtractorTest {
	private static final String PROJECTNAME = "TestProject";
	private static final String PATH_TO_DOT_PROJECT_FILE = "resources" + File.separator
			+ PROJECTNAME + "/.project";
	
	//Depth Of Inheritance (DOI) is 0 -> Base Class
	private static final String DOI_0_TARGET_COMPILATION_UNIT = "StackSpec.java";
	//Depth Of Inheritance (DOI) is 1
	private static final String DOI_1_TARGET_COMPILATION_UNIT = "StackDepthOfInheritance_1.java";
	//Depth Of Inheritance (DOI) is 2
	private static final String DOI_2_TARGET_COMPILATION_UNIT = "StackDepthOfInheritance_2.java";
	//Depth Of Inheritance (DOI) is 3
	private static final String DOI_3_TARGET_COMPILATION_UNIT = "StackDepthOfInheritance_3.java";
	
	//Target Method
	private static final String METHOD_NAME = "push";
	private static final String METHOD_SIGNATURE = "(QT;)V"; //see: Signature javaDoc

	private IJavaProject javaProject;

	@Before
	public void setUp() throws Exception {
		IPath pathToDotProjectFile = getPathToDotProjectFile();
		javaProject = JavaProjectLoader.loadProject(PROJECTNAME, pathToDotProjectFile);
	}
	
	@Test
	public void testGetConitionsOfTargetMethod_DepthOfInheritance_0() throws Exception {
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, DOI_0_TARGET_COMPILATION_UNIT);
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
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, DOI_1_TARGET_COMPILATION_UNIT);
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
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, DOI_2_TARGET_COMPILATION_UNIT);
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
	public void testGetConitionsOfTargetMethod_DepthOfInheritance_3() throws Exception {
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, DOI_3_TARGET_COMPILATION_UNIT);
		IMethod method = JavaProjectLoader.getMethod(targetCompUnit, METHOD_NAME, METHOD_SIGNATURE);
		
		C4JConditions conditionsOfMethod = new ConditionExtractor().getConditionsOf(method);
		
		List<String> expectedPreConditions = new ArrayList<String>();
		expectedPreConditions.add("!isFull() : \"not isFull\"");
		
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
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, DOI_0_TARGET_COMPILATION_UNIT);
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
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, DOI_3_TARGET_COMPILATION_UNIT);
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
	
	
	private static IPath getPathToDotProjectFile() {
		File file = new File(PATH_TO_DOT_PROJECT_FILE);
		return new Path(file.getAbsolutePath());
	}
}
