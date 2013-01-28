package internalContract;

import static de.vksi.c4j.Condition.old;
import static de.vksi.c4j.Condition.postCondition;
import static de.vksi.c4j.Condition.preCondition;
import de.vksi.c4j.Target;

public class StackDepthOfInheritance_3_Contract<T> extends StackDepthOfInheritance_3<T> {

	@Target
	private StackDepthOfInheritance_1<T> target;
	
	@ClassInvariant
	public void classInvariant() {
		assert "foo".equals("baa") : "some additional condition";
	}
	
	
	public StackDepthOfInheritance_3_Contract(int capacity) {
		super(capacity);
		if (postCondition()) {
			assert target.capacity() == capacity : "capacity set";
			assert "xyz".equals("xyz") : "some additional condition";
		}
	}
	
	@Override
	public void push(T item) {
		if (preCondition()) {
//			assert item != null : "item != null"; --> preCondition is weakened: null-param is allowed
			assert !target.isFull() : "not isFull";
		}
		if (postCondition()) {
			assert target.top() == item : "item set";
			assert target.size() == old(target.size()) + 1 : "size = old size + 1";
			assert !target.isEmpty() : "not isEmpty";
			// --> postCondition strengthened by adding new condition
			assert "newCondition".equals("newCondition") : "some additional condition"; 
		}
	}

}
