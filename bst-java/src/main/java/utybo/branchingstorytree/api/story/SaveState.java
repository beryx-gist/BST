/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.story;

import utybo.branchingstorytree.api.script.VariableRegistry;

public class SaveState
{
    private int nodeId;
    private VariableRegistry registry;

    public SaveState(int nodeId, VariableRegistry vr)
    {
        this.nodeId = nodeId;
        registry = vr.clone();
    }

    public int getNodeId()
    {
        return nodeId;
    }

    public void applySaveState(BranchingStory bs)
    {
        bs.setRegistry(registry);
    }
}