package org.sperle.keepass.ui;

import org.sperle.keepass.ui.command.CommandManager;

/**
 * Platform is the interface, the application can use to call code that
 * depends on the platform the application runs on.
 */
public interface Platform {
    /**
     * Returns a platform property (given at startup).
     */
    String getProperty(String key);

    /**
     * Sends the signal to the platform to exit the application.
     */
    void exit();
    
    /**
     * Returns the platform specific command manager.
     */
    CommandManager getCommandManager();
}
