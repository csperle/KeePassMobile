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

import com.sun.lwuit.Command;
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

    public void addCommands(Form form, Command[] commands, Command defaultCommand) {
        form.removeAllCommands();
        
        int j = 0;
        for (int i = 0; i < commands.length; i++) {
            if(commands[i] != null && commands[i] != defaultCommand) {
                form.addCommand(commands[i], j++);
            }
        }
        if(defaultCommand != null) form.addCommand(defaultCommand, j);
    }
}
