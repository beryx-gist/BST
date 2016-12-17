/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.bdf;

import utybo.branchingstorytree.api.script.VariableRegistry;

public class BDFIntNode extends BDFNode
{
    private int value;

    public BDFIntNode(String name, int i)
    {
        super(name);
        this.value = i;
    }

    @Override
    public void applyTo(VariableRegistry registry, String prefix)
    {
        registry.put(prefix + getName(), value);
    }

}
