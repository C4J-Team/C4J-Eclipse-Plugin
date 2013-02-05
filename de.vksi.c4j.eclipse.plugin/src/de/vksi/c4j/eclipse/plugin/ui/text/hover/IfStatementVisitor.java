package de.vksi.c4j.eclipse.plugin.ui.text.hover;

import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.POST_CONDITION;
import static de.vksi.c4j.eclipse.plugin.util.C4JPluginConstants.PRE_CONDITION;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IfStatement;

import de.vksi.c4j.eclipse.plugin.util.C4JConditions;

public class IfStatementVisitor extends ASTVisitor {
	private C4JConditions conditions;
	
	public IfStatementVisitor(){
		conditions = new C4JConditions();
	}

	@Override
	public boolean visit(IfStatement ifStatement) {
		AssertStatementVisitor assertVisitor = new AssertStatementVisitor();
		ifStatement.accept(assertVisitor);

		if (ifStatement.getExpression().toString().equals(PRE_CONDITION))
			conditions.addPreConditions(assertVisitor.getConditions());
		else if (ifStatement.getExpression().toString().equals(POST_CONDITION))
			conditions.addPostConditions(assertVisitor.getConditions());

		// false: children of this node should be skipped
		return false;
	}

	public C4JConditions getConditions() {
		return conditions;
	}

}
