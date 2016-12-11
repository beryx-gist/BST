/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.img;

import utybo.branchingstorytree.brm.BRMResourceConsumer;

public interface IMGHandler extends BRMResourceConsumer
{
    public void setBackground(String name);
}