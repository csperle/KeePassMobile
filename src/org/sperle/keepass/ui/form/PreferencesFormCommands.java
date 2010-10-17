package org.sperle.keepass.ui.form;

import java.io.IOException;

import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.command.AbstractFormCommands;
import org.sperle.keepass.ui.command.KeePassMobileCommand;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.locale.Locales;
import org.sperle.keepass.ui.settings.Settings;
import org.sperle.keepass.ui.util.SecurityTimer;

import com.sun.lwuit.Dialog;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.util.Log;

public class PreferencesFormCommands extends AbstractFormCommands {
    
    public PreferencesFormCommands(final PreferencesForm form) {
        commands = new KeePassMobileCommand[3];
        commands[0] = (KeePassMobileCommand) form.getBackCommand();
        commands[1] = new KeePassMobileCommand(Messages.get("help")) {
            public void actionPerformed(ActionEvent evt) {
                Forms.showHelp(Messages.get("preferences_help"));
            }
        };
        commands[2] = new KeePassMobileCommand(Messages.get("save")) {
            public void actionPerformed(ActionEvent ev) {
                try {
                    if(KeePassMobile.instance().getSettings().available()) {
                        if(form.getUiBox() != null) {
                            KeePassMobile.instance().getSettings().setBoolean(Settings.UI_FAST, form.getUiBox().getSelectedIndex() == 1);
                        }
//                        midlet.getSettings().setBoolean(Settings.TOUCH_DEVICE, touchBox.getSelectedIndex() == 0);
                        KeePassMobile.instance().getSettings().setBoolean(Settings.SHOW_BACKUP, form.getBackupBox().getSelectedIndex() == 0);
                        KeePassMobile.instance().getSettings().setBoolean(Settings.QUICK_VIEW, form.getQuickBox().getSelectedIndex() == 0);
                        KeePassMobile.instance().getSettings().set(Settings.LANGUAGE, Messages.SUPPORTED_LANGS[form.getLangBox().getSelectedIndex()]);
                        KeePassMobile.instance().getSettings().set(Settings.DATE_FORMAT, Locales.SUPPORTED_DATE_FORMATS[form.getDfBox().getSelectedIndex()]);
                        KeePassMobile.instance().getSettings().set(Settings.SECURITY_TIMEOUT, getTimeoutMillis(form.getTimeoutBox().getSelectedIndex()));
                        KeePassMobile.instance().getSettings().setInt(Settings.FONT_SIZE, form.getFontBox().getSelectedIndex());
                        
                        Dialog.show(Messages.get("notice"), Messages.get("preferences_saved"), Messages.get("ok"), null);
                        Log.p("User preferences saved", Log.DEBUG);
                    }
                } catch (IOException e) {
                    Log.p("Could not write preferences - " + e.toString(), Log.ERROR);
                } finally {
                    form.previousForm.show();
                }
            }
        };
        defaultCommand = 0;
    }
    
    private String getTimeoutMillis(int selectedIndex) {
        if(selectedIndex == 0) return "-1";
        else return ""+SecurityTimer.SUPPORTED_TIMEOUT_MINUTES[selectedIndex]*60*1000;
    }

}
