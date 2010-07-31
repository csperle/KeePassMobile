package org.sperle.keepass.ui;


/**
 * Platform is the interface, the application can use to call code or construct components that
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
     * Returns a KeePassMobileFactory that is able to construct platform specific KeePassMobile components.
     */
    KeePassMobileFactory getKeePassMobileFactory();
}
