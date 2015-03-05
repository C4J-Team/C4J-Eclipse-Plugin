package de.vksi.c4j.eclipse.plugin.core.configuration;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.DELIMITER;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.JAVAAGENT_ARGUMENT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.launching.StandardVMType;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMStandin;

@SuppressWarnings("restriction")
public class VMManager {
	private static final String JRE_CONTAINER = "org.eclipse.jdt.launching.JRE_CONTAINER";
	private static final String PROJECT_LOC_WITHNAME = "${project_loc:NAME}/";
	private IJavaProject javaProject;

	public VMManager(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public VMStandin getDuplicateOf(IVMInstall vm) {
		return new VMStandin(vm, String.valueOf(new Date().getTime()));
	}

	public void installVM(VMStandin vmToInstall) throws CoreException {
		installVM(vmToInstall.convertToRealVM());
	}

	public void installVM(IVMInstall vm) throws CoreException {
		removeJREContainer();
		addJREContainer(vm);
	}

	public IVMInstall getCurrentVM() {
		try {
			return JavaRuntime.getVMInstall(this.javaProject);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setVMArguments(IVMInstall vm, List<String> listOfArguments) {
		IVMInstall2 currentVM = (IVMInstall2) vm;
		String vmArguments = generateVMArgumentsString(listOfArguments);
		// TODO: keep existing arguments? -> get arguments (vm.getVMArgs()) and
		// remove C4J-Informations if necessary
		currentVM.setVMArgs(vmArguments);
	}

	private String generateVMArgumentsString(List<String> listOfArguments) {
		String newArgs = "";

		for (String arg : listOfArguments)
			newArgs += arg + DELIMITER;

		return newArgs.trim();
	}

	private void removeJREContainer() throws CoreException {
		List<IClasspathEntry> entriesWithoutJREContainer = new ArrayList<IClasspathEntry>();

		for (IClasspathEntry entry : javaProject.getRawClasspath()) {
			if (!isJreContainerEntry(entry)) {
				entriesWithoutJREContainer.add(entry);
			}
		}

		setClasspathEntries(entriesWithoutJREContainer);
	}

	private void setClasspathEntries(List<IClasspathEntry> entries) throws JavaModelException {
		this.javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
	}

	private boolean isJreContainerEntry(IClasspathEntry entry) {
		return IClasspathEntry.CPE_CONTAINER == entry.getEntryKind() && entry.getPath().toString().startsWith(JRE_CONTAINER);
	}

	private void addJREContainer(IVMInstall vmToInstall) throws CoreException {
		if (vmToInstall != null) {
			IPath jreContainerPath = JavaRuntime.newJREContainerPath(vmToInstall);
			IClasspathEntry jreEntry = JavaCore.newContainerEntry(jreContainerPath);
			new Classpath(javaProject).add(jreEntry);
		}
	}

	public void setNameOf(VMStandin vmStanding, String name) {
		vmStanding.setName(name);
	}

	public String getNameOf(VMStandin vmStanding) {
		return vmStanding.getName();
	}

	public String getIdOf(VMStandin vmStanding) {
		return vmStanding.getId();
	}

	public String getNameOf(IVMInstall vm) {
		return vm.getName();
	}

	public String getIdOf(IVMInstall vm) {
		return vm.getId();
	}

	public IVMInstall searchForMatchingVM(IFile file) {
		if (file == null)
			return null;

		IVMInstallType[] vmInstallTypes = JavaRuntime.getVMInstallTypes();
		for (IVMInstallType ivmInstallType : vmInstallTypes) {
			if (StandardVMType.ID_STANDARD_VM_TYPE.equals(ivmInstallType.getId()))
				return searchForMatchingStandardVMType(ivmInstallType, file);

		}
		return null;
	}

	private IVMInstall searchForMatchingStandardVMType(IVMInstallType ivmInstallType, IFile file) {
		for (IVMInstall ivmInstall : ivmInstallType.getVMInstalls()) {
			if (searchForMatchingArguments(file, ivmInstall))
				return ivmInstall;
		}
		return null;
	}

	private boolean searchForMatchingArguments(IFile file, IVMInstall ivmInstall) {
		if (isMatchingCurrentJavaVersion(ivmInstall)) {
			String[] vmArguments = ivmInstall.getVMArguments();
			if (vmArguments != null) {
				for (String arg : vmArguments) {
					if (arg.contains(JAVAAGENT_ARGUMENT)) {
						String pathToC4Jlib = arg.replace(JAVAAGENT_ARGUMENT + PROJECT_LOC_WITHNAME.replace("NAME", file.getProject().getName()), "");
						boolean match = pathToC4Jlib.equals(file.getProjectRelativePath().toString());
						if (match)
							return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isMatchingCurrentJavaVersion(IVMInstall ivmInstall) {
		if (getCurrentVM() instanceof IVMInstall2 && ivmInstall instanceof IVMInstall2) {

			String projectCurrentJavaVersion = ((IVMInstall2) getCurrentVM()).getJavaVersion();
			String vmJavaVersion = ((IVMInstall2) ivmInstall).getJavaVersion();
			return projectCurrentJavaVersion.equals(vmJavaVersion);
		}
		return false;
	}
}