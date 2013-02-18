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
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.junit.Before;
import org.junit.Test;

import test.util.JavaProjectLoader;
import de.vksi.c4j.eclipse.plugin.internal.C4JConditions;
import de.vksi.c4j.eclipse.plugin.ui.text.hover.MethodVisitor;

public class MethodVisitorTest {
	private static final String PROJECTNAME = "TestProject";
	private static final String PATH_TO_DOT_PROJECT_FILE = "resources" + File.separator
			+ PROJECTNAME + "/.project";
	
	private static final String CONTRACT_COMPILATION_UNIT = "StackSpecContract.java";
	private static final String CONTRACT_COMPILATION_UNIT_WITH_CONSTRUCTOR = "StackDepthOfInheritance_1_Contract.java";
	private static final String TARGET_COMPILATION_UNIT = "StackDepthOfInheritance_1.java";
	private static final String METHOD_WITHOUT_CONDITIONS_NAME = "methodWithoutConditions";
	private static final String METHOD_WITHOUT_CONDITIONS_SIGNATURE = "()V"; //see: Signature javaDoc
	private static final String METHOD_NAME = "push";
	private static final String METHOD_SIGNATURE = "(QT;)V"; //see: Signature javaDoc
	private static final String CONSTRUCTOR_NAME = "StackDepthOfInheritance_1";
	private static final String CONSTRUCTOR_SIGNATURE = "(I)V"; //see: Signature javaDoc

	private IJavaProject javaProject;
	private MethodVisitor methodVisitor;

	@Before
	public void setUp() {
		IPath pathToDotProjectFile = getPathToDotProjectFile();
		javaProject = JavaProjectLoader.loadProject(PROJECTNAME, pathToDotProjectFile);
	}
	
	@Test
	public void testGetPreConditionsOfTargetConstructor() throws Exception {
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, TARGET_COMPILATION_UNIT);
		ICompilationUnit contractCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, CONTRACT_COMPILATION_UNIT_WITH_CONSTRUCTOR);
		ASTNode root = parseCompilationUnit(contractCompUnit);
		IMethod constructor = JavaProjectLoader.getMethod(targetCompUnit, CONSTRUCTOR_NAME, CONSTRUCTOR_SIGNATURE);
		methodVisitor = new MethodVisitor(constructor);
		root.accept(methodVisitor);
		
		List<String> expectedPreConditions = new ArrayList<String>();
		expectedPreConditions.add("capacity > 0 : \"capacity > 0\"");

		C4JConditions conditions = methodVisitor.getConditions();
		assertTrue(conditions.getConditions(C4JConditions.PRE_CONDITIONS).containsAll(expectedPreConditions));
	}

	@Test
	public void testGetPostConditionsOfTargetConstructor() throws Exception {
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, TARGET_COMPILATION_UNIT);
		ICompilationUnit contractCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, CONTRACT_COMPILATION_UNIT_WITH_CONSTRUCTOR);
		ASTNode root = parseCompilationUnit(contractCompUnit);
		IMethod constructor = JavaProjectLoader.getMethod(targetCompUnit, CONSTRUCTOR_NAME, CONSTRUCTOR_SIGNATURE);
		methodVisitor = new MethodVisitor(constructor);
		root.accept(methodVisitor);
		
		List<String> expectedPostConditions = new ArrayList<String>();
		expectedPostConditions.add("capacity() == capacity : \"capacity set\"");
		
		C4JConditions conditions = methodVisitor.getConditions();
		assertTrue(conditions.getConditions(C4JConditions.POST_CONDITIONS).containsAll(expectedPostConditions));
	}
	
	@Test
	public void testGetPreConditionsOfTargetMethod() throws Exception {
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, TARGET_COMPILATION_UNIT);
		ICompilationUnit contractCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, CONTRACT_COMPILATION_UNIT);
		ASTNode root = parseCompilationUnit(contractCompUnit);
		IMethod method = JavaProjectLoader.getMethod(targetCompUnit, METHOD_NAME, METHOD_SIGNATURE);
		methodVisitor = new MethodVisitor(method);
		root.accept(methodVisitor);
		
		List<String> expectedPreConditions = new ArrayList<String>();
		expectedPreConditions.add("item != null : \"item != null\"");
		expectedPreConditions.add("!isFull() : \"not isFull\"");
	
		C4JConditions conditions = methodVisitor.getConditions();
		assertTrue(conditions.getConditions(C4JConditions.PRE_CONDITIONS).containsAll(expectedPreConditions));
	}

	@Test
	public void testGetPostConditionsOfTargetMethod() throws Exception {
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, TARGET_COMPILATION_UNIT);
		ICompilationUnit contractCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, CONTRACT_COMPILATION_UNIT);
		ASTNode root = parseCompilationUnit(contractCompUnit);
		IMethod method = JavaProjectLoader.getMethod(targetCompUnit, METHOD_NAME, METHOD_SIGNATURE);
		methodVisitor = new MethodVisitor(method);
		root.accept(methodVisitor);
		
		List<String> expectedPostConditions = new ArrayList<String>();
		expectedPostConditions.add("top() == item : \"item set\"");
		expectedPostConditions.add("size() == old(size()) + 1 : \"size = old size + 1\"");
		expectedPostConditions.add("!isEmpty() : \"not isEmpty\"");
		
		C4JConditions conditions = methodVisitor.getConditions();
		assertTrue(conditions.getConditions(C4JConditions.POST_CONDITIONS).containsAll(expectedPostConditions));
	}

	@Test
	public void testTargetMethodWithoutConditions() throws Exception {
		ICompilationUnit targetCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, TARGET_COMPILATION_UNIT);
		ICompilationUnit contractCompUnit = JavaProjectLoader.getCompilationUnit(javaProject, CONTRACT_COMPILATION_UNIT);
		ASTNode root = parseCompilationUnit(contractCompUnit);
		IMethod method = JavaProjectLoader.getMethod(targetCompUnit, METHOD_WITHOUT_CONDITIONS_NAME, METHOD_WITHOUT_CONDITIONS_SIGNATURE);
		methodVisitor = new MethodVisitor(method);
		root.accept(methodVisitor);
		
		List<String> expectedPreConditions = new ArrayList<String>();
		List<String> expectedPostConditions = new ArrayList<String>();
		
		C4JConditions conditions = methodVisitor.getConditions();
		
		assertTrue(conditions.getConditions(C4JConditions.PRE_CONDITIONS).containsAll(expectedPreConditions));
		assertTrue(conditions.getConditions(C4JConditions.POST_CONDITIONS).containsAll(expectedPostConditions));
	}

	private static ASTNode parseCompilationUnit(ICompilationUnit compilationUnit) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(compilationUnit);
		parser.setResolveBindings(false);
		ASTNode root = parser.createAST(null);

		return root;
	}

	private static IPath getPathToDotProjectFile() {
		File file = new File(PATH_TO_DOT_PROJECT_FILE);
		return new Path(file.getAbsolutePath());
	}

}
