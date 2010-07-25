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

package org.sperle.keepass.ui.icon;

import java.io.IOException;

import com.sun.lwuit.Image;
import com.sun.lwuit.util.Resources;

public class Icons {
    public static final int NUM_KEEPASS_ICONS = 69;
    
    private static Image keepassmobileIcon;
    private static Image keepassIcons[];
    private static Image defaultEntryIcon;
    
    private static Image kpmIcon;
    
    private static Image upIcon;
    private static Image dirIcon;
    private static Image fileIcon;
    
    public static void load() {
        loadKeePassIcons();
        loadFileChooserIcons();
    }
    
    private static void loadKeePassIcons() {
        try {
            keepassmobileIcon = Image.createImage("/keepassmobile.png");
        } catch (IOException e) {
            throw new IllegalStateException("KeePassMobile icon missing");
        }
        
        Resources iconRes;
        try {
            iconRes = Resources.open("/kdbicons.res");
        } catch (IOException e) {
            throw new IllegalStateException("can not load keepass icons:" + e.getMessage());
        }
        
        keepassIcons = new Image[NUM_KEEPASS_ICONS];
        for (int i = 0; i < NUM_KEEPASS_ICONS; i++) {
            keepassIcons[i] = iconRes.getImage((i < 10 ? "0" : "") + i + ".png");
            if(keepassIcons[i] == null) throw new IllegalStateException("keepass icon [" + (i < 10 ? "0" : "") + i +".png] missing");
        }
        defaultEntryIcon = keepassIcons[0];
    }
    
    public static Image getKeePassMobileIcon() {
        return keepassmobileIcon;
    }
    
    public static Image getKeePassIcon(int i) {
        try {
            return keepassIcons[i];
        } catch (ArrayIndexOutOfBoundsException e) {
            return defaultEntryIcon;
        }
    }
    
    public static Image getExpiredIcon() {
        return keepassIcons[45];
    }
    
    private static void loadFileChooserIcons() {
        Resources iconRes;
        try {
            iconRes = Resources.open("/filechooser.res");
        } catch (IOException e) {
            throw new IllegalStateException("can not load filechooser icons:" + e.getMessage());
        }
        
        try {
            kpmIcon = Image.createImage("/keepassmobile_16.png");
        } catch (IOException e) {
            throw new IllegalStateException("can not load KeePassMobile icon:" + e.getMessage());
        }
        
        upIcon = iconRes.getImage("up.png");
        if(upIcon == null) throw new IllegalStateException("filechooser icon [up.png] missing");
        dirIcon = iconRes.getImage("dir.png");
        if(dirIcon == null) throw new IllegalStateException("filechooser icon [dir.png] missing");
        fileIcon = iconRes.getImage("file.png");
        if(fileIcon == null) throw new IllegalStateException("filechooser icon [file.png] missing");
    }
    
    public static Image getUpIcon() {
        return upIcon;
    }
    
    public static Image getDirIcon() {
        return dirIcon;
    }
    
    public static Image getFileIcon() {
        return fileIcon;
    }
    
    public static Image getInfoIcon() {
        return getKeePassIcon(46);
    }
    
    public static Image getWarningIcon() {
        return getKeePassIcon(2);
    }
    
    public static Image getErrorIcon() {
        return getKeePassIcon(2);
    }
    
    public static Image getKpmIcon() {
        return kpmIcon;
    }
}
