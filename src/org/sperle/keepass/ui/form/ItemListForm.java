/*
    Copyright (c) 2009-2010 Christoph Sperle <keepassmobile@gmail.com>
    
    This file is part of KeePassMobile.

    KeePassMobile is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    KeePassMobile is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with KeePassMobile.  If not, see <http://www.gnu.org/licenses/>.

*/

package org.sperle.keepass.ui.form;

import org.sperle.keepass.kdb.KdbItem;

/**
 * A Form that shows KeePass database items.
 */
public abstract class ItemListForm extends KeePassMobileForm {
    
    public ItemListForm() {
        super();
    }
    
    public ItemListForm(String title) {
        super(title);
    }
    
    /**
     * Refresh item list (is called after changing item content).
     */
    public abstract void refresh();
    
    /**
     * Select the given item in the list.
     */
    public abstract void setSelected(KdbItem item);
}
