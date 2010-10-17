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

package org.sperle.keepass.ui.menu;

import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.form.KeePassMobileForm;
import org.sperle.keepass.ui.icon.Icons;

import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.DefaultListModel;

public class MainMenuForm extends KeePassMobileForm {
    private List mainMenu;
    
    public MainMenuForm(MenuItem[] menuItems) {
        super(KeePassMobile.NAME + " " + KeePassMobile.VERSION);
        this.getTitleComponent().setIcon(Icons.getKpmIcon());
        
        setLayout(new BorderLayout());
        setScrollable(false);
        
        mainMenu = new List(menuItems);
        mainMenu.setListCellRenderer(new MainMenuListCellRenderer());
        mainMenu.setOrientation(List.VERTICAL);
        if(!KeePassMobile.instance().isFastUI()) mainMenu.setSmoothScrolling(true);
        else mainMenu.setSmoothScrolling(false);
        mainMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MenuItem selectedSource = (MenuItem) mainMenu.getSelectedItem();
                if (selectedSource != null) {
                    selectedSource.choosen();
                }
            }
        });
        addComponent(BorderLayout.CENTER, mainMenu);
        updateCommands();
    }
    
    protected void goBack() {
        // do nothing
    }
    
    public void refresh(MenuItem[] menuItems) {
        mainMenu.setModel(new DefaultListModel(menuItems));
    }
}
