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

import org.sperle.keepass.kdb.KdbDate;
import org.sperle.keepass.kdb.KdbEntry;
import org.sperle.keepass.ui.component.DateField;
import org.sperle.keepass.ui.form.IconTitleForm;
import org.sperle.keepass.ui.form.ItemListForm;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.icon.Icons;
import org.sperle.keepass.ui.util.DateFormatter.ParseException;
import org.sperle.keepass.util.Passwords;

import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.Label;
import com.sun.lwuit.TabbedPane;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.util.Log;

public class EntryForm extends IconTitleForm {

    private final KdbEntry entry;
    
    private TabbedPane tabbedPane;
    private Container detailsPanel;
    private Label titleLabel;
    private TextArea titleField;
    private Label userLabel;
    private TextArea userField;
    private Label passLabel;
    private TextArea passField;
    private Label urlLabel;
    private TextArea urlField;
    private Label attachLabel;
    private TextArea attachField;
    private Label expiryLabel;
    private DateField expiryField;
    private TextArea notesField;
    
    public EntryForm(final KdbEntry entry, final boolean editTitle) {
        super(entry.expired() ? Icons.getExpiredIcon() : Icons.getKeePassIcon(entry.getIconId()), entry.getTitle() != null ? entry.getTitle() : Messages.get("create_entry"));
        this.entry = entry;
        
        entry.access();
        
        setLayout(new BorderLayout());
        setScrollable(false);
        
        if(entry.expired()) {
            this.getTitleComponent().getStyle().setFgColor(0xD60000); // red
        }
        
        tabbedPane = new TabbedPane(TabbedPane.TOP);
        
        detailsPanel = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        detailsPanel.setScrollableY(true);
        
        if(editTitle) {
            titleLabel = new Label(Messages.get("title"));
            detailsPanel.addComponent(titleLabel);
            titleField = new TextArea(entry.getTitle() != null ? entry.getTitle() : "");
            titleField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    entry.setTitle(titleField.getText());
                }
            });
            detailsPanel.addComponent(titleField);
            this.setFocused(titleField);
        }
        userLabel = new Label(Messages.get("user_name"));
        detailsPanel.addComponent(userLabel);
        userField = new TextArea(entry.getUsername() != null ? entry.getUsername() : "");
        userField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                entry.setUsername(userField.getText());
            }
        });
        detailsPanel.addComponent(userField);
        
        passLabel = new Label(Messages.get("password"));
        detailsPanel.addComponent(passLabel);
        String entryPasswd = Passwords.toString(entry.getPassword());
        passField = new TextArea(entryPasswd != null ? entryPasswd : "");
        //passField.getStyle().setFont(Fonts.getPasswdFont()); -> do not use: does not support Umlaute at the moment
        passField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                entry.setPassword(Passwords.fromString(passField.getText()));
            }
        });
        detailsPanel.addComponent(passField);
        
        urlLabel = new Label(Messages.get("url"));
        detailsPanel.addComponent(urlLabel);
        urlField = new TextArea(entry.getUrl() != null ? entry.getUrl() : "");
        urlField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                entry.setUrl(urlField.getText());
            }
        });
        detailsPanel.addComponent(urlField);
        
        try {
            String expiry = null;
            if(entry.expired()) {
                expiry = Messages.get("expiry_expired");
            } else {
                expiry = Messages.get("expiry");
            }
            expiryLabel = new Label(expiry);
            expiryField = new DateField(KdbDate.NEVER_EXPIRES.equals(entry.getExpirationTime()) ? null : entry.getExpirationTime());
            expiryField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    try {
                        KdbDate expDate = expiryField.getDate();
                        entry.setExpirationTime(expDate != null ? expDate : KdbDate.NEVER_EXPIRES);
                    } catch (final ParseException e) {
                        try {expiryField.setDate(KdbDate.NEVER_EXPIRES.equals(entry.getExpirationTime()) ? null : entry.getExpirationTime());} catch (ParseException e1) {}
                        Log.p("Could not parse date: " + e.getMessage(), Log.WARNING);
                        // have to call dialog after this action event is processed
                        Display.getInstance().callSerially(new Runnable() {
                            public void run() {
                                Dialog.show(Messages.get("parsing_date_error"), Messages.get("parsing_date_error_text")+ ": " + Messages.get(e.getMessageKey()), Messages.get("ok"), null);
                            }
                        });
                    }
                }
            });
            detailsPanel.addComponent(expiryLabel);
            detailsPanel.addComponent(expiryField);
        } catch (final ParseException e) {
            Log.p("Could not format date: " + e.getMessage(), Log.WARNING);
            Dialog.show(Messages.get("parsing_date_error"), Messages.get("parsing_date_error_text")+ ": " + Messages.get(e.getMessageKey()), Messages.get("ok"), null);
        }
        
        if(entry.hasAttachment()) {
            addAttachmentComponents();
        } else {
            removeAttachmentComponents();
        }
        
        tabbedPane.addTab(Messages.get("details"), detailsPanel);
        
        Container notesPanel = new Container(new BorderLayout());
        notesPanel.setScrollableY(false);
        notesField = new TextArea(entry.getNotes() != null ? entry.getNotes() : "", 5, 15);
        notesField.getSelectedStyle().setBgColor(0x6600cc);
        notesField.setMaxSize(4000);
        notesField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                entry.setNotes(notesField.getText());
            }
        });
        notesPanel.addComponent(BorderLayout.CENTER, notesField);
        tabbedPane.addTab(Messages.get("notes"), notesPanel);
        
        addComponent(BorderLayout.CENTER, tabbedPane);
        updateCommands();
    }
    
    protected void goBack() {
        ItemListForm itemList = (ItemListForm)previousForm;
        itemList.refresh();
        itemList.setSelected(entry);
        itemList.show();
    }
    
    protected void addAttachmentComponents() {
        if(attachLabel == null) {
            attachLabel = new Label(Messages.get("attachment"));
            detailsPanel.addComponent(attachLabel);
        }
        if(attachField == null) {
            attachField = new TextArea(entry.getBinaryDescription() != null ? entry.getBinaryDescription() : "");
            attachField.setEditable(false);
            detailsPanel.addComponent(attachField);
        }
        updateCommands();
    }
    
    protected void removeAttachmentComponents() {
        if(attachLabel != null) {
            detailsPanel.removeComponent(attachLabel);
            attachLabel = null;
        }
        if(attachField != null) {
            detailsPanel.removeComponent(attachField);
            attachField = null;
        }
        updateCommands();
    }
    
    public KdbEntry getEntry() {
        return entry;
    }

    public void setPasswordText(String password) {
        passField.setText(password);
    }

    public void setPasswordFieldFocused() {
        setFocused(passField);
    }
}
