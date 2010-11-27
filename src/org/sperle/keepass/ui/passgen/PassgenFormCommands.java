package org.sperle.keepass.ui.passgen;

import org.sperle.keepass.ui.command.AbstractFormCommands;
import org.sperle.keepass.ui.command.KeePassMobileCommand;
import org.sperle.keepass.ui.form.Forms;
import org.sperle.keepass.ui.i18n.Messages;

import com.sun.lwuit.events.ActionEvent;

public class PassgenFormCommands extends AbstractFormCommands {
    private final PassgenForm form;

    public PassgenFormCommands(PassgenForm form) {
        this.form = form;
        
        commands = new KeePassMobileCommand[2];
        commands[0] = (KeePassMobileCommand) form.getBackCommand();
        commands[1] = new KeePassMobileCommand(Messages.get("help")) {
            public void actionPerformed(ActionEvent evt) {
                Forms.showHelp(Messages.get("passgen_help"));
            }
        };
        defaultCommand = 0;
    }
}
