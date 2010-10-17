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

package org.sperle.keepass.ui.util;

import java.util.Vector;

import org.sperle.keepass.kdb.KdbEntry;
import org.sperle.keepass.kdb.KdbGroup;
import org.sperle.keepass.kdb.KdbItem;
import org.sperle.keepass.ui.menu.MenuItem;


public class Vectors {
    public static KdbItem[] toItemArray(Vector v) {
        KdbItem[] a = new KdbItem[v.size()];
        for (int i = 0; i < v.size(); i++) {
            a[i] = (KdbItem)v.elementAt(i);
        }
        return a;
    }
    
    public static KdbGroup[] toGroupArray(Vector v) {
        KdbGroup[] a = new KdbGroup[v.size()];
        for (int i = 0; i < v.size(); i++) {
            a[i] = (KdbGroup)v.elementAt(i);
        }
        return a;
    }
    
    public static KdbEntry[] toEntryArray(Vector v) {
        KdbEntry[] a = new KdbEntry[v.size()];
        for (int i = 0; i < v.size(); i++) {
            a[i] = (KdbEntry)v.elementAt(i);
        }
        return a;
    }
    
    public static MenuItem[] toMenuItemArray(Vector v) {
        MenuItem[] a = new MenuItem[v.size()];
        for (int i = 0; i < v.size(); i++) {
            a[i] = (MenuItem)v.elementAt(i);
        }
        return a;
    }

    public static Vector append(Vector first, Vector second) {
        Vector all = new Vector(first.size() + second.size());
        for (int i = 0; i < first.size(); i++) {
            all.addElement(first.elementAt(i));
        }
        for (int i = 0; i < second.size(); i++) {
            all.addElement(second.elementAt(i));
        }
        return all;
    }
}
