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
import org.sperle.keepass.KeePassMobileIOFactory;
import org.sperle.keepass.crypto.KeePassCryptoException;
import org.sperle.keepass.kdb.KeePassDatabase;
import org.sperle.keepass.kdb.KeePassDatabaseException;
import org.sperle.keepass.monitor.ProgressMonitor;
import org.sperle.keepass.ui.form.ProgressDialog;
import org.sperle.keepass.ui.i18n.Messages;

import com.sun.lwuit.Dialog;
import com.sun.lwuit.util.Log;

public class Saver implements Runnable {
    private String filename;
    
    private ProgressMonitor pm;
    private ProgressDialog pd;
    private KeePassDatabase kdb;
    private Exception exception;
    private boolean success;
    
    public Saver(KeePassDatabase kdb) {
        this.kdb = kdb;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public boolean save() {
        if(filename == null) filename = kdb.getFileName();
        
        Log.p("Trying to save and encrypt database [" + filename + "]...", Log.DEBUG);
        
        success = false;
        pm = new ProgressMonitor();
        pd = new ProgressDialog(pm);
        pd.show();
        
        new Thread(this).start();
        pd.blockUntilTaskFinished();
        pd.dispose();
        
        if(pm.isCanceled()) {
            Log.p("User canceled saving/encrypting", Log.INFO);
        } else if(exception != null) {
            if(exception instanceof KeePassCryptoException) {
                Log.p("Could not encrypt database file - " + exception.toString(), Log.ERROR);
                Dialog.show(Messages.get("encryption_error"), Messages.get("encryption_error_text"), Messages.get("ok"), null);
            } else if(exception instanceof KeePassDatabaseException) {
                Log.p("Database file not supported - " + exception.toString(), Log.ERROR);
                Dialog.show(Messages.get("db_unsupported"), Messages.get("db_unsupported_text") + exception.getMessage(), Messages.get("ok"), null);
            } else {
                Log.p("Could not save database file - " + exception.toString(), Log.ERROR);
                Dialog.show(Messages.get("saving_error"), Messages.get("saving_error_text") + exception.getMessage(), Messages.get("ok"), null);
            }
        } else {
            Log.p("Database file saved and encrypted successfully", Log.DEBUG);
        }
        return success;
    }
    
    // runs in own thread (not EDT -> no UI code allowed!)
    public void run() {
        KeePassMobileIOFactory factory = new KeePassMobileIOFactory();
        KeePassMobileIO keePassIO = factory.create();
        try {
            success = keePassIO.save(kdb, filename, pm);
        } catch (Exception e) {
            exception = e;
        } finally {
            pm.finish();
        }
    }
}
