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

package org.sperle.keepass.ui.io;

import org.sperle.keepass.KeePassMobileIO;
import org.sperle.keepass.crypto.KeePassCryptoException;
import org.sperle.keepass.kdb.KeePassDatabase;
import org.sperle.keepass.kdb.KeePassDatabaseException;
import org.sperle.keepass.monitor.ProgressMonitor;
import org.sperle.keepass.ui.form.ProgressDialog;
import org.sperle.keepass.ui.i18n.Messages;

import com.sun.lwuit.Dialog;
import com.sun.lwuit.util.Log;

public class Loader implements Runnable {
    private KeePassMobileIO keePassMobileIO;
    
    private ProgressMonitor pm;
    private ProgressDialog pd;
    private KeePassDatabase kdb;
    private Exception exception;
    private String filename;
    private String password;
    private String keyfile;
    
    public Loader(KeePassMobileIO keePassMobileIO, String filename, String password, String keyfile) {
        this.keePassMobileIO = keePassMobileIO;
        this.filename = filename;
        this.password = password;
        this.keyfile = keyfile;
    }

    public void load() {
        Log.p("Trying to load and decrypt database [" + filename + "]...", Log.DEBUG);
        
        pm = new ProgressMonitor();
        pd = new ProgressDialog(pm);
        pd.show();
        
        new Thread(this).start();
        pd.blockUntilTaskFinished();
        pd.dispose();
        
        if(kdb == null) {
            if(pm.isCanceled()) {
                Log.p("User canceled loading/decrypting", Log.INFO);
            } else {
                if(exception != null) {
                    if(exception instanceof KeePassCryptoException) {
                        Log.p("Could not decrypt database file with specified password - " + exception.toString(), Log.ERROR);
                        Dialog.show(Messages.get("wrong_password"), Messages.get("wrong_password_text"), Messages.get("ok"), null);
                    } else if(exception instanceof KeePassDatabaseException) {
                        Log.p("Database file is corrupt - " + exception.toString(), Log.ERROR);
                        Dialog.show(Messages.get("corrupt_file"), Messages.get("corrupt_file_text") + exception.getMessage(), Messages.get("ok"), null);
                    } else {
                        Log.p("Could not load database file - " + exception.toString(), Log.ERROR);
                        Dialog.show(Messages.get("loading_error"), Messages.get("loading_error_text") + exception.getMessage(), Messages.get("ok"), null);
                    }
                } else {
                    Log.p("Could not load database file - unknown error", Log.ERROR);
                    Dialog.show(Messages.get("loading_error"), Messages.get("loading_error_text") + "unknown error", Messages.get("ok"), null);
                }
            }
        } else {
            Log.p("Database file loaded and decrypted successfully", Log.DEBUG);
        }
    }
    
    public KeePassDatabase getKdb() {
        return kdb;
    }
    
    // runs in own thread (not EDT -> no UI code allowed!)
    public void run() {
        try {
            kdb = keePassMobileIO.load(filename, password, keyfile, true, pm); // TODO new setting: usePasswordEncryption
        } catch (Exception e) {
            exception = e;
        } finally {
            pm.finish();
        }
    }
}
