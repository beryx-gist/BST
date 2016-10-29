/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.story;

public class StoryNode extends TagHolder
{

    private final int id;

    public int getId()
    {
        return id;
    }

    public StoryNode(int id)
    {
        this.id = id;
    }
}
