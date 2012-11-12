package de.andrena.C4J.eclipse.plugin.core.configuration;

import static org.junit.Assert.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.junit.Before;
import org.junit.Test;

public class ConvertToC4jProjectTest {

	private ConvertToC4jProject converter;
	private IJavaProject javaProject;
	
	@Before
	public void SetUp() throws CoreException{
		//this.javaProject = createJavaProject();
		System.out.println("ölkasd");
	}

	private IJavaProject createJavaProject() throws CoreException {
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		IProject project= root.getProject();
		project.create(null);
		project.open(null);
		
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);
		
		IJavaProject javaProject= JavaCore.create(project);
		//javaProject.setRawClasspath(classPath, defaultOutputLocation, null);
		
		return javaProject;
	}

	@Test
	public void testConvert() {
		
	}

}
