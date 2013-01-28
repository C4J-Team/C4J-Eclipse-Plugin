package internalContract;

import static de.vksi.c4j.Condition.ignored;
import static de.vksi.c4j.Condition.old;
import static de.vksi.c4j.Condition.postCondition;
import static de.vksi.c4j.Condition.preCondition;
import static de.vksi.c4j.Condition.result;
import internalContract.StackSpec;
import de.vksi.c4j.ClassInvariant;
import de.vksi.c4j.Target;

public class StackSpecContract<T> implements StackSpec<T> {

	@Target
	private StackSpec<T> target;

	private int constCapacity;
	private boolean capacitySet = false;

	@ClassInvariant
	public void classInvariant() {
		if (!capacitySet) {
			constCapacity = target.capacity();
		}
		assert target.capacity() == constCapacity : "capacity is immutable";
		assert target.capacity() > 0 : "capacity > 0";
		assert target.size() >= 0 : "size >= 0";
		assert target.size() <= target.capacity() : "size <= capacity";
		if (!target.isEmpty()) {
			assert target.top() != null : "if not isEmpty then top != null";
		}
	}

	@Override
	public void push(T item) {
		if (preCondition()) {
			assert item != null : "item != null";
			assert !target.isFull() : "not isFull";
		}
		if (postCondition()) {
			assert target.top() == item : "item set";
			assert target.size() == old(target.size()) + 1 : "size = old size + 1";
			assert !target.isEmpty() : "not isEmpty";
		}
	}

	@Override
	public void pop() {
		if (preCondition()) {
			assert !target.isEmpty() : "not isEmpty";
		}
		if (postCondition()) {
			assert target.size() == old(target.size()) - 1 : "size = old size - 1";
			assert !target.isFull() : "not isFull";
		}
	}

	@Override
	public T top() {
		if (preCondition()) {
			assert !target.isEmpty() : "not isEmpty";
		}
		return ignored();
	}

	@Override
	public int size() {
		return ignored();
	}

	@Override
	public boolean isEmpty() {
		if (postCondition()) {
			boolean result = result();
			if (result) {
				assert target.size() == 0 : "if isEmpty then size == 0";
				assert !target.isFull() : "if isEmpty then not isFull";
			}
		}
		return ignored();
	}

	@Override
	public boolean isFull() {
		if (postCondition()) {
			boolean result = result();
			if (result) {
				assert target.size() == target.capacity() : "if isFull then size == capacity";
				assert !target.isEmpty() : "if isFull then not isEmpty";
			}
		}
		return ignored();
	}

	@Override
	public int capacity() {
		return ignored();
	}

}
