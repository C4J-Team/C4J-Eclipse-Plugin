package de.vksi.c4j.eclipse.plugin.core.configuration;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.EA_AGRUMENT;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.JAVAAGENT_ARGUMENT;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.DELIMITER;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.VMStandin;

public class C4JVM {
	private static final String PROJECT_LOC_WITHNAME = "${project_loc:NAME}/";
	private static final String C4J_NAME_POSTIFX = "C4J";
	private VMManager vmManager;

	public C4JVM(IJavaProject javaProject) {
		vmManager = new VMManager(javaProject);
	}

	public void setUpC4JVM(IFile libFile) throws CoreException {
		IVMInstall matchingVM = vmManager.searchForMatchingVM(libFile);

		if (matchingVM != null)
			vmManager.installVM(matchingVM);
		else
			createNewVM(libFile);
	}

	private void createNewVM(IFile file) throws CoreException {
		VMStandin duplicate = vmManager.getDuplicateOf(vmManager.getCurrentVM());
		vmManager.setNameOf(duplicate, generateVMname(duplicate));
		vmManager.installVM(duplicate);
		vmManager.setVMArguments(vmManager.getCurrentVM(), generateC4JVMarguments(file));
	}

	private String generateVMname(VMStandin vmStandin) {
		return "Java " + vmStandin.getJavaVersion() + DELIMITER + C4J_NAME_POSTIFX + DELIMITER
				+ String.valueOf(new Date().getTime());
	}

	private ArrayList<String> generateC4JVMarguments(IFile file) {
		String fileLocation = PROJECT_LOC_WITHNAME.replace("NAME", file.getProject().getName()) + file.getProjectRelativePath().toString();

		ArrayList<String> listOfArguments = new ArrayList<String>();
		listOfArguments.add(EA_AGRUMENT);
		listOfArguments.add(JAVAAGENT_ARGUMENT + fileLocation);
		return listOfArguments;
	}
}
