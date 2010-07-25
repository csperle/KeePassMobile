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
