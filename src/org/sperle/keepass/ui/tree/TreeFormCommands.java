package org.sperle.keepass.ui.tree;

import java.io.IOException;

import org.sperle.keepass.kdb.KdbEntry;
import org.sperle.keepass.kdb.KdbGroup;
import org.sperle.keepass.kdb.KdbItem;
import org.sperle.keepass.kdb.KeePassDatabase;
import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.command.AbstractFormCommands;
import org.sperle.keepass.ui.command.KeePassMobileCommand;
import org.sperle.keepass.ui.edit.EntryForm;
import org.sperle.keepass.ui.edit.GroupForm;
import org.sperle.keepass.ui.form.EditDBForm;
import org.sperle.keepass.ui.form.Forms;
import org.sperle.keepass.ui.form.StatisticsForm;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.io.Saver;
import org.sperle.keepass.ui.search.SearchForm;
import org.sperle.keepass.ui.settings.Settings;
import org.sperle.keepass.ui.source.file.FileChooserForm;

import com.sun.lwuit.Dialog;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.util.Log;

public class TreeFormCommands extends AbstractFormCommands {
    
    protected TreeForm form;

    /****
     * ATTENTION: IF YOU CHANGE SOMETHING HERE, YOU HAVE TO CHANGE TreeFormBlackBerryCommands, TOO!!!
     ****/
    public TreeFormCommands(final TreeForm form) {
        this.form = form;
        
        commands = new KeePassMobileCommand[13];
        commands[0] = new KeePassMobileCommand(Messages.get("search")) {
            public void actionPerformed(ActionEvent evt) {
                SearchForm.create(form.getKdb()).show();
            }
        };
        commands[1] = new KeePassMobileCommand(Messages.get("edit_entry")) {
            public void actionPerformed(ActionEvent evt) { 
                if(form.getList().getSelectedItem() != null) {
                    editSelectedItem();
                }
            }
        };
        commands[2] = new KeePassMobileCommand(Messages.get("delete_entry")) {
            public void actionPerformed(ActionEvent evt) { 
                if(form.getList().getSelectedItem() != null) {
                    deleteItem(form.getList().getSelectedItem());
                }
            }
        };
        commands[3] = new KeePassMobileCommand(Messages.get("create_entry")) {
            public void actionPerformed(ActionEvent evt) {
                new EntryForm(form.getKdb().createEntry(form.getParentGroup()), true).show();
            }
        };
        commands[4] = new KeePassMobileCommand(Messages.get("create_group")) {
            public void actionPerformed(ActionEvent evt) {
                new GroupForm(form.getKdb().createGroup(form.getParentGroup()), true).show();
            }
        };
        commands[5] = new KeePassMobileCommand(Messages.get("cut_entry")) {
            public void actionPerformed(ActionEvent evt) { 
                if(form.getList().getSelectedItem() != null && form.getList().getSelectedItem() instanceof KdbEntry) {
                    cutSelectedEntry();
                }
            }
        };
        commands[6] = new KeePassMobileCommand(Messages.get("paste_entry")) {
            public void actionPerformed(ActionEvent evt) {
                if(KeePassMobile.instance().getClipboardEntry() != null) {
                    pasteClipboardEntry();
                }
            }
        };
        commands[7] = new KeePassMobileCommand(Messages.get("top")) {
            public void actionPerformed(ActionEvent evt) {
                showRootGroups();
            }
        };
        commands[8] = new KeePassMobileCommand(Messages.get("edit_db")) {
            public void actionPerformed(ActionEvent evt) {
                new EditDBForm(form.getKdb()).show();
            }
        };
        commands[9] = new KeePassMobileCommand(Messages.get("stats")) {
            public void actionPerformed(ActionEvent evt) {
                StatisticsForm.create(form.getKdb()).show();
            }
        };
        commands[10] = new KeePassMobileCommand(Messages.get("help")) {
            public void actionPerformed(ActionEvent evt) {
                Forms.showHelp(Messages.get("tree_help"));
            }
        };
        commands[11] = new KeePassMobileCommand(Messages.get("save")) {
            public void actionPerformed(ActionEvent evt) {
                saveDatabase(false);
            }
        };
        commands[12] = new KeePassMobileCommand(Messages.get("close")) {
            public void actionPerformed(ActionEvent evt) {
                if(form.getKdb().hasChanged()) {
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
        defaultCommand = 0; // search
    }
    
    /****
     * ATTENTION: IF YOU CHANGE SOMETHING HERE, YOU HAVE TO CHANGE TreeFormBlackBerryCommands, TOO!!!
     ****/
    public void update() {
        final KdbItem selected = (KdbItem)form.getList().getSelectedItem();
        final KdbGroup parentGroup = form.getParentGroup();
        final KeePassDatabase kdb = form.getKdb();
        
        commands[1].setEnabled(selected != null && !kdb.isBackupItem(selected));
        commands[1].setCommandName(Messages.get((selected instanceof KdbGroup) ? "edit_group" : "edit_entry"));
        commands[2].setEnabled(selected != null && !(kdb.isBackupItem(selected) && selected instanceof KdbGroup));
        commands[2].setCommandName(Messages.get((selected instanceof KdbGroup) ? "delete_group" : "delete_entry"));
        commands[3].setEnabled(parentGroup != null && !kdb.isBackupGroup(parentGroup)); // do not show on root and in backup group
        commands[4].setEnabled(parentGroup == null || !kdb.isBackupGroup(parentGroup)); // do not show in backup group
        commands[5].setEnabled(selected != null && selected instanceof KdbEntry);
        commands[6].setEnabled(KeePassMobile.instance().getClipboardEntry() != null && parentGroup != null && !kdb.isBackupGroup(parentGroup)); // do not show on root and in backup group
        commands[7].setEnabled(parentGroup != null); // do not show on root
        commands[8].setEnabled(parentGroup == null); // only show on root
        commands[9].setEnabled(parentGroup == null && kdb.getPerformanceStatistics() != null); // only show on root and if not newly created
        commands[11].setEnabled(kdb.hasChanged());
    }

    private void saveDatabase(final boolean closeAfterSave) {
        final KeePassDatabase kdb = form.getKdb();
        if(kdb.getFileName().startsWith("file:")) { // file URL: db was loaded from file
            new Saver(KeePassMobile.instance().getKeePassMobileIO(), kdb).save();
            Dialog.show(Messages.get("db_saved"), Messages.get("db_saved_sucessfully") 
                    + " " + kdb.getFileName(), Messages.get("ok"), null);
            if(closeAfterSave) closeDatabase();
        } else {  // no file URL: db was newly created and the filename field of the db only holds the given name (during creation)
            FileChooserForm fileChooser = new FileChooserForm(Messages.get("select_folder"), new FileChooserForm.FileChooserCallback() {
                public void choosen(String foldername) {
                    String filename = foldername + (foldername.endsWith("/") ? "" : "/") + 
                            (kdb.getFileName().endsWith(".kdb") ? kdb.getFileName() : (kdb.getFileName() + ".kdb"));
                    Saver saver = new Saver(KeePassMobile.instance().getKeePassMobileIO(), kdb);
                    saver.setFilename(filename);
                    if(saver.save()) {
                        Dialog.show(Messages.get("db_saved"), Messages.get("db_saved_sucessfully") 
                                + " " + filename, Messages.get("ok"), null);
                    }
                    try {
                        if(KeePassMobile.instance().getSettings().available()) KeePassMobile.instance().getSettings().set(Settings.LAST_FILE, filename);
                        if(KeePassMobile.instance().getSettings().available()) KeePassMobile.instance().getSettings().set(Settings.LAST_FOLDER, filename.substring(0, filename.lastIndexOf('/')+1));
                    } catch (IOException e) {
                        Log.p("Could not write last file settings - " + e.toString(), Log.ERROR);
                    }
                    
                    if(closeAfterSave) closeDatabase();
                    else form.show();
                }
                public void canceled() {
                    if(closeAfterSave) closeDatabase();
                    else form.show();
                }
                public void errorOccured(Exception e) {
                    Log.p("Error choosing folder to save database file - " + e.toString(), Log.ERROR);
                    Dialog.show(Messages.get("choosing_error"), Messages.get("choosing_error_text") + e.getMessage(), Messages.get("ok"), null);
                    if(closeAfterSave) closeDatabase();
                    else form.show();
                }
            });
            fileChooser.setDirectoriesOnly(true);
            fileChooser.show();
        }
    }
    
    private void closeDatabase() {
        KeePassMobile.instance().getKeePassMobileIO().close(form.getKdb());
        KeePassMobile.instance().emptyClipboard();
        KeePassMobile.instance().stopSecurityTimer();
        KeePassMobile.instance().showMainMenu();
    }
    
    private void editSelectedItem() {
        Object selectedItem = form.getList().getSelectedItem();
        if(selectedItem instanceof KdbGroup) {
            showGroup();
        } else {
            showEntry(true);
        }
    }
    
    private void showEntry(boolean editTitle) {
        Forms.setNoTransitionOut(form);
        new EntryForm((KdbEntry)form.getList().getSelectedItem(), editTitle).show();
    }
    
    private void showGroup() {
        Forms.setNoTransitionOut(form);
        new GroupForm((KdbGroup)form.getList().getSelectedItem(), false).show();
    }
    
    private void deleteItem(Object selectedItem) {
        final KeePassDatabase kdb = form.getKdb();
        if(selectedItem instanceof KdbGroup) {
            if(kdb.isEmpty((KdbGroup)selectedItem)) {
                if(Dialog.show(Messages.get("delete_group"), Messages.get("delete_group_text"), Messages.get("yes"), Messages.get("no"))) {
                    kdb.removeGroup((KdbGroup)selectedItem);
                    form.refresh();
                }
            } else {
                Dialog.show(Messages.get("group_notempty"), Messages.get("group_notempty_text"), Messages.get("ok"), null);
            }
        } else {
            KdbEntry selectedEntry = (KdbEntry)selectedItem;
            if(kdb.isBackupEntry(selectedEntry)) {
                if(Dialog.show(Messages.get("delete_forever"), Messages.get("delete_forever_text"), Messages.get("yes"), Messages.get("no"))) {
                    kdb.removeEntry(selectedEntry);
                    form.refresh();
                }
            } else {
                if(Dialog.show(Messages.get("delete_entry"), Messages.get("delete_entry_text"), Messages.get("yes"), Messages.get("no"))) {
                    kdb.removeEntry(selectedEntry);
                    form.refresh();
                }
            }
        }
    }
    
    private void cutSelectedEntry() {
        KdbEntry selectedItem = (KdbEntry) form.getList().getSelectedItem();
        KeePassMobile.instance().addToClipboard(selectedItem);
    }
    
    private void pasteClipboardEntry() {
        form.getKdb().moveEntry(KeePassMobile.instance().getClipboardEntry(), form.getParentGroup());
        KeePassMobile.instance().emptyClipboard();
        form.refresh();
    }
    
    private void showRootGroups() {
        Forms.setTransitionOut(form, false);
        TreeForm treeForm = new TreeForm(form.getKdb(), null);
        treeForm.show();
    }
}
