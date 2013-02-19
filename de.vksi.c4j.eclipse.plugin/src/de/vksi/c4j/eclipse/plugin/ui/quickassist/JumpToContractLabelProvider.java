package de.vksi.c4j.eclipse.plugin.ui.quickassist;



import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


public class JumpToContractLabelProvider extends LabelProvider {	  
	  @Override
	  public String getText(Object element) {
	    if (element instanceof IType) {
	    	IType type = (IType) element;
	      return type.getElementName();
	    }
	    return "";
	  }

	  @SuppressWarnings("restriction")
	@Override
	  public Image getImage(Object element) {
	    if (element instanceof IType) 
	      return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CLASS);
		return null;
	  }


//	  private static final Image FOLDER = getImage("folder.gif");
//	  private static final Image FILE = getImage("file.gif");
//	  
//	  
//	  @Override
//	  public String getText(Object element) {
//	    if (element instanceof Category) {
//	      Category category = (Category) element;
//	      return category.getName();
//	    }
//	    return ((Todo) element).getSummary();
//	  }
//
//	  @Override
//	  public Image getImage(Object element) {
//	    if (element instanceof Category) {
//	      return FOLDER;
//	    }
//	    return FILE;
//	  }
//
//	  // Helper Method to load the images
//	  private static Image getImage(String file) {
//	    Bundle bundle = FrameworkUtil.getBundle(TodoLabelProvider.class);
//	    URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
//	    ImageDescriptor image = ImageDescriptor.createFromURL(url);
//	    return image.createImage();
//
//	  } 
	  
}
