/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.story;

/**
 * An empty node that does not do anything
 *
 * @author utybo
 * @see LogicalNode
 * @see TextNode
 * @see VirtualNode
 */
public class StoryNode extends TagHolder
{
    private final int id;

    public int getId()
    {
        return id;
    }

    /**
     * Create a node with the given ID
     *
     * @param id
     *            the id to use
     */
    public StoryNode(final int id)
    {
        this.id = id;
    }
}
