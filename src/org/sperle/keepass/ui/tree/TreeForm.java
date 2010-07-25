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

import java.io.IOException;
import java.util.Vector;

import org.sperle.keepass.kdb.KdbEntry;
import org.sperle.keepass.kdb.KdbGroup;
import org.sperle.keepass.kdb.KdbItem;
import org.sperle.keepass.kdb.KeePassDatabase;
import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.command.ChangeTitleCommand;
import org.sperle.keepass.ui.edit.EntryForm;
import org.sperle.keepass.ui.edit.GroupForm;
import org.sperle.keepass.ui.form.EditDBForm;
import org.sperle.keepass.ui.form.Forms;
import org.sperle.keepass.ui.form.ItemListForm;
import org.sperle.keepass.ui.form.StatisticsForm;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.io.Saver;
import org.sperle.keepass.ui.search.SearchForm;
import org.sperle.keepass.ui.settings.Settings;
import org.sperle.keepass.ui.source.file.FileChooserForm;
import org.sperle.keepass.ui.util.Vectors;

import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.SelectionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.DefaultListModel;
import com.sun.lwuit.util.Log;

public class TreeForm extends ItemListForm {
    private KeePassDatabase kdb;
    private KdbGroup parentGroup;
    private List list;
    
    private Command defaultCommand;
    
    public TreeForm(final KeePassMobile app, final KeePassDatabase kdb, final KdbGroup parentGroup) {
        super(app);
        this.kdb = kdb;
        this.parentGroup = parentGroup;
        
        String title = null;
        if(parentGroup == null) {
            title = kdb.getDatabaseName();
        } else {
            title = kdb.getGroupPath(parentGroup);
        }
        setTitle(title);
        
        setLayout(new BorderLayout());
        setScrollable(false);
        
        // is called implicitly with SelectionListener
        //app.getCommandManager().addCommands(this, createCommands(), defaultCommand);
        
        list = new List(getTreeObjects());
        if(app.getSettings().getBoolean(Settings.QUICK_VIEW))
             list.setListCellRenderer(new QuickViewTreeListCellRenderer(app, kdb, app.isFastUI()));
        else list.setListCellRenderer(new TreeListCellRenderer(app, kdb, app.isFastUI()));
        list.setOrientation(List.VERTICAL);
        list.setFixedSelection(List.FIXED_NONE_CYCLIC);
        if(!app.isFastUI()) list.setSmoothScrolling(true);
        else list.setSmoothScrolling(false);
        list.addSelectionListener(new SelectionListener() {
            public void selectionChanged(int oldSelected, int newSelected) {
                if(list.getSelectedItem() != null) {
                }
//                if(quickView) { // TODO bug: quick view does not work, when group consists of subgroups and entries!
//                    Object oldObject = list.getModel().getItemAt(oldSelected);
//                    Object newObject = list.getModel().getItemAt(newSelected);
//                    if(oldObject != null && newObject != null && !oldObject.getClass().equals(newObject.getClass())) {
//                        TreeForm.this.revalidate();
//                    }
//                }
                app.getCommandManager().addCommands(TreeForm.this, createCommands(), defaultCommand);
            }
        });
        list.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(list.getSelectedItem() != null) {
                    showSelectedItem();
                }
            }
        });
        if(list.getModel().getSize() > 0) {
            list.setSelectedIndex(0);
        }
        addGameKeyListener(Display.GAME_RIGHT, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(list.getSelectedItem() != null) {
                    showSelectedItem();
                }
            }
        });
        addGameKeyListener(Display.GAME_LEFT, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(parentGroup != null) {
                    showParents();
                }
            }
        });
        addComponent(BorderLayout.CENTER, list);
    }
    
    private Command[] createCommands() {
        KdbItem selected = (KdbItem)list.getSelectedItem();
        
        int i = 0;
        Command[] commands = new Command[13];
        commands[i++] = new Command(Messages.get("search")) {
            public void actionPerformed(ActionEvent evt) {
                SearchForm.create(app, kdb).show();
            }
        };
        if(selected != null && !kdb.isBackupItem(selected)) {
            commands[i++] = new ChangeTitleCommand((selected instanceof KdbGroup) ? "edit_group" : "edit_entry") {
                public void actionPerformed(ActionEvent evt) { 
                    if(list.getSelectedItem() != null) {
                        editSelectedItem();
                    }
                }
            };
        }
        if(selected != null && !(kdb.isBackupItem(selected) && selected instanceof KdbGroup)) {
            commands[i++] = new ChangeTitleCommand((selected instanceof KdbGroup) ? "delete_group" : "delete_entry") {
                public void actionPerformed(ActionEvent evt) { 
                    if(list.getSelectedItem() != null) {
                        deleteItem(list.getSelectedItem());
                    }
                }
            };
        }
        if(parentGroup != null && !kdb.isBackupGroup(parentGroup)) { // do not show on root and in backup group
            commands[i++] = new Command(Messages.get("create_entry")) {
                public void actionPerformed(ActionEvent evt) {
                    new EntryForm(app, kdb, kdb.createEntry(parentGroup), true, app.isFastUI()).show();
                }
            };
        }
        if(parentGroup == null || !kdb.isBackupGroup(parentGroup)) { // do not show in backup group
            commands[i++] = new Command(Messages.get("create_group")) {
                public void actionPerformed(ActionEvent evt) {
                    new GroupForm(app, kdb, kdb.createGroup(parentGroup), true).show();
                }
            };
        }
        if(selected != null && selected instanceof KdbEntry) {
            commands[i++] = new ChangeTitleCommand("cut_entry") {
                public void actionPerformed(ActionEvent evt) { 
                    if(list.getSelectedItem() != null && list.getSelectedItem() instanceof KdbEntry) {
                        cutSelectedEntry();
                    }
                }
            };
        }
        if(app.getClipboardEntry() != null && parentGroup != null && !kdb.isBackupGroup(parentGroup)) { // do not show on root and in backup group
            commands[i++] = new ChangeTitleCommand("paste_entry") {
                public void actionPerformed(ActionEvent evt) {
                    if(app.getClipboardEntry() != null) {
                        pasteClipboardEntry();
                    }
                }
            };
        }
        if(parentGroup != null) { // do not show on root
            commands[i++] = new Command(Messages.get("top")) {
                public void actionPerformed(ActionEvent evt) {
                    showRootGroups();
                }
            };
        }
        if(parentGroup == null) { // only show on root
            commands[i++] = new Command(Messages.get("edit_db")) {
                public void actionPerformed(ActionEvent evt) {
                    new EditDBForm(app, kdb, app.isFastUI()).show();
                }
            };
        }
        if(parentGroup == null && kdb.getPerformanceStatistics() != null) { // only show on root and if not newly created
            commands[i++] = new Command(Messages.get("stats")) {
                public void actionPerformed(ActionEvent evt) {
                    StatisticsForm.create(app, kdb).show();
                }
            };
        }
        commands[i++] = new Command(Messages.get("help")) {
            public void actionPerformed(ActionEvent evt) {
                Forms.showHelp(Messages.get("tree_help"));
            }
        };
        if(kdb.hasChanged()) {
            commands[i++] = new Command(Messages.get("save")) {
                public void actionPerformed(ActionEvent evt) {
                    saveDatabase(false);
                }
            };
        }
        commands[i++] = new Command(Messages.get("close")) {
            public void actionPerformed(ActionEvent evt) {
                if(kdb.hasChanged()) {
                    if(Dialog.show(Messages.get("save_changes"), Messages.get("save_changes_text"), Messages.get("yes"), Messages.get("no"))) {
                        saveDatabase(true);
                    } else {
                        closeDatabase();
                    }
                } else {
                    closeDatabase();
                }
            }
        };
        
        defaultCommand = commands[0]; // search
        return commands;
    }
    
    protected void goBack() {
        if(parentGroup != null) {
            showParents();
        }
    }
    
    private void saveDatabase(final boolean closeAfterSave) {
        if(kdb.getFileName().startsWith("file:")) { // file URL: db was loaded from file
            new Saver(kdb).save();
            Dialog.show(Messages.get("db_saved"), Messages.get("db_saved_sucessfully") 
                    + " " + kdb.getFileName(), Messages.get("ok"), null);
            if(closeAfterSave) closeDatabase();
        } else {  // no file URL: db was newly created and the filename field of the db only holds the given name (during creation)
            FileChooserForm fileChooser = new FileChooserForm(app, new FileChooserForm.FileChooserCallback() {
                public void choosen(String foldername) {
                    String filename = foldername + (foldername.endsWith("/") ? "" : "/") + 
                            (kdb.getFileName().endsWith(".kdb") ? kdb.getFileName() : (kdb.getFileName() + ".kdb"));
                    Saver saver = new Saver(kdb);
                    saver.setFilename(filename);
                    if(saver.save()) {
                        Dialog.show(Messages.get("db_saved"), Messages.get("db_saved_sucessfully") 
                                + " " + filename, Messages.get("ok"), null);
                    }
                    try {
                        if(app.getSettings().available()) app.getSettings().set(Settings.LAST_FILE, filename);
                        if(app.getSettings().available()) app.getSettings().set(Settings.LAST_FOLDER, filename.substring(0, filename.lastIndexOf('/')+1));
                    } catch (IOException e) {
                        Log.p("Could not write last file settings - " + e.toString(), Log.ERROR);
                    }
                    
                    if(closeAfterSave) closeDatabase();
                    else TreeForm.this.show();
                }
                public void canceled() {
                    if(closeAfterSave) closeDatabase();
                    else TreeForm.this.show();
                }
                public void errorOccured(Exception e) {
                    Log.p("Error choosing folder to save database file - " + e.toString(), Log.ERROR);
                    Dialog.show(Messages.get("choosing_error"), Messages.get("choosing_error_text") + e.getMessage(), Messages.get("ok"), null);
                    if(closeAfterSave) closeDatabase();
                    else TreeForm.this.show();
                }
            }, app.isFastUI());
            fileChooser.setDirectoriesOnly(true);
            fileChooser.show();
        }
    }
    
    private void closeDatabase() {
        kdb.close();
        app.emptyClipboard();
        app.stopSecurityTimer();
        app.showMainMenu();
    }
    
    private DefaultListModel getTreeObjects() {
        Vector entries = parentGroup == null ? kdb.getRootGroups() : Vectors.append(kdb.getChildGroups(parentGroup), kdb.getEntries(parentGroup));
        if(!app.getSettings().getBoolean(Settings.SHOW_BACKUP)) {
            Object backupGroup = null;
            for (int i = 0; i < entries.size(); i++) {
                if (entries.elementAt(i) instanceof KdbGroup) {
                    KdbGroup group = (KdbGroup)entries.elementAt(i);
                    if (kdb.isBackupGroup(group)) {
                        backupGroup = group;
                        break;
                    }
                }
            }
            if(backupGroup != null) entries.removeElement(backupGroup);
        }
        return new DefaultListModel(Vectors.toItemArray(entries));
    }
    
    public void setSelected(KdbItem item) {
        list.setSelectedItem(item);
    }
    
    public void refresh() {
        list.setModel(getTreeObjects());
        app.getCommandManager().addCommands(this, createCommands(), defaultCommand);
    }
    
    private void editSelectedItem() {
        Object selectedItem = list.getSelectedItem();
        if(selectedItem instanceof KdbGroup) {
            showGroup();
        } else {
            showEntry(true);
        }
    }
    
    private void cutSelectedEntry() {
        KdbEntry selectedItem = (KdbEntry) list.getSelectedItem();
        app.addToClipboard(selectedItem);
    }
    
    private void pasteClipboardEntry() {
        kdb.moveEntry(app.getClipboardEntry(), parentGroup);
        app.emptyClipboard();
        app.getCommandManager().addCommands(this, createCommands(), defaultCommand);
        refresh();
    }
    
    private void deleteItem(Object selectedItem) {
        if(selectedItem instanceof KdbGroup) {
            if(kdb.isEmpty((KdbGroup)selectedItem)) {
                if(Dialog.show(Messages.get("delete_group"), Messages.get("delete_group_text"), Messages.get("yes"), Messages.get("no"))) {
                    kdb.removeGroup((KdbGroup)selectedItem);
                    refresh();
                }
            } else {
                Dialog.show(Messages.get("group_notempty"), Messages.get("group_notempty_text"), Messages.get("ok"), null);
            }
        } else {
            KdbEntry selectedEntry = (KdbEntry)selectedItem;
            if(kdb.isBackupEntry(selectedEntry)) {
                if(Dialog.show(Messages.get("delete_forever"), Messages.get("delete_forever_text"), Messages.get("yes"), Messages.get("no"))) {
                    kdb.removeEntry(selectedEntry);
                    refresh();
                }
            } else {
                if(Dialog.show(Messages.get("delete_entry"), Messages.get("delete_entry_text"), Messages.get("yes"), Messages.get("no"))) {
                    kdb.removeEntry(selectedEntry);
                    refresh();
                }
            }
        }
    }
    
    private void showSelectedItem() {
        Object selectedItem = list.getSelectedItem();
        if(selectedItem instanceof KdbGroup) {
            showChilds();
        } else {
            showEntry(false);
        }
    }
    
    private void showChilds() {
        Forms.setTransitionOut(this, true, app.isFastUI());
        TreeForm treeForm = new TreeForm(app, kdb, (KdbGroup)list.getSelectedItem());
        treeForm.show();
    }
    
    private void showParents() {
        Forms.setTransitionOut(this, false, app.isFastUI());
        TreeForm treeForm = new TreeForm(app, kdb, kdb.getParentGroup(parentGroup));
        treeForm.setSelected(parentGroup);
        treeForm.show();
    }
    
    private void showEntry(boolean editTitle) {
        Forms.setNoTransitionOut(this);
        new EntryForm(app, kdb, (KdbEntry)list.getSelectedItem(), editTitle, app.isFastUI()).show();
    }
    
    private void showGroup() {
        Forms.setNoTransitionOut(this);
        new GroupForm(app, kdb, (KdbGroup)list.getSelectedItem(), false).show();
    }
    
    private void showRootGroups() {
        Forms.setTransitionOut(this, false, app.isFastUI());
        TreeForm treeForm = new TreeForm(app, kdb, null);
        treeForm.show();
    }
}
