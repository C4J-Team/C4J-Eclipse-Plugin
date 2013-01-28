package internalContract;

import static de.vksi.c4j.Condition.postCondition;
import static de.vksi.c4j.Condition.preCondition;
import internalContract.StackDepthOfInheritance_1;
import de.vksi.c4j.Target;

public class StackDepthOfInheritance_1_Contract<T> extends StackDepthOfInheritance_1<T> {

	@Target
	private StackDepthOfInheritance_1<T> target;

	public StackDepthOfInheritance_1_Contract(int capacity) {
		super(capacity);
		if (preCondition()) {
			assert capacity > 0 : "capacity > 0";
		}
		if (postCondition()) {
			assert target.capacity() == capacity : "capacity set";
		}
	}

}
