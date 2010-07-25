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

package org.sperle.keepass.ui.source.midlet;

import java.io.IOException;

import org.sperle.keepass.KeePassMobileIO;
import org.sperle.keepass.KeePassMobileIOFactory;
import org.sperle.keepass.crypto.KeePassCryptoException;
import org.sperle.keepass.kdb.KeePassDatabase;
import org.sperle.keepass.kdb.KeePassDatabaseException;
import org.sperle.keepass.monitor.ProgressMonitor;
import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.form.ProgressDialog;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.menu.MenuItem;
import org.sperle.keepass.ui.tree.TreeForm;

import com.sun.lwuit.Dialog;
import com.sun.lwuit.util.Log;

public class MidletSource implements MenuItem, Runnable {
    private static final String TEST_DB = "test.kdb";
    private static final String TEST_PASSWORD = "ÖÄÜöäüß_@!\"§$%&/()[]=*\\n";
    
    private KeePassMobile app;
    
    // used to communicate between EDT and background thread
    private ProgressMonitor pm;
    private ProgressDialog pd;
    private KeePassDatabase kdb;
    private Exception exception;
    
    public MidletSource(KeePassMobile app) {
        this.app = app;
    }

    public void choosen() {
        Log.p("Trying to load and decrypt test database file [test.kdb]...", Log.DEBUG);
        
        kdb = null;
        exception = null;
        pm = new ProgressMonitor();
        pd = new ProgressDialog(pm);
        pd.show();
        
        new Thread(this).start();
        pd.blockUntilTaskFinished();
        pd.dispose();
        
        if(kdb == null) {
            if(exception instanceof IOException) {
                Log.p("Could not load test database file - " + exception.toString(), Log.ERROR);
                Dialog.show("Loading Error", exception.getMessage(), Messages.get("ok"), null);
            } else if(exception instanceof KeePassCryptoException) {
                Log.p("Could not decrypt test database file with specified password - " + exception.toString(), Log.ERROR);
                Dialog.show("Wrong password", exception.getMessage(), Messages.get("ok"), null);
            } else if(exception instanceof KeePassDatabaseException) {
                Log.p("Test database file is corrupt - " + exception.toString(), Log.ERROR);
                Dialog.show("Corrupt file", exception.getMessage(), Messages.get("ok"), null);
            }
            exception = null;
            return;
        } else {
            Log.p("Test database file loaded and decrypted successfully", Log.DEBUG);
        }
        
        TreeForm treeForm = new TreeForm(app, kdb, null);
        kdb = null; // delete references used by background thread, before EDT moves on
        treeForm.show();
    }

    public String toString() {
        return "DEV: Open test.kdb";
    }

    // runs in own thread (not EDT -> no UI code allowed!)
    public void run() {
        KeePassMobileIOFactory factory = new MidletKeePassMobileIOFactory();
        KeePassMobileIO keePassIO = factory.create();
        try {
            kdb = keePassIO.load(TEST_DB, TEST_PASSWORD, null, pm);
        } catch (Exception e) {
            exception = e;
        } finally {
            pm.finish();
        }
    }
}
