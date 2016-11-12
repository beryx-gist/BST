/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.script.functions.action;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.script.VariableRegistry;

public class ClientInteraction implements ScriptAction
{

    @Override
    public void exec(String head, String desc, VariableRegistry registry, BSTClient client) throws BSTException
    {
        switch(head)
        {
        case "input":
            final String varName = desc.split(",")[0];
            final String msg = desc.substring(desc.indexOf(',') + 1);
            registry.put(varName, client.askInput(msg));
            break;
        case "exit":
            client.exit();
        }
    }

    @Override
    public String[] getName()
    {
        return new String[] {"input", "exit"};
    }

}
