package de.vksi.c4j.eclipse.plugin.core.configuration;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import testutil.JavaProjectCreator;

public class AddClasspathEntryTest {

	private static final String TEST_PATH = "TestPath";
	private static final String PROJECT_NAME = "testProject";
	private IJavaProject javaProject;
	private AddClasspathEntry addClasspathEntry;
	
	@Before
	public void setUp() throws CoreException{
		this.javaProject = JavaProjectCreator.create(PROJECT_NAME);
		this.addClasspathEntry = new AddClasspathEntry(this.javaProject);
	}

	@Test
	public void testAddIClasspathEntry() throws JavaModelException {
		this.javaProject.setRawClasspath(null,null);
		int nuberOfEntriesBefore = this.javaProject.getRawClasspath().length;
		
		IPath path = new Path(TEST_PATH).makeAbsolute();
		IClasspathEntry testEntry = JavaCore.newLibraryEntry(path, null, null);
		this.addClasspathEntry.add(testEntry);
		
		List<IClasspathEntry> classpathEntries = Arrays.asList(this.javaProject.getRawClasspath());
		int nuberOfEntriesAfter = this.javaProject.getRawClasspath().length;
		
		assertTrue(classpathEntries.contains(testEntry));
		assertEquals(nuberOfEntriesBefore + 1, nuberOfEntriesAfter);
	}

	@After
	public void tearDown() throws CoreException {
		IProject project = this.javaProject.getProject();
		project.delete(false, null);
	}
}
