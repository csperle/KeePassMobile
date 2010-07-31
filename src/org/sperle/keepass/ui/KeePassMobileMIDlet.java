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

package org.sperle.keepass.ui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.sun.lwuit.Display;
import com.sun.lwuit.util.Log;

/**
 * The MIDlet to start KeePassMobile on the J2ME platform. It implements the Platform
 * interface in a J2ME compatible way.
 */
public class KeePassMobileMIDlet extends MIDlet implements Platform {

    private Application app;
    
    protected void startApp() throws MIDletStateChangeException {
        if(app == null) {
            app = new KeePassMobile(this);
            
            printPlatformInfo();
            
            // check & init platform
            if (isAndroid()) {
                initAndroidPlatform();
            }
            if (isBlackberry()) {
                initBlackBerryDevice();
            }
            
            // init LWUIT
            Display.init(this);
            Log.p("LWUIT initialized", Log.DEBUG);
            
            Log.p("Verifying J2ME implementation...", Log.DEBUG);
            if(!verifyJ2ME()) {
                Log.p("The J2ME implementation of this mobile phone does NOT meet the requirements to run " +
                                "KeePassMobile! Quit...", Log.ERROR);
                showFatalErrorAndQuit();
            }
            
            app.startUp();
        } else {
            app.resume();
        }
    }
    
    protected void pauseApp() {
        app.pause();
    }

    /**
     * Called from the mobile phone.
     */
    protected void destroyApp(boolean unconditional) {
        app.shutdown();
        this.exit();
    }
    
    /**
     * Creates a KeePassMobileFactory for the J2ME platform.
     */
    public KeePassMobileFactory getKeePassMobileFactory() {
        return new KeePassMobileFactoryJ2ME();
    }
    
    /**
     * Returns a JAD property.
     */
    public String getProperty(String key) {
        return getAppProperty(key);
    }

    /**
     * Called from the application.
     */
    public void exit() {
        this.notifyDestroyed();
    }
    
    private void printPlatformInfo() {
        Log.p("Mobile platform: " + System.getProperty("microedition.platform"), Log.DEBUG);
        Log.p("Microedition Configuration: " + System.getProperty("microedition.configuration"), Log.DEBUG);
        Log.p("Microedition Profile: " + System.getProperty("microedition.profiles"), Log.DEBUG);
        Log.p("Platform Encoding: " + System.getProperty("microedition.encoding"), Log.DEBUG);
    }
    
    private boolean verifyJ2ME() {
        String fcVersion = System.getProperty("microedition.io.file.FileConnection.version" );
        if(fcVersion != null) {
            Log.p("FileConnection API V" + fcVersion + " available", Log.DEBUG);
            return true;
        } else {
            Log.p("FileConnection API not available!", Log.ERROR);
            return false;
        }
    }
    
    private boolean isAndroid() {
        try {
            Class.forName("android.app.Activity");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private void initAndroidPlatform() {
        Log.p("Running on a Android device!", Log.DEBUG);
        Log.p("  - OS name: " + System.getProperty("os.name"), Log.DEBUG);
        Log.p("  - OS architecture: " + System.getProperty("os.arch"), Log.DEBUG);
        Log.p("  - OS version: " + System.getProperty("os.version"), Log.DEBUG);
        Log.p("  - Java vendor: " + System.getProperty("java.vendor"), Log.DEBUG);
        Log.p("  - Java version: " + System.getProperty("java.version"), Log.DEBUG);
        Log.p("  * Attention: You are using the standard version of KeePassMobile. " +
                "Though, this version works for your Android device it feels not like a Android application. " +
                "Please use the special Android version of KeePassMobile for a better user experience.", Log.DEBUG);
    }
    
    private boolean isBlackberry() {
        try {
            Class.forName("net.rim.device.api.system.Application");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
    private void initBlackBerryDevice() {
        Log.p("Running on a BlackBerry device!", Log.DEBUG);
        Log.p("  * Attention: You are using the standard version of KeePassMobile. " +
              "Though, this version works for your BlackBerry it feels not like a BlackBerry application. " +
              "Please use the special BlackBerry version of KeePassMobile for a better user experience.", Log.DEBUG);
    }
    
    public void showFatalErrorAndQuit() {
        Alert alert = new Alert("Fatal Error!", Log.getLogContent(), null, AlertType.ERROR);
        alert.setTimeout(Alert.FOREVER);
        javax.microedition.lcdui.Display.getDisplay(this).setCurrent(alert);
        alert.setCommandListener(new CommandListener() {
            public void commandAction(javax.microedition.lcdui.Command command, Displayable displayable) {
                synchronized (this){
                    this.notify();
                }
            }
        });
        try {
            synchronized (this){
                this.wait();
            }
        } catch (InterruptedException e) {
        }
        this.destroyApp(true);
    }
}
