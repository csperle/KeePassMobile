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

package org.sperle.keepass.ui.source.create;


import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.command.AbstractFormCommands;
import org.sperle.keepass.ui.command.KeePassMobileCommand;
import org.sperle.keepass.ui.form.Forms;
import org.sperle.keepass.ui.form.KeePassMobileForm;
import org.sperle.keepass.ui.i18n.Messages;

import com.sun.lwuit.Dialog;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.layouts.BoxLayout;

public class CreateDatabaseForm extends KeePassMobileForm {
    final CreateDatabaseSource createSource;
    
    private Label nameLabel;
    private TextArea nameField;
    private Label passwdLabel;
    private TextArea passwdField;
    private Label passwdLabel2;
    private TextArea passwdField2;
    
    public CreateDatabaseForm(final CreateDatabaseSource createSource) {
        super(Messages.get("new_db"));
        this.createSource = createSource;
        
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setScrollableY(true);
        
        nameLabel = new Label(Messages.get("enter_dbname"));
        addComponent(nameLabel);
        nameField = new TextArea();
        addComponent(nameField);
        passwdLabel = new Label(Messages.get("enter_password"));
        addComponent(passwdLabel);
        passwdField = new TextArea();
        if(!KeePassMobile.instance().isPasswordFieldWorkaroundEnabled()) {
            passwdField.setConstraint(TextArea.ANY | TextArea.PASSWORD);
        }
        addComponent(passwdField);
        passwdLabel2 = new Label(Messages.get("enter_password2"));
        addComponent(passwdLabel2);
        passwdField2 = new TextArea();
        if(!KeePassMobile.instance().isPasswordFieldWorkaroundEnabled()) {
            passwdField2.setConstraint(TextArea.ANY | TextArea.PASSWORD);
        }
        addComponent(passwdField2);
        updateCommands();
    }
    
    protected void goBack() {
        createSource.creationCanceled();
    }
    
    public static class FormCommands extends AbstractFormCommands {

        public FormCommands(final CreateDatabaseForm form) {
            commands = new KeePassMobileCommand[4];
            commands[0] = new KeePassMobileCommand(Messages.get("create")) {
                public void actionPerformed(ActionEvent evt) {
                    if("".equals(form.nameField.getText())) {
                        Dialog.show(Messages.get("dbname_empty"), Messages.get("dbname_empty_text"), Messages.get("ok"), null);
                        return;
                    }
                    if("".equals(form.passwdField.getText())) {
                        Dialog.show(Messages.get("password_empty"), Messages.get("password_empty_text"), Messages.get("ok"), null);
                        return;
                    }
                    if(!form.passwdField.getText().equals(form.passwdField2.getText())) {
                        Dialog.show(Messages.get("password_mismatch"), Messages.get("password_mismatch_text"), Messages.get("ok"), null);
                        form.passwdField.setText("");
                        form.passwdField2.setText("");
                        return;
                    }
                    form.createSource.create(form.nameField.getText(), form.passwdField.getText(), null);
                }
            };
            commands[1] = new KeePassMobileCommand(Messages.get("use_keyfile")) {
                public void actionPerformed(ActionEvent evt) {
                    if("".equals(form.nameField.getText())) {
                        Dialog.show(Messages.get("dbname_empty"), Messages.get("dbname_empty_text"), Messages.get("ok"), null);
                        return;
                    }
                    if(!form.passwdField.getText().equals(form.passwdField2.getText())) {
                        Dialog.show(Messages.get("password_mismatch"), Messages.get("password_mismatch_text"), Messages.get("ok"), null);
                        form.passwdField.setText("");
                        form.passwdField2.setText("");
                        return;
                    }
                    form.createSource.openKeyFile(form.nameField.getText(), ("".equals(form.passwdField.getText()) ? null : form.passwdField.getText()));
                }
            };
            commands[2] = new KeePassMobileCommand(Messages.get("help")) {
                public void actionPerformed(ActionEvent evt) {
                    Forms.showHelp(Messages.get("createdb_help"));
                }
            };
            commands[3] = new KeePassMobileCommand(Messages.get("cancel")) {
                public void actionPerformed(ActionEvent evt) {
                    form.goBack();
                }
            };
            defaultCommand = 0; // create
        }
    }
}
