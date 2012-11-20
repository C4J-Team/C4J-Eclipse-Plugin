package de.vksi.c4j.eclipse.plugin.core.configuration;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.vksi.c4j.eclipse.plugin.core.configuration.ConvertProject;

public class ConvertProjectTest {

	private static final String LIB_FOLDER_NAME = "lib";
	private static final String PROJECT_NAME = "testProject";

	private static final String C4J_JAR = "c4j-6.0.0.jar";
	private static final String JAVA_ASSIST_JAR = "javassist-3.16.1-GA.jar";
	private static final String LOG4J_JAR = "log4j-1.2.16.jar";

	private JavaProject javaProject;
	private IJavaProject projectToConvert;
	private ConvertProject converter;

	@Before
	public void SetUp() throws CoreException {
		
		this.javaProject = new JavaProject();
		this.projectToConvert = this.javaProject.create(PROJECT_NAME);
		converter = new ConvertProject();
	}

	@Test
	public void libFolderHasBeenCreatedTest() throws CoreException {
		converter.toC4JProject(this.projectToConvert);
		
		IFolder libFolder = getLibFolder();

		assertTrue(libFolder.exists());
	}
	
	@Test
	public void existingLibFolderWillNotBeReplacedTest() throws Exception {
		this.javaProject.addFolderToClassEntries(LIB_FOLDER_NAME);
		converter.toC4JProject(this.projectToConvert);
	}

	@Test
	public void libFolderContainsRequiredJarsTest() throws Exception {
		converter.toC4JProject(this.projectToConvert);
		IFolder libFolder = getLibFolder();

		assertTrue(libFolder.getFile(C4J_JAR).exists());
		assertTrue(libFolder.getFile(JAVA_ASSIST_JAR).exists());
		assertTrue(libFolder.getFile(LOG4J_JAR).exists());
	}

	@Test
	public void jarsAddedToClassPathTest() throws Exception {
		converter.toC4JProject(this.projectToConvert);
		List<String> classpathEntries = getJarsFromClassPath();

		assertTrue(classpathEntries.contains(C4J_JAR));
		assertTrue(classpathEntries.contains(JAVA_ASSIST_JAR));
		assertTrue(classpathEntries.contains(LOG4J_JAR));
	}

	private List<String> getJarsFromClassPath() throws JavaModelException {
		List<String> classpathEntries = new ArrayList<String>();

		for (IClasspathEntry entry : this.projectToConvert.getRawClasspath()) {
			classpathEntries.add(entry.getPath().segment(2));
		}

		return classpathEntries;
	}

	private IFolder getLibFolder() {
		IProject project = this.projectToConvert.getProject();
		IFolder libFolder = project.getFolder(LIB_FOLDER_NAME);
		return libFolder;
	}

	@After
	public void tearDown() throws CoreException {
		IProject project = this.projectToConvert.getProject();
		project.delete(false, null);
	}

}
