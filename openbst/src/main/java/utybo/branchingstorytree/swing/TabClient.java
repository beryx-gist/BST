/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.brm.BRMHandler;

public class TabClient implements BSTClient
{
    private OpenBST instance;
    private StoryPanel tab;
    private TabUIB uibHandler;
    private BRMClient brmClient;
    private SSBClient ssbClient;

    public TabClient(OpenBST instance)
    {
        this.instance = instance;
        this.ssbClient = new SSBClient();
    }

    @Override
    public String askInput(String message)
    {
        Object input = null;
        while(input == null || input.toString().isEmpty())
        {
            input = JOptionPane.showInputDialog(instance, message, "Input asked", JOptionPane.QUESTION_MESSAGE, new ImageIcon(OpenBST.renameImage), null, null);
        }
        return input.toString();
    }

    @Override
    public void exit()
    {
        instance.removeStory(tab);
    }

    public void setStoryPanel(StoryPanel sp)
    {
        tab = sp;
        uibHandler = new TabUIB(tab);
        brmClient = new BRMClient(tab.getBSTFile(), this);
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
    public BRMHandler getBRMHandler()
    {
        return brmClient;
    }

    
}
