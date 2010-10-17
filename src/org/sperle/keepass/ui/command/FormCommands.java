package org.sperle.keepass.ui.command;

/**
 * This class configures the available commands for a form and decides depending
 * on the ui state of the form, which commands are enabled/disabled.
 */
public interface FormCommands {
    /**
     * Returns the commands (and order!) that are available for this form.
     * Called during form initialization.
     */
    KeePassMobileCommand[] getCommands();

    /**
     * Returns the (fully configured) default command of the form. Called during
     * form initialization.
     */
    KeePassMobileCommand getDefaultCommand();

    /**
     * Update commands: enable/disable commands depending on ui state (form).
     * Called on form's demand.
     */
    void update();
}
