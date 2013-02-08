package de.vksi.c4j.eclipse.plugin.util;

import org.eclipse.jdt.core.IType;

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
