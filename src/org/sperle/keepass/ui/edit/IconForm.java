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

package org.sperle.keepass.ui.edit;

import org.sperle.keepass.kdb.KdbItem;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.icon.Icons;

import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.layouts.BorderLayout;

public class IconForm extends Dialog {
    private int currentIcon;
    
    public IconForm(final KdbItem item) {
        super("");
        this.currentIcon = item.getIconId();
        
        setLayout(new BorderLayout());
        setWidth(16);
        setHeight(16);
        setPreferredSize(new Dimension(16, 16));
        setScrollable(false);
        getStyle().setBorder(null);
        getStyle().setBgTransparency(0);
        getStyle().setBgImage(null);
        getDialogStyle().setBorder(null);
        getDialogStyle().setBgTransparency(0);
        getDialogStyle().setBgImage(null);
        
        final Form current = Display.getInstance().getCurrent();
        Command back = new Command(Messages.get("back")) {
            public void actionPerformed(ActionEvent ev) {
                //item.setIconId(currentIcon);
                current.show();
            }
        };
        addCommand(back);
        setBackCommand(back);
        
        final Label icon = new Label(Icons.getKeePassIcon(currentIcon));
        icon.setWidth(16);
        icon.setHeight(16);
        icon.setPreferredSize(new Dimension(16, 16));
        icon.getStyle().setBorder(null);
        icon.getStyle().setBgTransparency(0);
        addComponent(BorderLayout.CENTER, icon);
        
        addGameKeyListener(Display.GAME_UP, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                currentIcon = (currentIcon == 0 ? Icons.NUM_KEEPASS_ICONS - 1 : currentIcon - 1);
                icon.setIcon(Icons.getKeePassIcon(currentIcon));
            }
        });
        addGameKeyListener(Display.GAME_DOWN, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                currentIcon = (currentIcon == Icons.NUM_KEEPASS_ICONS - 1 ? 0 : currentIcon + 1);
                icon.setIcon(Icons.getKeePassIcon(currentIcon));
            }
        });
        addGameKeyListener(Display.GAME_LEFT, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                currentIcon = (currentIcon == 0 ? Icons.NUM_KEEPASS_ICONS - 1 : currentIcon - 1);
                icon.setIcon(Icons.getKeePassIcon(currentIcon));
            }
        });
        addGameKeyListener(Display.GAME_RIGHT, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                currentIcon = (currentIcon == Icons.NUM_KEEPASS_ICONS - 1 ? 0 : currentIcon + 1);
                icon.setIcon(Icons.getKeePassIcon(currentIcon));
            }
        });
        addGameKeyListener(Display.GAME_FIRE, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                item.setIconId(currentIcon);
                current.show();
            }
        });
    }
    
    public void show() {
        showPacked(BorderLayout.CENTER, true);
    }
}
