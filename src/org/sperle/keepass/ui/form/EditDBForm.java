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

import org.sperle.keepass.kdb.KeePassDatabase;
import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.i18n.Messages;

import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class EditDBForm extends KeePassMobileForm {
    public final static int MIN_ROUNDS = 50;
    public final static int DEFAULT_ROUNDS = 300;
    public final static int MAX_ROUNDS = 60000;
    
    private KeePassDatabase kdb;
    
    private Label roundsLabel;
    private TextArea roundsField;
    private Label passwdLabel;
    private TextArea passwdField;
    private Label passwd2Label;
    private TextArea passwd2Field;
    
    public EditDBForm(final KeePassDatabase kdb) {
        super(Messages.get("edit_db"));
        this.kdb = kdb;
        
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setScrollableY(true);
        
        roundsLabel = new Label(Messages.get("editdb_rounds"));
        addComponent(roundsLabel);
        roundsField = new TextArea(""+ kdb.getNumKeyEncRounds());
        roundsField.setConstraint(TextArea.NUMERIC);
        roundsField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                kdb.setNumKeyEncRounds(getRounds());
            }
        });
        addComponent(roundsField);
        
        passwdLabel = new Label(Messages.get("editdb_newpasswd"));
        addComponent(passwdLabel);
        passwdField = new TextArea();
        if(!KeePassMobile.instance().isPasswordFieldWorkaroundEnabled()) {
            passwdField.setConstraint(TextArea.ANY | TextArea.PASSWORD);
        }
        addComponent(passwdField);
        passwd2Label = new Label(Messages.get("editdb_confirmpasswd"));
        addComponent(passwd2Label);
        passwd2Field = new TextArea();
        if(!KeePassMobile.instance().isPasswordFieldWorkaroundEnabled()) {
            passwd2Field.setConstraint(TextArea.ANY | TextArea.PASSWORD);
        }
        passwd2Field.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(passwdField.getText().equals(passwd2Field.getText())) {
                    kdb.setMasterPassword(passwdField.getText());
                    Display.getInstance().callSerially(new Runnable() {
                        public void run() {
                            Dialog.show(Messages.get("password_changed"), Messages.get("password_changed_text"), Messages.get("ok"), null);
                        }
                    });
                } else {
                    Display.getInstance().callSerially(new Runnable() {
                        public void run() {
                            Dialog.show(Messages.get("password_mismatch"), Messages.get("password_mismatch_text"), Messages.get("ok"), null);
                        }
                    });
                }
                passwdField.setText("");
                passwd2Field.setText("");
            }
        });
        addComponent(passwd2Field);
        updateCommands();
    }
    
    private int getRounds() {
        try {
            int rounds = Integer.parseInt(roundsField.getText());
            if(rounds > MAX_ROUNDS) {
                roundsField.setText(""+MAX_ROUNDS);
                return MAX_ROUNDS;
            }
            else if(rounds < MIN_ROUNDS) {
                roundsField.setText(""+MIN_ROUNDS);
                return MIN_ROUNDS;
            }
            else return rounds;
        } catch (NumberFormatException e) {
            roundsField.setText(""+kdb.getNumKeyEncRounds());
            return kdb.getNumKeyEncRounds();
        }
    }
    
    public KeePassDatabase getKdb() {
        return kdb;
    }
}
