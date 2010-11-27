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

package org.sperle.keepass.ui.command;

import org.sperle.keepass.ui.edit.EntryForm;
import org.sperle.keepass.ui.edit.EntryFormCommands;
import org.sperle.keepass.ui.edit.GroupForm;
import org.sperle.keepass.ui.form.EditDBForm;
import org.sperle.keepass.ui.form.EditDBFormCommands;
import org.sperle.keepass.ui.form.PreferencesForm;
import org.sperle.keepass.ui.form.PreferencesFormCommands;
import org.sperle.keepass.ui.form.StatisticsForm;
import org.sperle.keepass.ui.menu.MainMenuForm;
import org.sperle.keepass.ui.menu.MainMenuFormCommands;
import org.sperle.keepass.ui.passgen.PassgenForm;
import org.sperle.keepass.ui.passgen.PassgenFormCommands;
import org.sperle.keepass.ui.search.SearchForm;
import org.sperle.keepass.ui.search.SearchOptionsForm;
import org.sperle.keepass.ui.source.create.CreateDatabaseForm;
import org.sperle.keepass.ui.source.file.FileChooserForm;
import org.sperle.keepass.ui.source.file.MasterPasswordForm;
import org.sperle.keepass.ui.tree.TreeForm;
import org.sperle.keepass.ui.tree.TreeFormCommands;

import com.sun.lwuit.Form;
import com.sun.lwuit.plaf.UIManager;

/**
 * The default command manager simply adds the commands of a form in the order
 * as they are given to the command manager.
 */
public class DefaultCommandManager implements CommandManager {

    public void init() {
        // reverse menu entries (to follow the Nokia style)
        UIManager.getInstance().getLookAndFeel().setReverseSoftButtons(true);
    }

    /**
     * TODO: find a better solution, so that the platform can decide which
     * FormCommand class to choose for a given form.
     */
    public FormCommands getCommands(Form form) {
        if (form instanceof EntryForm) {
            return new EntryFormCommands((EntryForm) form);
        } else if (form instanceof EditDBForm) {
            return new EditDBFormCommands((EditDBForm) form);
        } else if (form instanceof PreferencesForm) {
            return new PreferencesFormCommands((PreferencesForm) form);
        } else if (form instanceof StatisticsForm) {
            return new BackHelpFormCommands((KeePassMobileCommand) form.getBackCommand(), "stats_help");
        } else if (form instanceof MainMenuForm) {
            return new MainMenuFormCommands((MainMenuForm) form);
        } else if (form instanceof PassgenForm) {
            return new PassgenFormCommands((PassgenForm)form);
        } else if (form instanceof SearchForm) {
            return new SearchForm.FormCommands((SearchForm) form);
        } else if (form instanceof SearchOptionsForm) {
            return new BackHelpFormCommands((KeePassMobileCommand) form.getBackCommand(), "searchoptions_help");
        } else if (form instanceof CreateDatabaseForm) {
            return new CreateDatabaseForm.FormCommands((CreateDatabaseForm) form);
        } else if (form instanceof FileChooserForm) {
            return new FileChooserForm.FormCommands((FileChooserForm) form);
        } else if (form instanceof MasterPasswordForm) {
            return new MasterPasswordForm.FormCommands((MasterPasswordForm) form);
        } else if (form instanceof GroupForm) {
            return new GroupForm.FormCommands((GroupForm) form);
        } else if (form instanceof TreeForm) {
            return new TreeFormCommands((TreeForm) form);
        }
        throw new IllegalStateException("no commands for form " + form.getClass().getName() + " configured");
    }

    public void updateCommands(Form form, FormCommands commands) {
        form.removeAllCommands();

        KeePassMobileCommand[] cmds = commands.getCommands();
        KeePassMobileCommand defCmd = commands.getDefaultCommand();

        int j = 0;
        for (int i = 0; i < cmds.length; i++) {
            if (cmds[i] != null && cmds[i].isEnabled() && cmds[i] != defCmd) {
                form.addCommand(cmds[i], j++);
            }
        }
        if (defCmd != null)
            form.addCommand(defCmd, j);
    }
}
