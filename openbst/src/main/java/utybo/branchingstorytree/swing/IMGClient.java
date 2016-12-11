/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.img.IMGHandler;

public class IMGClient implements IMGHandler
{
    private HashMap<String, BufferedImage> images = new HashMap<>();
    private BufferedImage current = null;
    private StoryPanel panel;

    public IMGClient(StoryPanel panel)
    {
        this.panel = panel;
    }

    @Override
    public void load(String pathToResource, String name) throws BSTException
    {
        try
        {
            images.put(name, ImageIO.read(new File(pathToResource)));
        }
        catch(IOException e)
        {
            throw new BSTException(-1, "Could not load image", e);
        }
    }

    @Override
    public void setBackground(String name)
    {
        panel.story.getRegistry().put("__img__background", name);
        if(name == null)
            current = null;
        else
            current = images.get(name);
    }

    public BufferedImage getCurrentBackground()
    {
        return current;
    }

    public void reset()
    {
        images.clear();
    }

    public void restoreSaveState()
    {
        try
        {
            setBackground(panel.story.getRegistry().get("__img__background", null).toString());
        }
        catch(NullPointerException e)
        {}
    }
}
