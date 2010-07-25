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

package org.sperle.keepass.ui.util;

import org.sperle.keepass.kdb.KdbDate;
import org.sperle.keepass.kdb.KeePassDatabase;
import org.sperle.keepass.ui.KeePassMobile;

import com.sun.lwuit.Display;
import com.sun.lwuit.util.Log;

/**
 * The security timer closes the database automatically, after a specified time
 * amount passes without any key is pressed.
 */
public class SecurityTimer implements Runnable {
    public static final long DEFAULT_TIMEOUT_MILLIS = 10*60*1000; // 10min
    public static final int[] SUPPORTED_TIMEOUT_MINUTES = {-1, 1, 2, 5, 10, 15, 30, 60};
    
    private KeePassMobile app;
    private KeePassDatabase kdb;
    private long maxTimeout;
    
    private KdbDate lastCheck;
    private boolean keyPressedInTheMeanwhile = false;
    private boolean stoppedFromOutside = false;
    
    public SecurityTimer(KeePassMobile app, KeePassDatabase kdb, long maxTimeout) {
        this.app = app;
        this.kdb = kdb;
        this.maxTimeout = maxTimeout;
    }

    public void run() {
        lastCheck = KdbDate.now();
        while (!stoppedFromOutside) {
            if (KdbDate.now().sub(lastCheck) >= maxTimeout) {
                if(!keyPressedInTheMeanwhile) break;
                else {
                    lastCheck = KdbDate.now();
                    keyPressedInTheMeanwhile = false;
                }
            }
            try {
                Thread.sleep(15000); // 15sec
            } catch (InterruptedException e) {
            }
        }
        if(!stoppedFromOutside) { // stopped, because security timeout was reached
            kdb.close();
            Log.p("Security timeout: database closed!", Log.DEBUG);
            Display.getInstance().callSerially(new Runnable() {
                public void run() {
                    app.showMainMenu();
                }
            });
        }
        app.releaseSecurityTimer();
    }

    public void keyPressed() {
        this.keyPressedInTheMeanwhile = true;
    }
    
    public void stopTimer() {
        this.stoppedFromOutside = true;
    }
}
