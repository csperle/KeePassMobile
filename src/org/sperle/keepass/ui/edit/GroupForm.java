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

import org.sperle.keepass.kdb.KdbGroup;
import org.sperle.keepass.ui.command.AbstractFormCommands;
import org.sperle.keepass.ui.command.KeePassMobileCommand;
import org.sperle.keepass.ui.form.IconTitleForm;
import org.sperle.keepass.ui.form.ItemListForm;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.icon.Icons;

import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class GroupForm extends IconTitleForm  {
    private final KdbGroup group;
    
    private Label nameLabel;
    private TextArea nameField;
    
    public GroupForm(final KdbGroup group, final boolean create) {
        super(Icons.getKeePassIcon(group.getIconId()), create ? Messages.get("create_group") : group.getName());
        this.group = group;
        
        group.access();
        
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setScrollableY(true);
        
        nameLabel = new Label(Messages.get("group_name"));
        addComponent(nameLabel);
        nameField = new TextArea(group.getName() != null ? group.getName() : "");
        nameField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                group.setName(nameField.getText());
            }
        });
        addComponent(nameField);
        updateCommands();
    }
    
    protected void goBack() {
        ItemListForm itemList = (ItemListForm)previousForm;
        itemList.refresh();
        itemList.setSelected(group);
        itemList.show();
    }
    
    public KdbGroup getGroup() {
        return group;
    }
    
    public static class FormCommands extends AbstractFormCommands {

        public FormCommands(final GroupForm form) {
            commands = new KeePassMobileCommand[2];
            commands[0] = (KeePassMobileCommand) form.getBackCommand();
            commands[1] = new KeePassMobileCommand(Messages.get("change_icon")) {
                public void actionPerformed(ActionEvent evt) {
                    new IconForm(form.getGroup()).show();
                    form.setTitleIcon(Icons.getKeePassIcon(form.getGroup().getIconId()));
                }
            };
            defaultCommand = 0; // back
        }
    }
}
