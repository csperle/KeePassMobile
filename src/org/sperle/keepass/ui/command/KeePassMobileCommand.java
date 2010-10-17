package org.sperle.keepass.ui.command;

import com.sun.lwuit.Command;
import com.sun.lwuit.Image;

/**
 * A LWUIT Command that can be enabled/disabled.
 */
public class KeePassMobileCommand extends Command {

    private boolean enabled = true;

    /**
     * Creates new Command that is enabled per default with the given name.
     */
    public KeePassMobileCommand(String name) {
        super(name);
    }

    /**
     * Creates new Command that is enabled per default with the given name and
     * icon.
     */
    public KeePassMobileCommand(String name, Image icon) {
        super(name, icon);
    }

    /**
     * Set true, if command should be enabled.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns true, if command is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }
}
