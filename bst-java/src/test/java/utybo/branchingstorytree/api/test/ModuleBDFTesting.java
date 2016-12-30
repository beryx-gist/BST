/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.junit.Test;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.BranchingStoryTreeParser;
import utybo.branchingstorytree.api.script.Dictionnary;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.bdf.BDFFile;
import utybo.branchingstorytree.bdf.BDFHandler;
import utybo.branchingstorytree.bdf.BDFParser;
import utybo.branchingstorytree.brm.BRMHandler;

public class ModuleBDFTesting
{
    private class BDFClient implements BSTClient, BDFHandler, BRMHandler
    {
        private HashMap<String, BDFFile> bdfFiles = new HashMap<>();

        @Override
        public void loadAuto() throws BSTException
        {
            load("/utybo/branchingstorytree/api/test/files/resources/bdf.bdf", "bdf");
        }

        @Override
        public void load(String pathToResource, String name) throws BSTException
        {
            try
            {
                bdfFiles.put(name, BDFParser.parse(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(pathToResource))), name));
            }
            catch(IOException e)
            {
                throw new BSTException(-1, "Unexpected I/O error on BDF load", e);
            }
        }

        @Override
        public BDFFile getBDFFile(String name)
        {
            return bdfFiles.get(name);
        }

        @Override
        public String askInput(String message)
        {
            // Useless
            return null;
        }

        @Override
        public void exit()
        {
            // Useless
        }

        @Override
        public BRMHandler getBRMHandler()
        {
            return this;
        }

        @Override
        public BDFHandler getBDFHandler()
        {
            return this;
        }
    }

    @Test
    public void testBDF() throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        testFile("module_bdf.bst", new BDFClient());
    }

    public static void testFile(String path, BSTClient client) throws IOException, BSTException, InstantiationException, IllegalAccessException
    {
        Dictionnary d = new Dictionnary();
        BranchingStory story = new BranchingStoryTreeParser().parse(new BufferedReader(new InputStreamReader(ActionTesting.class.getResourceAsStream("/utybo/branchingstorytree/api/test/files/" + path))), d, client);
        StoryNode node = story.getInitialNode();
        while(node != null)
        {
            if(node instanceof LogicalNode)
                node = story.getNode(((LogicalNode)node).solve());
            else
                throw new BSTException(-1, node.getId() + " isn't a logical node");
        }
    }
}
