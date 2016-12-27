/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.script;

/**
 * A classic next node definer that does not change under any circumstance
 * 
 * @author utybo
 *
 */
public class StaticNextNode implements NextNodeDefiner
{
    private final int nextNode;

    /**
     * Creates a {@link StaticNextNode}
     * 
     * @param nextNode
     *            The next node represented by this object
     */
    public StaticNextNode(final int nextNode)
    {
        this.nextNode = nextNode;
    }

    @Override
    public int getNextNode()
    {
        return nextNode;
    }

}
