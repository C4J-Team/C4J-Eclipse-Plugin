package de.vksi.c4j.eclipse.plugin.ui;

import org.eclipse.jdt.core.IMember;
import org.eclipse.swt.graphics.Image;

import de.vksi.c4j.eclipse.plugin.util.PluginImages;

public abstract class CreateNewClassAction implements TreeActionElement<IMember>
{
    public boolean provideElement()
    {
        return true;
    }

    abstract public IMember execute();

    public Image getImage()
    {
        return PluginImages.DESC_NEW_CONTRACT.createImage();
    }

    public String getText()
    {
        return "Assign new Contract...";
    }
}
