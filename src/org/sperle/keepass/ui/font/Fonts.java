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

package org.sperle.keepass.ui.font;

import com.sun.lwuit.Font;

public class Fonts {
    public static final int SIZE_SMALL  = 0;
    public static final int SIZE_MEDIUM = 1;
    public static final int SIZE_LARGE  = 2;
    public static final int SIZE_DEFAULT = SIZE_MEDIUM;
    
    private static Font normalFont;
    private static Font boldFont;
    
    public static void load(int fontSize) {
        normalFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, getFontSize(fontSize));
        boldFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, getFontSize(fontSize));
    }
    
    private static int getFontSize(int fontSize) {
        if(fontSize == SIZE_SMALL) return Font.SIZE_SMALL;
        else if(fontSize == SIZE_MEDIUM) return Font.SIZE_MEDIUM;
        else return Font.SIZE_LARGE;
    }

    public static Font getNormalFont() {
        return normalFont;
    }
    
    public static Font getBoldFont() {
        return boldFont;
    }
}
