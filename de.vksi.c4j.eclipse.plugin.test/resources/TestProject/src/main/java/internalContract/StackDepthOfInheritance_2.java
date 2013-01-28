package internalContract;

public class StackDepthOfInheritance_2<T> extends StackDepthOfInheritance_1<T> {

	public StackDepthOfInheritance_2(int capacity) {
		super(capacity);
	}
	
	@Override
	public void push(T item) {
		super.push(item);
	}

	@Override
	public void pop() {
		super.pop();
	}

	@Override
	public T top() {
		return super.top();
	}

	@Override
	public int size() {
		return super.size();
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}

	@Override
	public boolean isFull() {
		return super.isFull();
	}

	@Override
	public int capacity() {
		return super.capacity();
	}

}
