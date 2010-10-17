package org.sperle.keepass.ui.command;

import org.sperle.keepass.ui.form.Forms;
import org.sperle.keepass.ui.i18n.Messages;

import com.sun.lwuit.events.ActionEvent;

/**
 * Default form commands for back and help command. Default command is back
 * command.
 */
public class BackHelpFormCommands extends AbstractFormCommands {

    /**
     * Creates a new instance with the given back command and help message key.
     */
    public BackHelpFormCommands(final KeePassMobileCommand backCommand, final String helpMessageKey) {
        commands = new KeePassMobileCommand[2];
        commands[0] = backCommand;
        commands[1] = new KeePassMobileCommand(Messages.get("help")) {
            public void actionPerformed(ActionEvent evt) {
                Forms.showHelp(Messages.get(helpMessageKey));
            }
        };
        defaultCommand = 0;
    }
}
