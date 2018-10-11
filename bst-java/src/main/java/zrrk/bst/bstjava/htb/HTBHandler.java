/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.htb;

import zrrk.bst.bstjava.api.Experimental;
import zrrk.bst.bstjava.api.UnsupportedExperimentalException;
import zrrk.bst.bstjava.brm.BRMResourceConsumer;

public interface HTBHandler extends BRMResourceConsumer
{
    public boolean hasResource(String resource);

    public String getAsString(String resource);

    public String getAsBase64(String resource);

    public boolean requestJSAccess();

    public boolean requestHrefAccess();
    
    @Experimental
    public default void applyCSS(String resource)
    {
        throw new UnsupportedExperimentalException();
    };

    @Experimental
    public default void removeCSS(String resource)
    {
        throw new UnsupportedExperimentalException();
    };
    
    @Experimental
    public default void clearCSS()
    {
        throw new UnsupportedExperimentalException();
    };
}
