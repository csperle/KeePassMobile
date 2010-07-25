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

package org.sperle.keepass.ui.theme;

import java.io.IOException;

import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;

public class Themes {
    public static final String NOT_SET = "null";
    public static final String NICE_THEME = "nice";
    public static final String FAST_THEME = "fast";
    
    private static Resources themes;
    private static String inUse = null;
    
    public static void load() {
        try {
            themes = Resources.open("/theme.res");
        } catch (IOException e) {
            throw new IllegalStateException("can not load theme: " + e.getMessage());
        }
    }
    
    public static boolean isSupported(String name) {
        return themes.getTheme(name) != null;
    }
    
    public static void apply(String name) {
        if(isSupported(name)) {
            UIManager.getInstance().setThemeProps(themes.getTheme(name));
            inUse = name;
        } else throw new IllegalStateException("theme [" + name + "] not available");
    }
    
    public static String inUse() {
        return inUse;
    }
}
