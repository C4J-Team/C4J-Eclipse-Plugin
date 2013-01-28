package de.vksi.c4j.eclipse.plugin.util;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.POST_CONDITION;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.PRE_CONDITION;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IfStatement;

public class IfStatementVisitor extends ASTVisitor {
	private Conditions conditions;
	
	public IfStatementVisitor(){
		conditions = new Conditions();
	}

	@Override
	public boolean visit(IfStatement ifStatement) {
		AssertStatementVisitor assertVisitor = new AssertStatementVisitor();
		ifStatement.accept(assertVisitor);

		if (ifStatement.getExpression().toString().equals(PRE_CONDITION))
			conditions.setPreConditions(assertVisitor.getConditions());
		else if (ifStatement.getExpression().toString().equals(POST_CONDITION))
			conditions.setPostConditions(assertVisitor.getConditions());

		// false: children of this node should be skipped
		return false;
	}

	public Conditions getConditions() {
		return conditions;
	}

}
