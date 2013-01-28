package internalContract;

import internalContract.StackSpecContract;
import de.vksi.c4j.ContractReference;
import de.vksi.c4j.Pure;

@ContractReference(StackSpecContract.class)
public interface StackSpec<T> {

	void push(T item);

	void pop();

	@Pure
	T top();

	@Pure
	int size();

	@Pure
	boolean isEmpty();

	@Pure
	boolean isFull();

	@Pure
	int capacity();
}
