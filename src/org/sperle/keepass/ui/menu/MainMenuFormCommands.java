package org.sperle.keepass.ui.menu;

import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.command.AbstractFormCommands;
import org.sperle.keepass.ui.command.KeePassMobileCommand;
import org.sperle.keepass.ui.form.AboutForm;
import org.sperle.keepass.ui.form.Forms;
import org.sperle.keepass.ui.form.PreferencesForm;
import org.sperle.keepass.ui.i18n.Messages;

import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.util.Log;

public class MainMenuFormCommands extends AbstractFormCommands {
    
    public MainMenuFormCommands(final MainMenuForm form) {
        commands = new KeePassMobileCommand[5];
        commands[0] = new KeePassMobileCommand(Messages.get("help")) {
            public void actionPerformed(ActionEvent evt) {
                Forms.showHelp(Messages.get("mainmenu_help"));
            }
        };
        commands[1] = new KeePassMobileCommand(Messages.get("preferences")) {
            public void actionPerformed(ActionEvent evt) {
                new PreferencesForm().show();
            }
        };
        commands[2] = new KeePassMobileCommand(Messages.get("show_log")) {
            public void actionPerformed(ActionEvent evt) {
                Forms.showLog(Log.getLogContent());
            }
        };
        commands[3] = new KeePassMobileCommand(Messages.get("about")) {
            public void actionPerformed(ActionEvent evt) {
                new AboutForm().show();
            }
        };
        commands[4] = new KeePassMobileCommand(Messages.get("exit")) {
            public void actionPerformed(ActionEvent evt) {
                KeePassMobile.instance().exit();
            }
        };
        defaultCommand = 4;
    }
}
