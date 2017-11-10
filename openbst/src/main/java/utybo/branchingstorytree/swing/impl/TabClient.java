/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.impl;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.htb.HTBHandler;
import utybo.branchingstorytree.swing.Icons;
import utybo.branchingstorytree.swing.OpenBST;
import utybo.branchingstorytree.swing.utils.Lang;
import utybo.branchingstorytree.swing.visuals.NodePanel;
import utybo.branchingstorytree.swing.visuals.StoryPanel;
import utybo.branchingstorytree.xbf.XBFHandler;

public class TabClient implements BSTClient
{
    private final OpenBST instance;
    private StoryPanel tab;
    private TabUIB uibHandler;
    private BRMAdvancedHandler brmClient;
    private SSBClient ssbClient;
    private IMGClient imgClient;
    private final BDFClient bdfClient;
    private XBFClient xbfClient;
    private HTBClient htbClient;
    private boolean isExperimental;

    public TabClient(final OpenBST instance)
    {
        this.instance = instance;
        bdfClient = new BDFClient();
    }

    @Override
    public String askInput(final String message)
    {
        Object input = null;
        while(input == null || input.toString().isEmpty())
        {
            input = JOptionPane.showInputDialog(instance, message, "Input asked",
                    JOptionPane.QUESTION_MESSAGE);
        }
        return input.toString();
    }

    @Override
    public void exit()
    {
        instance.removeStory(tab);
    }

    public void setStoryPanel(final StoryPanel sp)
    {
        tab = sp;
        uibHandler = new TabUIB(tab);
        ssbClient = new SSBClient(tab);
        imgClient = new IMGClient(tab);
        xbfClient = new XBFClient(tab, this);
    }

    public void setNodePanel(final NodePanel np)
    {
        htbClient = new HTBClient(np);
    }

    @Override
    public TabUIB getUIBarHandler()
    {
        return uibHandler;
    }

    @Override
    public SSBClient getSSBHandler()
    {
        return ssbClient;
    }

    @Override
    public BRMAdvancedHandler getBRMHandler()
    {
        return brmClient;
    }

    @Override
    public IMGClient getIMGHandler()
    {
        return imgClient;
    }

    @Override
    public BDFClient getBDFHandler()
    {
        return bdfClient;
    }

    @Override
    public XBFHandler getXBFHandler()
    {
        return xbfClient;
    }

    @Override
    public HTBHandler getHTBHandler()
    {
        return htbClient;
    }

    public void setBRMHandler(BRMAdvancedHandler handler)
    {
        brmClient = handler;
    }

    @Override
    public void warn(String string)
    {
        OpenBST.LOG.warn(string);
    }

    @Override
    public void warnExperimental(int line, String from)
    {
        if(!isExperimental)
        {
            isExperimental = true;
            JOptionPane.showMessageDialog(OpenBST.getInstance(),
                    "<html><body style='width:300px'>" + Lang.get("story.experimental").replace("$l", "" + line).replace("$f", from),
                    Lang.get("story.experimental.title"), JOptionPane.WARNING_MESSAGE,
                    new ImageIcon(Icons.getImage("Experiment", 48)));
        }
    }
}
