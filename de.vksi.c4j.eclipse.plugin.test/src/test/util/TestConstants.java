package test.util;

import java.io.File;

public class TestConstants {
	//*************************C4J-Constants*******************************************
	public static final String ANNOTATION_CONTRACT = "Contract";
	public static final String ANNOTATION_CONTRACT_REFERENCE = "ContractReference";
	
	
	//*************************TestProject*********************************************
	public static final String PROJECTNAME = "TestProject";
	public static final String PATH_TO_DOT_PROJECT_FILE = "resources" + File.separator + PROJECTNAME + "/.project";
	
	//***internal
	//Depth Of Inheritance (DOI) is 0 -> Base Class
	public static final String TARGET_SOURCEFILE_DOI_0 = "StackSpec.java";
	public static final String TARGET_SOURCEFILE_DOI_1 = "StackDepthOfInheritance_1.java";
	public static final String TARGET_SOURCEFILE_DOI_2 = "StackDepthOfInheritance_2.java";
	public static final String TARGET_SOURCEFILE_DOI_3 = "StackDepthOfInheritance_3.java";
	
	public static final String CONTRACT_SOURCEFILE_DOI_0 = "StackSpecContract.java";
	
	//***external
	public static final String TODS_CONTRACT = "TimeOfDaySpecContract.java";
	public static final String TODS = "TimeOfDaySpec.java";
}
