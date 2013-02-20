package de.vksi.c4j.eclipse.plugin.core.configuration;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.util.JavaProjectCreator;

public class C4JResourcesTest {

	private static final String LOG4J_PROPERTIES = "log4j.properties";
	private static final String C4J_PURE_REGISTRY_XML = "c4j-pure-registry.xml";
	private static final String C4J_LOCAL_XML = "c4j-local.xml";
	private static final String C4J_GLOBAL_XML = "c4j-global.xml";
	private static final String PROJECT_NAME = "virtualTestProject";
	private IJavaProject javaProject;
	private C4JResources c4jResources;
	
	@Before
	public void setUp() throws CoreException{
		this.javaProject = JavaProjectCreator.create(PROJECT_NAME);
		this.c4jResources = new C4JResources();
	}
	
	@Test
	public void testCopyConfigFilesTo() throws CoreException {
		IFolder folder = this.javaProject.getProject().getFolder(JavaProjectCreator.SRC_FOLDER);
		this.c4jResources.copyConfigFilesTo(folder);
	
		List<String> resourceFiles = new ArrayList<String>();
		IResource[] resources = folder.members();
		
		for (IResource res : resources) {
			switch (res.getType()) {
			case IResource.FILE:
				IFile file = (IFile) res;
				resourceFiles.add(file.getName());
				break;
			}
		}
		
		assertTrue(resourceFiles.contains(C4J_GLOBAL_XML));
		assertTrue(resourceFiles.contains(C4J_LOCAL_XML));
		assertTrue(resourceFiles.contains(C4J_PURE_REGISTRY_XML));
		assertTrue(resourceFiles.contains(LOG4J_PROPERTIES));
	}

	@After
	public void tearDown() throws CoreException {
		IProject project = this.javaProject.getProject();
		project.delete(false, null);
	}
}
