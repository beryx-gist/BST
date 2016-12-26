/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.bdf.BDFFile;
import utybo.branchingstorytree.bdf.BDFHandler;
import utybo.branchingstorytree.bdf.BDFParser;

public class BDFClient implements BDFHandler
{
    private final HashMap<String, BDFFile> map = new HashMap<>();

    @Override
    public void load(final String pathToResource, final String name) throws BSTException
    {
        try
        {
            map.put(name, BDFParser.parse(new BufferedReader(new InputStreamReader(new FileInputStream(new File(pathToResource)))), name));
        }
        catch(final IOException e)
        {
            throw new BSTException(-1, "Error when reading file " + pathToResource, e);
        }
    }

    @Override
    public BDFFile getBDFFile(final String name)
    {
        return map.get(name);
    }

    public void reset()
    {
        map.clear();
    }

}
