package de.vksi.c4j.eclipse.plugin.ui.quickassist;



import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


@SuppressWarnings("restriction")
public class JumpToContractLabelProvider extends LabelProvider {	  
	  @Override
	  public String getText(Object element) {
	    if (element instanceof IType) {
	    	IType type = (IType) element;
	      return type.getElementName();
	    }
	    return "";
	  }

	@Override
	  public Image getImage(Object element) {
	    if (element instanceof IType) 
	      return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CLASS);
		return null;
	  }



//	  // Helper Method to load the images
//	  private static Image getImage(String file) {
//	    Bundle bundle = FrameworkUtil.getBundle(TodoLabelProvider.class);
//	    URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
//	    ImageDescriptor image = ImageDescriptor.createFromURL(url);
//	    return image.createImage();
//
//	  } 
	  
}
