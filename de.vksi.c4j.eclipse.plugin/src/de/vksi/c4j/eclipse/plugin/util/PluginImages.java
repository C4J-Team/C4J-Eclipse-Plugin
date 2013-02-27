package de.vksi.c4j.eclipse.plugin.util;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Point;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class PluginImages {

	public static final String IMG_NEW_CONTRACT_WIZ = "newclass_wiz.png";
	public static final String IMG_NEW_CONTRACT = "newcontract.gif";
	public static final String IMG_CONTRACT_CLASS = "contract_class.gif";
	public static final String IMG_CONTRACT_METHOD = "contractmeth_obj.gif";
	public static final String IMG_C4J_BRANDING = "c4jIcon.gif";

	public static final ImageDescriptor DESC_NEW_CONTRACT_WIZ = create(IMG_NEW_CONTRACT_WIZ, false);
	public static final ImageDescriptor DESC_NEW_CONTRACT = create(IMG_NEW_CONTRACT, true);
	public static final ImageDescriptor DESC_CONTRACT_CLASS = create(IMG_CONTRACT_CLASS, true);
	public static final ImageDescriptor DESC_CONTRACT_METHOD = create(IMG_CONTRACT_METHOD, true);

	private static ImageDescriptor create(String imgNewContractWiz, boolean useJavaElementImgDesc) {
		Bundle bundle = FrameworkUtil.getBundle(PluginImages.class);
		URL url = FileLocator.find(bundle, new Path("icons/" + imgNewContractWiz), null);
		return createImageDescriptor(bundle, url, useJavaElementImgDesc);
	}

	private static ImageDescriptor createImageDescriptor(Bundle bundle, URL url, boolean useJavaElementImgDesc) {
		if (url == null)
			return ImageDescriptor.getMissingImageDescriptor();

		if (useJavaElementImgDesc)
			return new JavaElementImageDescriptor(ImageDescriptor.createFromURL(url), 0, new Point(22, 16));
		else
			return ImageDescriptor.createFromURL(url);

	}
}
