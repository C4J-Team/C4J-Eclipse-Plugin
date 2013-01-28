package de.vksi.c4j.eclipse.plugin.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;

public class AssertStatementVisitor extends ASTVisitor {
	private static final String TARGET = "target."; //better solution: search for @Target-Ref and get the value name
	private List<String> conditions = new ArrayList<String>();

	@Override
	public boolean visit(AssertStatement assertStatement) {
		conditions.add(createAssertString(assertStatement));
		
		// false: children of this node should be skipped
		return false;
	}

	public List<String> getConditions() {
		return conditions;
	}
	
	private String createAssertString(AssertStatement assertStatement){
		String assertExpression = assertStatement.getExpression().toString();
		String assertMessage = assertStatement.getMessage() != null ? assertStatement.getMessage().toString() : "";
		
		assertExpression = assertExpression.replace(TARGET, "");
		return assertExpression + " : " + assertMessage;
	}

}
