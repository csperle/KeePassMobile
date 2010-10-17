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

package org.sperle.keepass.ui.tree;

import org.sperle.keepass.kdb.KeePassDatabase;
import org.sperle.keepass.kdb.v1.KdbEntryV1;
import org.sperle.keepass.kdb.v1.KdbGroupV1;
import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.font.Fonts;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.icon.Icons;

import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.ListCellRenderer;

public class QuickViewTreeListCellRenderer extends Label implements ListCellRenderer {
    private KeePassDatabase kdb;
    
    private Container selected = new Container(new BoxLayout(BoxLayout.Y_AXIS));
    private Label user = new Label();
    private Label password = new Label();
    
    public QuickViewTreeListCellRenderer(KeePassDatabase kdb) {
        this.kdb = kdb;
        
        user.setUIID("QuickViewUserLabel");
        user.setFocus(true);
        user.getStyle().setBgTransparency(0, true);
        user.getStyle().setFont(Fonts.getNormalFont(), true);
        user.setText(Messages.get("user_name_initial"));
        password.setUIID("QuickViewPasswordLabel");
        password.setFocus(true);
        password.getStyle().setBgTransparency(0, true);
        password.getStyle().setFont(Fonts.getNormalFont(), true);
        password.setText(Messages.get("password_initial"));
        selected.addComponent(this);
        selected.addComponent(user);
        selected.addComponent(password);
    }
    
    public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
        if(value == null) {
            setText("null");
            return this;
        }
        
        if(value instanceof String) {
            setText((String)value);
            return this;
        }
        
        if(value instanceof KdbGroupV1) {
            KdbGroupV1 group = (KdbGroupV1)value;
            setText(" "+group.getName());
            
            boolean expired = group.expired();
            setIcon(expired ? Icons.getExpiredIcon() : Icons.getKeePassIcon(group.getIconId()));
            
            if(expired) {
                getUnselectedStyle().setFgColor(0xD60000, true); //red
                getSelectedStyle().setFgColor(0xD60000, true); //red
            } else {
                if(kdb.getEntries(group).size() == 0 && kdb.getChildGroups(group).size() == 0) {
                    getUnselectedStyle().setFgColor(0xCCCCCC, true); //grey
                    getSelectedStyle().setFgColor(0xCCCCCC, true); //grey
                } else {
                    getUnselectedStyle().setFgColor(0xFFFFFF, true);
                    getSelectedStyle().setFgColor(0xFFFFFF, true);
                }
            }
        } else {
            KdbEntryV1 entry = (KdbEntryV1)value;
            setText(" "+entry.getTitle());
            boolean expired = entry.expired();
            setIcon(expired ? Icons.getExpiredIcon() : Icons.getKeePassIcon(entry.getIconId()));
            
            if(entry == KeePassMobile.instance().getClipboardEntry()) {
                getUnselectedStyle().setFgColor(0xCCCCCC, true); //grey
                getSelectedStyle().setFgColor(0xCCCCCC, true); //grey
            } else if(expired) {
                getUnselectedStyle().setFgColor(0xD60000, true); //red
                getSelectedStyle().setFgColor(0xD60000, true); //red
            } else {
                getUnselectedStyle().setFgColor(0xFFFFFF, true);
                getSelectedStyle().setFgColor(0xFFFFFF, true);
            }
            
            user.setText(Messages.get("user_name_initial") + " " + entry.getUsername());
            password.setText(Messages.get("password_initial") + " " + entry.getPassword());
        }
        
        if (isSelected) {
            setFocus(true);
            getStyle().setFont(Fonts.getBoldFont(), true);
            if(!KeePassMobile.instance().isFastUI()) getStyle().setBgTransparency(128, true);
            if(value instanceof KdbGroupV1) return this;
            else return selected;
        } else {
            setFocus(false);
            getStyle().setFont(Fonts.getNormalFont(), true);
            if(!KeePassMobile.instance().isFastUI()) getStyle().setBgTransparency(0, true);
            return this;
        }
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
