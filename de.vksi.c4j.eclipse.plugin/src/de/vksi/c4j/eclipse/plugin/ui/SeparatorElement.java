package de.vksi.c4j.eclipse.plugin.ui;

import org.eclipse.swt.graphics.Image;

public class SeparatorElement implements TreeActionElement<Void>
{
    public boolean provideElement()
    {
        return false;
    }

    public Void execute()
    {
        throw new UnsupportedOperationException();
    }

    public Image getImage()
    {
        return null;
    }

    public String getText()
    {
        return "___________________________________";
    }
}
