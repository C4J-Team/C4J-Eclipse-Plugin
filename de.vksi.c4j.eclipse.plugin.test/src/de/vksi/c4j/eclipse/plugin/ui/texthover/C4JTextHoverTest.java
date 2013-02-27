package de.vksi.c4j.eclipse.plugin.ui.texthover;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static test.util.TestConstants.PATH_TO_DOT_PROJECT_FILE;
import static test.util.TestConstants.PROJECTNAME;
import static test.util.TestConstants.TARGET_SOURCEFILE_DOI_0;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocBrowserInformationControlInput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.vksi.c4j.eclipse.plugin.ui.texthover.C4JTextHover;

import test.util.JavaProjectLoader;

@SuppressWarnings("restriction")
public class C4JTextHoverTest {

	private C4JTextHover c4jHover;
	private IJavaProject javaProject;
	
	@Before
	public void setUp() throws Exception {
		c4jHover = new C4JTextHover();
		javaProject = JavaProjectLoader.loadProject(PROJECTNAME, PATH_TO_DOT_PROJECT_FILE);
	}

	@Test
	public void testCreateC4JHoverInfoForType() throws JavaModelException {
		JavadocBrowserInformationControlInput hoverInfo =  Mockito.mock(JavadocBrowserInformationControlInput.class);
		
		ICompilationUnit targetCompilationUnit = JavaProjectLoader.getCompilationUnit(javaProject, TARGET_SOURCEFILE_DOI_0);
		IType targetType = JavaProjectLoader.getType(targetCompilationUnit);
		
		when(hoverInfo.getElement()).thenReturn(targetType);
		when(hoverInfo.getHtml()).thenReturn(getBasicHtml());
		
		JavadocBrowserInformationControlInput createC4JHoverInfo = c4jHover.createC4JHoverInfo(hoverInfo);
		
		String expectedHtml = "<html><head><title>title</title></head><body><dl><dt>Invariants</dt><dd>top() != null : \"if not isEmpty then top != null\"</dd><dd>size() <= capacity() : \"size <= capacity\"</dd><dd>capacity() == constCapacity : \"capacity is immutable\"</dd><dd>capacity() > 0 : \"capacity > 0\"</dd><dd>size() >= 0 : \"size >= 0\"</dd></dl></dl></body></html>";
		
		assertEquals(expectedHtml, createC4JHoverInfo.getHtml());
	}

	@Test
	public void testC4JHoverInfoDoesNotExist() throws JavaModelException {
		JavadocBrowserInformationControlInput hoverInfo =  null;
		
		JavadocBrowserInformationControlInput createC4JHoverInfo = c4jHover.createC4JHoverInfo(hoverInfo);
		
		assertNull(createC4JHoverInfo);
	}
	
	private String getBasicHtml(){
		String html = "<html><head><title>title</title></head><body></body></html>";
		return html;
	}
}
