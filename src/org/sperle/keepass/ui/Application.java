package org.sperle.keepass.ui;

/**
 * Application is the interface, the platform can send signals to. The
 * application must not contain any platform specific code!
 */
public interface Application {

    void startUp();

    void pause();

    void resume();
    
    void shutdown();
}
