package internalContract;

import de.vksi.c4j.ContractReference;

@ContractReference(StackDepthOfInheritance_3_Contract.class)
public class StackDepthOfInheritance_3<T> extends StackDepthOfInheritance_2<T> {

	public StackDepthOfInheritance_3(int capacity) {
		super(capacity);
	}
	
	@Override
	public void push(T item) {
		//simulate some implementation...
		//e.g. handle null-parameter..
		super.push(item);
	}

}
