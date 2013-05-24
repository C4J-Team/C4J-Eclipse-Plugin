package de.vksi.c4j.eclipse.plugin.logging;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class C4JPluginLogging extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.vksi.c4j.eclipse.plugin.logging"; //$NON-NLS-1$

	// The shared instance
	private static C4JPluginLogging plugin;

	private ArrayList<PluginLogManager> logManagers = new ArrayList<PluginLogManager>();

	public C4JPluginLogging() {
		super();
		plugin = this;
	}

	/**
	 * Iterates over the list of active log managers and shutdowns each one
	 * before calling the base class implementation.
	 * 
	 * @see Plugin#stop
	 */
	public void stop(BundleContext context) throws Exception {
		synchronized (this.logManagers) {
			Iterator<PluginLogManager> it = this.logManagers.iterator();
			while (it.hasNext()) {
				PluginLogManager logManager = it.next();
				logManager.internalShutdown();
			}
			this.logManagers.clear();
		}
		super.stop(context);
	}

	/**
	 * Adds a log manager object to the list of active log managers
	 */
	void addLogManager(PluginLogManager logManager) {
		synchronized (this.logManagers) {
			if (logManager != null)
				this.logManagers.add(logManager);
		}
	}

	/**
	 * Removes a log manager object from the list of active log managers
	 */
	void removeLogManager(PluginLogManager logManager) {
		synchronized (this.logManagers) {
			if (logManager != null)
				this.logManagers.remove(logManager);
		}
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public static C4JPluginLogging getDefault() {
		return plugin;
	}
}
