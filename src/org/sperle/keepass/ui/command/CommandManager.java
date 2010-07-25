package org.sperle.keepass.ui.command;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;

/**
 * A command manager is responsible to add the commands of a form in the order
 * that fits best to the underlying platform. A command manager therefore
 * implements only a specific algorithm and has no state!
 */
public interface CommandManager {

    /**
     * Is called on application startup.
     */
    void init();

    /**
     * Implements the algorithm to order (and add) the commands of a form and sets the
     * default command (that is shown on the second soft button).
     */
    void addCommands(Form form, Command[] commands, Command defaultCommand);
}
