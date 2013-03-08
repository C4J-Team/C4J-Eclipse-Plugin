package de.vksi.c4j.eclipse.plugin.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;

public class C4JPluginSettings {

	private static final String SETTINGS_FOLDER = ".settings";
	private static final String C4J_SETTINGS_XML = "c4j.settings.xml";
	private static final String CONFIGURATION = "configuration";
	private static final String LIBRARY = "library";
	private IJavaProject javaProject;
	private IFile settingXML;

	public C4JPluginSettings(IJavaProject javaProject) throws CoreException, IOException {
		this.javaProject = javaProject;
		settingXML = getSettingsFolder().getFile(C4J_SETTINGS_XML);
	}

	/**
	 * @param absolut path
	 */
	public void setPathToConfigFiles(IPath path) throws IOException, CoreException {
		writeSettings(CONFIGURATION, path.makeRelativeTo(javaProject.getPath()));
	}

	/**
	 * @param absolut path
	 */
	public void setPathToLibFiles(IPath path) throws IOException, CoreException {
		writeSettings(LIBRARY, path.makeRelativeTo(javaProject.getPath()));
	}

	/**
	 * @return path relative to containing project
	 */
	public IPath getPathToConfigFiles() throws IOException, CoreException {
		String property = readSettings().getProperty(CONFIGURATION);
		return property != null ? new Path(property) : null;
	}

	/**
	 * @return path relative to containing project
	 */
	public IPath getPathToLibFiles() throws IOException, CoreException {
		String property = readSettings().getProperty(LIBRARY);
		return property != null ? new Path(property) : null;
	}

	public boolean exist() throws CoreException {
		return settingXML.exists();
	}
	
	private void writeSettings(String key, IPath value) throws IOException, CoreException {
		Properties currentProperties = new Properties();
		if (settingXML.exists())
			currentProperties = readSettings();
		else
			createSettingsXML();

		currentProperties.setProperty(key, value.toString());
		ByteArrayOutputStream out = getXMLOutputStream(currentProperties);
		ByteArrayInputStream in = getXMLInputStream(out);
		
		settingXML.setContents(in, true, false, null);
		
		in.close();
		out.close();
	}

	private ByteArrayInputStream getXMLInputStream(ByteArrayOutputStream out) {
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		return in;
	}

	private ByteArrayOutputStream getXMLOutputStream(Properties currentProperties) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		currentProperties.storeToXML(out, "C4J root configuration", "UTF-8");
		return out;
	}

	private void createSettingsXML() throws CoreException{
		settingXML.create(new ByteArrayInputStream("".getBytes()), true, null);
	}

	private Properties readSettings() throws IOException, IOException, CoreException {
		Properties props = new Properties();
		if(exist())
			props.loadFromXML(settingXML.getContents());
		return props;
	}

	private IFolder getSettingsFolder() throws CoreException {
		IFolder folder = javaProject.getProject().getFolder(SETTINGS_FOLDER);
		if (!folder.exists())
			folder.create(true, true, null);
		return folder;
	}
}
