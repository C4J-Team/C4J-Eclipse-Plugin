package de.vksi.c4j.eclipse.plugin.util;

public class C4JPluginConstants {
	//C4J core resources
	//Libs
	public static final String C4J_JAR = "c4j-6.0.0.jar";
	public static final String JAVASSIST_JAR = "javassist-3.16.1-GA.jar";
	public static final String LOG4J_JAR = "log4j-1.2.16.jar";
	public static final String CHANGELOG = "CHANGELOG.txt";
	
	//Config
	public static final String C4J_GLOBAL = "c4j-global.xml";
	public static final String C4J_LOCAL = "c4j-local.xml";
	public static final String C4J_PURE_REGISTRY = "c4j-pure-registry.xml";
	public static final String LOG4J_PROPERTIES = "log4j.properties";
	
	//VM
	public static final String EA_AGRUMENT = "-ea";
	public static final String JAVAAGENT_ARGUMENT = "-javaagent:";
	public static final String DELIMITER = " ";
	
	//C4J keywords
	public static final String POST_CONDITION = "postCondition()";
	public static final String PRE_CONDITION = "preCondition()";
	public static final String RETURN_IGNORED = "ignored";
	
	public static final String ANNOTATION_CONTRACT = "Contract";
	public static final String ANNOTATION_CONTRACT_REFERENCE = "ContractReference";
	public static final String ANNOTATION_CLASS_INVARIANT = "ClassInvariant";
	public static final String ANNOTATION_TARGET = "Target";

	public static final String IMPORT_CONTRACT_REFERENCE = "de.vksi.c4j.ContractReference";
	public static final String IMPORT_CLASSINVARIANT = "de.vksi.c4j.ClassInvariant";
	public static final String IMPORT_TARGET = "de.vksi.c4j.Target";
	public static final String IMPORT_IGNORED = "de.vksi.c4j.Condition.ignored";
	public static final String IMPORT_POST_CONDITIONS = "de.vksi.c4j.Condition.postCondition";
	public static final String IMPORT_PRE_CONDITIONS = "de.vksi.c4j.Condition.preCondition";
	public static final String IMPORT_CONTRACT_ANNOTATION = "de.vksi.c4j.Contract";
}
