package de.vksi.c4j.eclipse.plugin.internal;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

import de.vksi.c4j.eclipse.plugin.wizards.ContractWizardRunner;

public class C4JContract {
	private IType target;
	private IType contract;
	
	public C4JContract(IType contract){
		this.contract = contract;
	}

	private C4JContract(IType target, IType contract) {
		this.target = target;
		this.contract = contract;
	}

	public static C4JContract createContractFor(IType target) {
		IType contract = runContractCreationWizard(target);
		return new C4JContract(target, contract);
	}

	private static IType runContractCreationWizard(IType target) {
		ContractWizardRunner wizard = new ContractWizardRunner(target);
		
		return wizard.runWizard();
	}
	
	public boolean hasMethod(IMethod method){
		IMethod requestedMethod = contract.getMethod(method.getElementName(), method.getParameterTypes());
		return requestedMethod.exists();
	}

	public IType getTarget(){
		return target;
	}
	
	public boolean exists() {
		return contract != null;
	}

	public boolean isExternal() {
		return new C4JContractAnnotation(contract).exists();
	}

	public IType getContract() {
		return contract;
	}
}
