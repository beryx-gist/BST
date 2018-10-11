/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.junit.Test;

import zrrk.bst.bstjava.api.BSTClient;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.BranchingStoryTreeParser;
import zrrk.bst.bstjava.api.script.Dictionary;
import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.bstjava.api.story.LogicalNode;
import zrrk.bst.bstjava.api.story.StoryNode;
import zrrk.bst.bstjava.bdf.BDFFile;
import zrrk.bst.bstjava.bdf.BDFHandler;
import zrrk.bst.bstjava.bdf.BDFParser;
import zrrk.bst.bstjava.brm.BRMHandler;

public class ModuleBDFTesting
{
    private class BDFClient implements BSTClient, BDFHandler, BRMHandler
    {
        private final HashMap<String, BDFFile> bdfFiles = new HashMap<>();

        public BDFClient() throws BSTException
        {
            loadAuto();
        }

        @Override
        public void loadAuto() throws BSTException
        {
            load("/utybo/branchingstorytree/api/test/files/resources/bdf.bdf", "bdf");
        }

        @Override
        public void load(InputStream in, String name) throws BSTException
        {
            // Useless
        }

        @Override
        public void load(final String pathToResource, final String name) throws BSTException
        {
            try
            {
                bdfFiles.put(name, BDFParser.parse(new BufferedReader(
                        new InputStreamReader(getClass().getResourceAsStream(pathToResource))),
                        name));
                assert bdfFiles.containsKey(name);
            }
            catch(final IOException e)
            {
                throw new BSTException(-1, "Unexpected I/O error on BDF load", e, "<none>");
            }
        }

        @Override
        public BDFFile getBDFFile(final String name)
        {
            return bdfFiles.get(name);
        }

        @Override
        public String askInput(final String message)
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
    public void testBDF()
            throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        testFile("module_bdf.bst", new BDFClient());
    }

    public static void testFile(final String path, final BSTClient client)
            throws IOException, BSTException, InstantiationException, IllegalAccessException
    {
        final Dictionary d = new Dictionary();
        final BranchingStory story = new BranchingStoryTreeParser().parse(
                new BufferedReader(new InputStreamReader(ActionTesting.class
                        .getResourceAsStream("/utybo/branchingstorytree/api/test/files/" + path))),
                d, client, path);
        StoryNode node = story.getInitialNode();
        while(node != null)
        {
            if(node instanceof LogicalNode)
            {
                node = ((LogicalNode)node).solve(story);
            }
            else
            {
                throw new BSTException(-1, node.getId() + " isn't a logical node", story);
            }
        }
    }
}
