package org.sperle.keepass.ui;

import org.sperle.keepass.KeePassMobileIO;
import org.sperle.keepass.rand.Random;
import org.sperle.keepass.ui.command.CommandManager;

/**
 * The KeePassMobileFactory should be able to construct the (platform dependent) components of the
 * KeePassMobile application.
 */
public interface KeePassMobileFactory {
    /**
     * Creates and returns a fully configured instance of the KeePassMobileIO facade.
     */
    KeePassMobileIO createKeePassMobileIO();
    
    /**
     * Creates and returns a command manager instance.
     */
    CommandManager createCommandManager();
    
    /**
     * Creates and returns a Random instance.
     */
    Random createRandom();
}
