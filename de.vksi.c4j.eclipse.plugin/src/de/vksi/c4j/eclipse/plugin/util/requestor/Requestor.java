package de.vksi.c4j.eclipse.plugin.util.requestor;

import java.util.List;

import org.eclipse.jdt.core.IType;

public interface Requestor {
	public List<IType> getAssociatedMemberOf(IType type);
}
