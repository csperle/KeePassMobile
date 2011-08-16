package org.sperle.keepass.ui.form;

import org.sperle.keepass.kdb.KeePassDatabaseException;
import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.command.AbstractFormCommands;
import org.sperle.keepass.ui.command.KeePassMobileCommand;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.source.file.FileChooserForm;

import com.sun.lwuit.Dialog;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.util.Log;

public class EditDBFormCommands extends AbstractFormCommands {

    private final EditDBForm form;
    
    public EditDBFormCommands(final EditDBForm form) {
        this.form = form;
        
        commands = new KeePassMobileCommand[5];
        commands[0] = new KeePassMobileCommand(Messages.get("add_keyfile")) {
                public void actionPerformed(ActionEvent evt) {
                    setKeyFile();
                }
        };
        commands[1] = new KeePassMobileCommand(Messages.get("change_keyfile")) {
                public void actionPerformed(ActionEvent evt) {
                    setKeyFile();
                }
        };
        commands[2] = new KeePassMobileCommand(Messages.get("del_keyfile")) {
                public void actionPerformed(ActionEvent evt) {
                    try {
                        KeePassMobile.instance().getKeePassMobileIO().removeKeyFile(form.getKdb());
                    } catch (KeePassDatabaseException e) {
                        // not possible that wrong database version is used here
                    }
                    form.updateCommands();
                    Dialog.show(Messages.get("keyfile_removed"), Messages.get("keyfile_removed_text"), Messages.get("ok"), null);
                }
        };
        commands[3] = new KeePassMobileCommand(Messages.get("help")) {
            public void actionPerformed(ActionEvent evt) {
                Forms.showHelp(Messages.get("editdb_help"));
            }
        };
        commands[4] = (KeePassMobileCommand) form.getBackCommand();
        defaultCommand = 4;
    }
    
    public KeePassMobileCommand[] getCommands() {
        return commands;
    }

    public KeePassMobileCommand getDefaultCommand() {
        return commands[defaultCommand];
    }

    public void update() {
        commands[0].setEnabled(!form.getKdb().hasKeyFile()); // add key
        commands[1].setEnabled(form.getKdb().hasKeyFile());  // change key
        commands[2].setEnabled(form.getKdb().hasKeyFile());  // del key
    }

    private void setKeyFile() {
        FileChooserForm fileChooser = new FileChooserForm(Messages.get("select_key"), new FileChooserForm.FileChooserCallback() {
            public void choosen(String filename) {
                try {
                    KeePassMobile.instance().getKeePassMobileIO().setKeyFile(form.getKdb(), filename);
                    Log.p("Keyfile added/changed successfully", Log.DEBUG);
                    form.updateCommands();
                    Dialog.show(Messages.get("keyfile_set"), Messages.get("keyfile_set_sucessfully"), Messages.get("ok"), null);
                } catch (Exception e) {
                    Log.p("Error adding/changing keyfile - " + e.toString(), Log.ERROR);
                    Dialog.show(Messages.get("loading_error"), Messages.get("loading_error_text") + e.getMessage(), Messages.get("ok"), null);
                } finally {
                    form.show();
                }
            }
            public void canceled() {
                form.show();
            }
            public void errorOccured(Exception e) {
                Log.p("Error choosing keyfile - " + e.toString(), Log.ERROR);
                Dialog.show(Messages.get("choosing_error"), Messages.get("choosing_error_text") + e.getMessage(), Messages.get("ok"), null);
                form.show();
            }
        });
        fileChooser.show();
    }
}
