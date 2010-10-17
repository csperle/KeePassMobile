package org.sperle.keepass.ui.edit;

import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.command.AbstractFormCommands;
import org.sperle.keepass.ui.command.KeePassMobileCommand;
import org.sperle.keepass.ui.form.Forms;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.icon.Icons;
import org.sperle.keepass.ui.passgen.PassgenForm;
import org.sperle.keepass.ui.source.file.FileChooserForm;

import com.sun.lwuit.Dialog;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.util.Log;

public class EntryFormCommands extends AbstractFormCommands {
    
    private final EntryForm form;
    
    public EntryFormCommands(final EntryForm form) {
        this.form = form;
        
        commands = new KeePassMobileCommand[7];
        commands[0] = (KeePassMobileCommand) form.getBackCommand();
        commands[1] = new KeePassMobileCommand(Messages.get("add_attachment")) {
                public void actionPerformed(ActionEvent evt) {
                    addAttachment();
                }
        };
        commands[2] = new KeePassMobileCommand(Messages.get("save_attachment")) {
                public void actionPerformed(ActionEvent evt) {
                    saveAttachment();
                }
        };
        commands[3] = new KeePassMobileCommand(Messages.get("del_attachment")) {
                public void actionPerformed(ActionEvent evt) {
                    String desc = form.getEntry().getBinaryDescription();
                    form.getEntry().removeAttachment();
                    form.removeAttachmentComponents();
                    Dialog.show(Messages.get("attachment_removed"), Messages.get("attachment_removed_sucessfully") 
                            + " " + desc, Messages.get("ok"), null);
                }
        };
        commands[4] = new KeePassMobileCommand(Messages.get("change_icon")) {
            public void actionPerformed(ActionEvent evt) {
                new IconForm(form.getEntry()).show();
                form.setTitleIcon(Icons.getKeePassIcon(form.getEntry().getIconId()));
            }
        };
        commands[5] = new KeePassMobileCommand(Messages.get("passgen")) {
            public void actionPerformed(ActionEvent evt) {
                PassgenForm.create(form).show();
            }
        };
        commands[6] = new KeePassMobileCommand(Messages.get("help")) {
            public void actionPerformed(ActionEvent evt) {
                Forms.showHelp(Messages.get("entry_help"));
            }
        };
        defaultCommand = 0;
    }

    public void update() {
        commands[1].setEnabled(!form.getEntry().hasAttachment()); // add attach
        commands[2].setEnabled(form.getEntry().hasAttachment());  // save attach
        commands[3].setEnabled(form.getEntry().hasAttachment());  // del attach
    }
    
    private void addAttachment() {
        FileChooserForm fileChooser = new FileChooserForm(new FileChooserForm.FileChooserCallback() {
            public void choosen(String filename) {
                try {
                    KeePassMobile.instance().getKeePassMobileIO().addAttachment(form.getEntry(), filename);
                    Log.p("Attachment added successfully", Log.DEBUG);
                    form.addAttachmentComponents();
                    Dialog.show(Messages.get("attachment_added"), Messages.get("attachment_added_sucessfully") 
                            + " " + filename, Messages.get("ok"), null);
                } catch (Exception e) {
                    Log.p("Error addind attachment - " + e.toString(), Log.ERROR);
                    Dialog.show(Messages.get("loading_error"), Messages.get("loading_error_text") + e.getMessage(), Messages.get("ok"), null);
                } finally {
                    form.show();
                }
            }
            public void canceled() {
                form.show();
            }
            public void errorOccured(Exception e) {
                Log.p("Error choosing attachment - " + e.toString(), Log.ERROR);
                Dialog.show(Messages.get("choosing_error"), Messages.get("choosing_error_text") + e.getMessage(), Messages.get("ok"), null);
                form.show();
            }
        });
        fileChooser.show();
    }
    
    private void saveAttachment() {
        FileChooserForm fileChooser = new FileChooserForm(new FileChooserForm.FileChooserCallback() {
            public void choosen(String foldername) {
                try {
                    KeePassMobile.instance().getKeePassMobileIO().saveAttachment(form.getEntry(), foldername);
                    Log.p("Attachment saved successfully", Log.DEBUG);
                    Dialog.show(Messages.get("attachment_saved"), Messages.get("attachment_saved_sucessfully") 
                            + " " + foldername + form.getEntry().getBinaryDescription(), Messages.get("ok"), null);
                } catch (Exception e) {
                    Log.p("Error saving attachment - " + e.toString(), Log.ERROR);
                    Dialog.show(Messages.get("saving_error"), Messages.get("saving_error_text") + e.getMessage(), Messages.get("ok"), null);
                } finally {
                    form.show();
                }
            }
            public void canceled() {
                form.show();
            }
            public void errorOccured(Exception e) {
                Log.p("Error choosing folder to save attachment - " + e.toString(), Log.ERROR);
                Dialog.show(Messages.get("choosing_error"), Messages.get("choosing_error_text") + e.getMessage(), Messages.get("ok"), null);
                form.show();
            }
        });
        fileChooser.setDirectoriesOnly(true);
        fileChooser.show();
    }
}
