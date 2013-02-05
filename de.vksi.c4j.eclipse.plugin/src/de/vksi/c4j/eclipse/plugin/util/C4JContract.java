package de.vksi.c4j.eclipse.plugin.util;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.ANNOTATION_CONTRACT;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import de.vksi.c4j.eclipse.plugin.wizards.ContractWizardRunner;

public class C4JContract {
	private IType target;
	private IType contract;;

	public C4JContract() {
	}

	public void createContractFor(IType type) {
		this.target = type;
		this.contract = runContractCreationWizard();
	}

	private IType runContractCreationWizard() {
		ContractWizardRunner wizard = new ContractWizardRunner(target);
		
		return wizard.runWizard();
	}	

	public boolean exists() {
		return contract != null;
	}

	public boolean isExternal() {
		return new C4JContractAnnotation(contract).exists();
	}

	public IType getType() {
		return contract;
	}
}
