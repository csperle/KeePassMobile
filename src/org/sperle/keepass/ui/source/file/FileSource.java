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

package org.sperle.keepass.ui.source.file;

import java.io.IOException;

import org.sperle.keepass.kdb.KeePassDatabase;
import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.io.Loader;
import org.sperle.keepass.ui.menu.MenuItem;
import org.sperle.keepass.ui.settings.Settings;
import org.sperle.keepass.ui.tree.TreeForm;

import com.sun.lwuit.Dialog;
import com.sun.lwuit.util.Log;

public class FileSource implements MenuItem {
    /** The file name ending of a keepass database. */
    private static final String KDB_FILE_ENDING = ".kdb";
    //private static final String KEY_FILE_ENDING = ".key";
    
    protected String filename;
    
    private String password;
    
    public void choosen() {
        FileChooserForm fileChooser = new FileChooserForm(new FileChooserForm.FileChooserCallback() {
            public void choosen(String filename) {
                dbFileChoosen(filename);
            }
            public void canceled() {
                fileChoosingCanceled();
            }
            public void errorOccured(Exception e) {
                Log.p("Error choosing database file - " + e.toString(), Log.ERROR);
                Dialog.show(Messages.get("choosing_error"), Messages.get("choosing_error_text") + e.getMessage(), Messages.get("ok"), null);
                KeePassMobile.instance().showMainMenu();
            }
        });
        if(KeePassMobile.instance().getSettings().exists(Settings.LAST_FILE)) {
            fileChooser.setDirectory(KeePassMobile.instance().getSettings().get(Settings.LAST_FOLDER));
        }
        fileChooser.setFileEndingFilter(KDB_FILE_ENDING);
        fileChooser.show();
    }

    protected void fileChoosingCanceled() {
        password = null;
        Log.p("Database file choosing canceled", Log.DEBUG);
        KeePassMobile.instance().showMainMenu();
    }

    protected void dbFileChoosen(String filename) {
        this.filename = filename;
        Log.p("Database file [" + filename + "] choosen");
        
        MasterPasswordForm passwdForm = new MasterPasswordForm(this);
        passwdForm.show();
    }
    
    protected void keyFileChoosen(String filename) {
        String p = null;
        if(password.length() > 0) {
            p = password;
        }
        this.password = null;
        Log.p("Key file choosen");
        decrypt(p, filename);
    }
    
    protected void passwordEnteringCanceled() {
        Log.p("Password entering canceled", Log.DEBUG);
        KeePassMobile.instance().showMainMenu();
    }
    
    protected void openKeyFile(String password) {
        this.password = password;
        
        FileChooserForm fileChooser = new FileChooserForm(new FileChooserForm.FileChooserCallback() {
            public void choosen(String filename) {
                keyFileChoosen(filename);
            }
            public void canceled() {
                fileChoosingCanceled();
            }
            public void errorOccured(Exception e) {
                Log.p("Error choosing key file - " + e.toString(), Log.ERROR);
                Dialog.show(Messages.get("choosing_error"), Messages.get("choosing_error_text") + e.getMessage(), Messages.get("ok"), null);
                KeePassMobile.instance().showMainMenu();
            }
        });
        fileChooser.show();
    }
    
    protected void decrypt(String password, String keyfile) {
        Loader loader = new Loader(KeePassMobile.instance().getKeePassMobileIO(), filename, password, keyfile);
        loader.load();
        KeePassDatabase kdb = loader.getKdb();
        
        if(kdb == null) {
            KeePassMobile.instance().showMainMenu();
            return;
        }
        
        try {
            if(KeePassMobile.instance().getSettings().available()) KeePassMobile.instance().getSettings().set(Settings.LAST_FILE, filename);
            if(KeePassMobile.instance().getSettings().available()) KeePassMobile.instance().getSettings().set(Settings.LAST_FOLDER, filename.substring(0, filename.lastIndexOf('/')+1));
        } catch (IOException e) {
            Log.p("Could not write last file settings - " + e.toString(), Log.ERROR);
        }
        
        KeePassMobile.instance().startSecurityTimer(kdb);
        
        TreeForm treeForm = new TreeForm(kdb, null);
        treeForm.show();
    }
    
    public String getKdbName() {
        return filename.substring(filename.lastIndexOf('/') + 1);
    }
    
    public String toString() {
        return Messages.get("open_new");
    }
}
