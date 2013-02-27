package de.vksi.c4j.eclipse.plugin.ui;

import org.eclipse.swt.graphics.Image;

public interface TreeActionElement<T>
{
    boolean provideElement();

    T execute();

    String getText();

    Image getImage();
}
