package de.vksi.c4j.eclipse.plugin.core.configuration;

import static org.junit.Assert.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMStandin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.util.JavaProjectCreator;

public class VMDuplicatorTest {
	private static final String TEST_ARGUMENTS = "testParameter";
	private static final String ID_OF_DUPLICATE = "duplVM_ID";
	private static final String NAME_OF_DUPLICATE = "duplicatedVM";
	private static final String PROJECT_NAME = "virtualTestProject";
	private VMDuplicator duplicateVm;
	private IJavaProject javaProject;

	@Before
	public void setUp() throws CoreException {
		this.javaProject = JavaProjectCreator.create(PROJECT_NAME);
		this.duplicateVm = new VMDuplicator(javaProject);
	}

	@Test
	public void testDuplicateVM() throws Exception {
		this.duplicateVm.duplicateVM(NAME_OF_DUPLICATE, ID_OF_DUPLICATE);

		String name = this.duplicateVm.getDuplicate().getName();
		String id = this.duplicateVm.getDuplicate().getId();

		assertEquals(NAME_OF_DUPLICATE, name);
		assertEquals(ID_OF_DUPLICATE, id);
	}

	@Test
	public void testSetVMArgumentForDuplicate() throws CoreException {
		this.duplicateVm.duplicateVM(NAME_OF_DUPLICATE, ID_OF_DUPLICATE);
		this.duplicateVm.setVMArgumentForDuplicate(TEST_ARGUMENTS);

		VMStandin duplicate = this.duplicateVm.getDuplicate();
		String[] vmArguments = duplicate.getVMArguments();

		assertEquals(TEST_ARGUMENTS, vmArguments[0]);
	}
	
	@Test
	public void testVMNotDuplicatedIfNameAndIdIsEqualToCurrentVM() throws Exception {
		this.duplicateVm.duplicateVM(NAME_OF_DUPLICATE, ID_OF_DUPLICATE);
		this.duplicateVm.convertDuplicateToRealVM();
		IVMInstall vmInstall = JavaRuntime.getVMInstall(this.javaProject);
		
		this.duplicateVm.duplicateVM(NAME_OF_DUPLICATE, ID_OF_DUPLICATE);
		this.duplicateVm.convertDuplicateToRealVM();
		IVMInstall vmInstallShouldNotHaveChanged = JavaRuntime.getVMInstall(this.javaProject);
	
		assertEquals(vmInstall, vmInstallShouldNotHaveChanged);
	}
	
	@Test
	public void testSettingArgumentsWithoutDuplicateHasNoEffect() throws Exception {
		IVMInstall vmInstall = JavaRuntime.getVMInstall(this.javaProject);
		String[] vmArguments = vmInstall.getVMArguments();
		
		this.duplicateVm.setVMArgumentForDuplicate(TEST_ARGUMENTS);
		
		String[] vmArgumentsShouldNotHaveChanged = vmInstall.getVMArguments();
		
		assertArrayEquals(vmArguments, vmArgumentsShouldNotHaveChanged);
	}

	@After
	public void tearDown() throws CoreException {
		IProject project = this.javaProject.getProject();
		project.delete(false, null);
	}

}
