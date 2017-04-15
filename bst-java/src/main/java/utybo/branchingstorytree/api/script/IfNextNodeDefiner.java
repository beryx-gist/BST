/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.script;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.NodeNotFoundException;
import utybo.branchingstorytree.api.StoryUtils;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.StoryNode;

/**
 * A {@link NextNodeDefiner} which returns either one node or another depending
 * on the outcome of a checker
 *
 * @author utybo
 *
 */
public class IfNextNodeDefiner implements NextNodeDefiner
{
    private String one, two;
    private final CheckerDescriptor checker;

    /**
     * Create an If-NND that will return either the node one if the checker is
     * true, or the node two otherwise.
     *
     * @param one
     *            The node returned if the checker returns true
     * @param two
     *            The node returned if the checker returns false
     * @param checker
     *            The checker to use to determine the next node
     */
    public IfNextNodeDefiner(final String one, final String two, final CheckerDescriptor checker)
    {
        this.one = one;
        this.two = two;
        this.checker = checker;
    }

    /**
     * @return The node one if the checker returns true, or the node two
     *         otherwise
     * @throws BSTException
     *             If the checker throws a BSTExceptions
     */
    @Override
    public StoryNode getNextNode(BranchingStory story) throws NodeNotFoundException, BSTException
    {
        StoryNode snOne, snTwo;
        System.out.println("one:" + one);
        System.out.println("two:" + two);
        snOne = StoryUtils.parseNode(one, story);
        snTwo = StoryUtils.parseNode(two, story);
        
        return checker.check() ? snOne : snTwo;
    }
    
                
}
