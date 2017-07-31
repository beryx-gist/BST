/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.xbf;

import java.util.Collection;

import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.brm.BRMResourceConsumer;

public interface XBFHandler extends BRMResourceConsumer
{
    public BranchingStory getAdditionalStory(String name);

    public BranchingStory getMainStory();

    public Collection<String> getAdditionalStoryNames();
}
