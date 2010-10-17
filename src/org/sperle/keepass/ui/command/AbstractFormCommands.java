package org.sperle.keepass.ui.command;

/**
 * Abstract form command class, that implements the default form command
 * behavior.
 */
public abstract class AbstractFormCommands implements FormCommands {

    protected KeePassMobileCommand[] commands;
    protected int defaultCommand;

    public KeePassMobileCommand[] getCommands() {
        return commands;
    }

    public KeePassMobileCommand getDefaultCommand() {
        return commands[defaultCommand];
    }

    /**
     * Default: Update not needed for any command.
     */
    public void update() {
    }
}
