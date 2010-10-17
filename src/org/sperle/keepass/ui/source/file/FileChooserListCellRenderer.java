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

package org.sperle.keepass.ui.source.file;

import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.font.Fonts;
import org.sperle.keepass.ui.icon.Icons;

import com.sun.lwuit.Component;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.list.ListCellRenderer;

public class FileChooserListCellRenderer extends Label implements ListCellRenderer {
    
    public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
        if(value instanceof FileChooserForm.UpDirectory) {
            setIcon(Icons.getUpIcon());
        } else if(value instanceof FileChooserForm.Directory) {
            setIcon(Icons.getDirIcon());
        } else { // File
            setIcon(Icons.getFileIcon());
        }
        setText(" "+value);
        
        if (isSelected) {
            setFocus(true);
            getStyle().setFont(Fonts.getBoldFont(), true);
            if(!KeePassMobile.instance().isFastUI()) getStyle().setBgTransparency(128);
        } else {
            setFocus(false);
            getStyle().setFont(Fonts.getNormalFont(), true);
            if(!KeePassMobile.instance().isFastUI()) getStyle().setBgTransparency(0);
        }
        return this;
    }

    public Component getListFocusComponent(List list) {
        setText("");
        setIcon(null);
        setFocus(true);
        getStyle().setFont(Fonts.getNormalFont(), true);
        if(!KeePassMobile.instance().isFastUI()) getStyle().setBgTransparency(128);
        return this;
    }
}
