package de.vksi.c4j.eclipse.plugin;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.vksi.c4j.eclipse.plugin.logging.PluginLogManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class C4JEclipsePluginActivator extends AbstractUIPlugin {
	private PluginLogManager logManager;

	private static final String LOG_PROPERTIES_FILE = "logger.properties";

	// The plug-in ID
	public static final String PLUGIN_ID = "de.vksi.c4j.eclipse.plugin"; //$NON-NLS-1$

	// The shared instance
	private static C4JEclipsePluginActivator plugin;

	public C4JEclipsePluginActivator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		configure();
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		if (this.logManager != null) {
			this.logManager.shutdown();
			this.logManager = null;
		}
	}

	public static C4JEclipsePluginActivator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	private void configure() {
		try {
			URL url = getBundle().getEntry("/" + LOG_PROPERTIES_FILE);
			InputStream propertiesInputStream = url.openStream();
			if (propertiesInputStream != null) {
				Properties props = new Properties();
				props.load(propertiesInputStream);
				propertiesInputStream.close();
				this.logManager = new PluginLogManager(this, props);
			}
		} catch (Exception e) {
			String message = "Error while initializing log properties."
					+ e.getMessage();
			IStatus status = new Status(IStatus.ERROR, getDefault().getBundle()
					.getSymbolicName(), IStatus.ERROR, message, e);
			getLog().log(status);
			throw new RuntimeException(
					"Error while initializing log properties.", e);
		}
	}
	
	public static PluginLogManager getLogManager() {
		return getDefault().logManager; 
	}
	
}
