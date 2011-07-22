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

package org.sperle.keepass.ui.passgen;

import java.util.Vector;

import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.edit.EntryForm;
import org.sperle.keepass.ui.form.Forms;
import org.sperle.keepass.ui.form.KeePassMobileForm;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.util.SimplePasswordGenerator;
import org.sperle.keepass.util.Passwords;

import com.sun.lwuit.CheckBox;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.layouts.GridLayout;
import com.sun.lwuit.list.DefaultListCellRenderer;
import com.sun.lwuit.list.DefaultListModel;

public class PassgenForm extends KeePassMobileForm {
    private static final int GENERATE_PASSWORDS = 10;
    private static final int MIN_LENGTH = 3;
    private static final int DEFAULT_LENGTH = 10;
    private static final int MAX_LENGTH = 64;
    
    private EntryForm entryForm;
    
    private Container optionsPanel;
    private TextArea lengthField;
    private CheckBox lowBox;
    private CheckBox capBox;
    private CheckBox numBox;
    private CheckBox specBox;
    
    private List passwordList;
    
    private SimplePasswordGenerator generator;
    
    private static PassgenForm instance;
    public static synchronized PassgenForm create(EntryForm entryForm) {
        if (instance == null) {
            instance = new PassgenForm();
            instance.generator = new SimplePasswordGenerator(KeePassMobile.instance().createRandom());
        }
        instance.entryForm = entryForm;
        instance.generateNewPasswordList();
        return instance;
    }
    
    private PassgenForm() {
        super(Messages.get("passgen"));
        
        setLayout(new BorderLayout());
        setScrollable(false);
        
        addComponent(BorderLayout.NORTH, getOptionsPanel());
        addComponent(BorderLayout.CENTER, getPasswordList());
        updateCommands();
    }
    
    private Container getOptionsPanel() {
        if(optionsPanel == null) {
            optionsPanel = new Container();
            optionsPanel.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
            optionsPanel.setScrollable(false);
            
            Container lengthPanel = new Container();
            lengthPanel.setLayout(new GridLayout(1, 2));
            lengthPanel.addComponent(new Label(Messages.get("passgen_length")));
            lengthField = new TextArea(""+DEFAULT_LENGTH);
            lengthField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    generateNewPasswordList();
                }
            });
            lengthPanel.addComponent(lengthField);
            optionsPanel.addComponent(lengthPanel);
            
            Container checkPanel = new Container();
            checkPanel.setLayout(new GridLayout(1, 4));
            lowBox = new CheckBox("a-z");
            lowBox.setSelected(true);
            lowBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    generateNewPasswordList();
                }
            });
            checkPanel.addComponent(lowBox);
            capBox = new CheckBox("A-Z");
            capBox.setSelected(true);
            capBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    generateNewPasswordList();
                }
            });
            checkPanel.addComponent(capBox);
            numBox = new CheckBox("1-9");
            numBox.setSelected(true);
            numBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    generateNewPasswordList();
                }
            });
            checkPanel.addComponent(numBox);
            specBox = new CheckBox("!#$");
            specBox.setSelected(true);
            specBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    generateNewPasswordList();
                }
            });
            checkPanel.addComponent(specBox);
            optionsPanel.addComponent(checkPanel);
        }
        return optionsPanel;
    }
    
    public List getPasswordList() {
        if(passwordList == null) {
            passwordList = new List();
            passwordList.setListCellRenderer(new DefaultListCellRenderer(false));
            passwordList.setOrientation(List.VERTICAL);
            passwordList.setFixedSelection(List.FIXED_NONE_CYCLIC);
            if(!KeePassMobile.instance().isFastUI()) passwordList.setSmoothScrolling(true);
            else passwordList.setSmoothScrolling(false);
            passwordList.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if(Dialog.show(Messages.get("change_password"), Messages.get("change_password_text"), Messages.get("yes"), Messages.get("no"))) {
                        String password = (String)passwordList.getSelectedItem();
                        entryForm.getEntry().setPassword(Passwords.fromString(password));
                        entryForm.setPasswordText(password);
                    }
                    Forms.setNoTransitionOut(PassgenForm.this);
                    entryForm.show();
                    entryForm.setPasswordFieldFocused();
                }
            });
        }
        return passwordList;
    }
    
    private int getLength() {
        try {
            int length = Integer.parseInt(lengthField.getText());
            if(length > MAX_LENGTH) return MAX_LENGTH;
            else if(length < MIN_LENGTH) return MIN_LENGTH;
            else return length;
        } catch (NumberFormatException e) {
            lengthField.setText(""+DEFAULT_LENGTH);
            return DEFAULT_LENGTH;
        }
    }
    
    private void generateNewPasswordList() {
        if(!lowBox.isSelected() && !capBox.isSelected() && !numBox.isSelected() && !specBox.isSelected()) {
            lowBox.setSelected(true); 
            capBox.setSelected(true);
            numBox.setSelected(true);
            specBox.setSelected(true);
        }
        int length = getLength();
        lengthField.setText(""+length);
        passwordList.setModel(new DefaultListModel(generatePasswords(length, lowBox.isSelected(), 
                capBox.isSelected(), numBox.isSelected(), specBox.isSelected())));
        passwordList.setSelectedIndex(0);
        setFocused(passwordList);
    }
    
    private Vector generatePasswords(int length, boolean low, boolean cap, boolean num, boolean spec) {
        Vector passwords = new Vector(GENERATE_PASSWORDS);
        for(int i = 0 ; i < GENERATE_PASSWORDS ; i++) {
            passwords.addElement(generator.generatePassword(length, low, cap, num, spec));
        }
        return passwords;
    }
}
