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

import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.KeePassMobileMIDlet;
import org.sperle.keepass.ui.font.Fonts;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.icon.Icons;

import com.sun.lwuit.Container;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;

public class AboutForm extends KeePassMobileForm {
    private Container headerPanel;
    private Container headlinePanel;
    private TextArea tributeArea;
    
    public AboutForm(final KeePassMobile app) {
        super(app, Messages.get("about"));
        
        setLayout(new BorderLayout());
        setScrollable(false);
        
        addCommand(backCommand);
        
        addComponent(BorderLayout.NORTH, getHeaderPanel());
        addComponent(BorderLayout.CENTER, getTributeArea());
    }
    
    private Container getHeaderPanel() {
        if(headerPanel == null) {
            headerPanel = new Container();
            headerPanel.setLayout(new BorderLayout());
            headerPanel.setScrollable(false);
            
            headerPanel.addComponent(BorderLayout.WEST, new Label(Icons.getKeePassMobileIcon()));
            headerPanel.addComponent(BorderLayout.CENTER, getHeadlinePanel());
        }
        return headerPanel;
    }
    
    private Container getHeadlinePanel() {
        if(headlinePanel == null) {
            headlinePanel = new Container();
            headlinePanel.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
            headlinePanel.setScrollable(false);
            
            Label l1 = new Label(KeePassMobile.NAME + " " + KeePassMobile.VERSION);
            l1.getStyle().setFont(Fonts.getBoldFont());
            l1.setPreferredSize(new Dimension(200,20));
            headlinePanel.addComponent(l1);
            Label l2 = new Label(KeePassMobile.COPYRIGHT);
            l2.setPreferredSize(new Dimension(200,20));
            headlinePanel.addComponent(l2);
            Label l3 = new Label(KeePassMobile.ME);
            l3.setPreferredSize(new Dimension(200,20));
            headlinePanel.addComponent(l3);
        }
        return headlinePanel;
    }
    
    private TextArea getTributeArea() {
        if(tributeArea == null) {
            tributeArea = new TextArea(5, 20);
            tributeArea.setEditable(false);
            tributeArea.getSelectedStyle().setBgColor(0x6600cc);
            tributeArea.setText(Messages.get("tribute") + "\n\n" + KeePassMobile.LICENSE);
        }
        return tributeArea;
    }
}
