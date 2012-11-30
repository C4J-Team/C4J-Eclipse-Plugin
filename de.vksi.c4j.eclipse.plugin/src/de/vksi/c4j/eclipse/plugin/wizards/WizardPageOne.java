package de.vksi.c4j.eclipse.plugin.wizards;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

public class WizardPageOne extends NewJavaProjectWizardPageOne implements Listener{
	private static final String DEFAULT_OUTPUT_FOLDER = "bin";
	public static final String SRC_TEST_JAVA = "src/test/java";
	public static final String SRC_MAIN_RESOURCES = "src/main/resources";
	public static final String SRC_MAIN_JAVA = "src/main/java";
	public static final String SRC_CONTRACT_JAVA = "src/contract/java";
	private Button useC4JTemplate;
	private Button noTemplate;
	private boolean isC4JTemplateSelected = true;

	public WizardPageOne() {
		super();
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		Control nameControl = createNameControl(composite);
		nameControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Control jreControl = createJRESelectionControl(composite);
		jreControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    Control c4jLayoutControl = createC4JProjectLayoutControl(composite);	
	    c4jLayoutControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
		Control infoControl = createInfoControl(composite);
		infoControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		setControl(composite);
	}


	public IClasspathEntry[] getSourceClasspathEntries() {
		if(isC4JTemplateSelected){
		IPath path1 = new Path(getProjectName()).append(SRC_CONTRACT_JAVA).makeAbsolute();
		IPath path2 = new Path(getProjectName()).append(SRC_MAIN_JAVA).makeAbsolute();
		IPath path3 = new Path(getProjectName()).append(SRC_MAIN_RESOURCES).makeAbsolute();
		IPath path4 = new Path(getProjectName()).append(SRC_TEST_JAVA).makeAbsolute();
		
		return new IClasspathEntry[] { JavaCore.newSourceEntry(path1), JavaCore.newSourceEntry(path2),
				JavaCore.newSourceEntry(path3), JavaCore.newSourceEntry(path4) };
		}
		
		IPath path1 = new Path(getProjectName()).append("src").makeAbsolute();
		
		return new IClasspathEntry[] { JavaCore.newSourceEntry(path1)};
	}
	
	

	public IPath getOutputLocation() {
		return new Path(getProjectName()).append(DEFAULT_OUTPUT_FOLDER).makeAbsolute();
	}
	
	private Control createC4JProjectLayoutControl(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
	    group.setText("C4J Project Layout");
	    
	    RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
	    rowLayout.marginTop = 10;
	    rowLayout.spacing = 10;
	    rowLayout.marginLeft = 12;
	    group.setLayout(rowLayout);

	    useC4JTemplate = new Button(group, SWT.RADIO);
	    useC4JTemplate.setText("Use C4J template to create Project structure (recommended)");
	    useC4JTemplate.setSelection(true);
	    useC4JTemplate.addListener(SWT.Selection, this);
	    
	    noTemplate = new Button(group, SWT.RADIO);
	    noTemplate.setText("Create standard Java Project structure without C4J template");
	    
	    return group;
	}

	@Override
	public void handleEvent(Event event) {
		isC4JTemplateSelected = useC4JTemplate.getSelection();
	}

}
