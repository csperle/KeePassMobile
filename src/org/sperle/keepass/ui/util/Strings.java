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

public class Strings {
    public static String[] split(String s, char seperator) {
        if (s == null || "".equals(s)) {
            return new String[0];
        }

        Vector splits = new Vector();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == seperator) {
                if(trim(buf.toString()).length() > 0) {
                    splits.addElement(trim(buf.toString()));
                }
                buf.setLength(0);
            } else {
                buf.append(c);
            }
        }

        if (buf.length() > 0) {
            if(trim(buf.toString()).length() > 0) {
                splits.addElement(trim(buf.toString()));
            }
        }

        return toArray(splits);
    }

    private static String trim(String s) {
        if (s == null || "".equals(s)) {
            return s;
        }
        
        String trimmed = s.trim();
        if ("".equals(trimmed)) {
            return trimmed;
        }
        
        char lastChar = trimmed.charAt(trimmed.length() - 1);
        if (lastChar == 13) {
            return trimmed.substring(0, trimmed.length() - 1).trim();
        } else if (lastChar == 10) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
            if ("".equals(trimmed)) {
                return trimmed;
            }
            lastChar = trimmed.charAt(trimmed.length() - 1);
            if (lastChar == 13) {
                return trimmed.substring(0, trimmed.length() - 1).trim();
            } else {
                return trimmed;
            }
        } else {
            return trimmed;
        }
    }
    
    private static String[] toArray(Vector v) {
        String[] a = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            a[i] = (String)v.elementAt(i);
        }
        return a;
    }
}
