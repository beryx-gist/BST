/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FilenameUtils;

import zrrk.bst.bstjava.api.BSTClient;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.bstjava.brm.BRMResourceConsumer;
import zrrk.bst.openbst.OpenBST;
import zrrk.bst.openbst.utils.Pair;
import zrrk.bst.openbst.visuals.AccumulativeRunnable;

public class BRMFileClient implements BRMAdvancedHandler
{
    protected volatile LoadStatusCallback loadCallback = null;

    private final File bstFileLocation;
    private final BSTClient client;
    private final BranchingStory origin;
    private boolean initialized = false;

    public BRMFileClient(final File bstFile, final BSTClient client, BranchingStory origin)
    {
        bstFileLocation = bstFile;
        this.client = client;
        this.origin = origin;
    }

    @Override
    public void load() throws BSTException
    {
        initialized = true;
        origin.getRegistry().put("__brm_initialized", 1);
        final File parent = bstFileLocation.getParentFile();
        final File resources = new File(parent, "resources");
        if(resources.exists() && resources.isDirectory())
        {
            int total = countFiles(resources);
            int current = 0;
            invokeAndWait(() ->
            {
                if(loadCallback == null) // Install our own reporters
                {
                    ProgressMonitor pm = new ProgressMonitor(OpenBST.getGUIInstance(),
                            "Loading resources...", "Initializing...", 0, total);
                    pm.setMillisToDecideToPopup(1);
                    pm.setMillisToPopup(1);
                    loadCallback = new LoadStatusCallback()
                    {
                        @Override
                        public void updateStatus(int i, String message)
                        {
                            pm.setProgress(i);
                            pm.setNote(message);
                        }

                        @Override
                        public void setTotal(int i)
                        {} // Cannot happen at this point

                        @Override
                        public void close()
                        {
                            pm.close();
                        }
                    };
                }
                else
                {
                    loadCallback.setTotal(total);
                }
            });
            AccumulativeRunnable<Pair<Integer, String>> r = new AccumulativeRunnable<Pair<Integer, String>>()
            {
                @Override
                public void run(List<Pair<Integer, String>> pairs)
                {
                    Pair<Integer, String> pair = pairs.get(pairs.size() - 1);
                    loadCallback.updateStatus(pair.a, pair.b);
                }
            };

            // Analysis of module directories list
            File[] fl = resources.listFiles();
            assert fl != null;
            for(final File moduleFolder : fl)
            {
                // Analysis of module directory
                if(!moduleFolder.isDirectory())
                {
                    continue;
                }
                final String module = moduleFolder.getName();
                final BRMResourceConsumer handler = client.getResourceHandler(module);
                if(handler != null)
                {
                    File[] fl2 = moduleFolder.listFiles();
                    assert fl2 != null;
                    for(final File file : fl2)
                    {
                        try
                        {
                            r.add(new Pair<>(current++,
                                    "Loading " + file.getName() + " for module " + module));
                            handler.load(file, FilenameUtils.getBaseName(file.getName()));
                        }
                        catch(IOException e)
                        {
                            throw new BSTException(-1, "Failed to load " + file.getName(), e,
                                    "<none>");
                        }
                    }
                }
            }

            invokeAndWait(() -> loadCallback.close());
        }
    }

    public void setLoadCallback(LoadStatusCallback callback)
    {
        this.loadCallback = callback;
    }

    private void invokeAndWait(Runnable r)
    {
        try
        {
            SwingUtilities.invokeAndWait(r);
        }
        catch(InvocationTargetException | InterruptedException e)
        {
            OpenBST.LOG.warn("Failed Swing invoke", e);
        }
    }

    public int countFiles(File folder)
    {
        int i = 0;
        File[] fl = folder.listFiles();
        assert fl != null;
        for(File f : fl)
        {
            if(f.isDirectory())
            {
                i += countFiles(f);
            }
            else
            {
                i += 1;
            }
        }
        return i;
    }

    @Override
    public void restoreSaveState() throws BSTException
    {
        Object o = origin.getRegistry().get("__brm_initialized", 0);
        if(!initialized && o instanceof Integer && (Integer)o == 1)
        {
            load();
        }
    }
}
