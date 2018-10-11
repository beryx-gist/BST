/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.htb;

import zrrk.bst.bstjava.api.BSTClient;
import zrrk.bst.bstjava.api.script.ExtNNDFactory;
import zrrk.bst.bstjava.api.script.NextNodeDefiner;

public class HTBNextNodeDefinerFactory implements ExtNNDFactory
{

    @Override
    public NextNodeDefiner createNND(String head, String desc, int line, BSTClient client)
    {
        return new HTBNextNodeDefiner(head, desc, line, client);
    }

    @Override
    public String[] getNames()
    {
        return new String[] {"htb_requestjs", "htb_requesthref"};
    }

}
