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

package org.sperle.keepass.ui.source.create;

import java.io.IOException;

import org.sperle.keepass.kdb.KdbGroup;
import org.sperle.keepass.kdb.KeePassDatabase;
import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.menu.MenuItem;
import org.sperle.keepass.ui.source.file.FileChooserForm;
import org.sperle.keepass.ui.tree.TreeForm;

import com.sun.lwuit.Dialog;
import com.sun.lwuit.util.Log;

public class CreateDatabaseSource implements MenuItem {
    
    public void choosen() {
        CreateDatabaseForm createdbForm = new CreateDatabaseForm(this);
        createdbForm.show();
    }
    
    protected void creationCanceled() {
        Log.p("Database creation canceled", Log.DEBUG);
        KeePassMobile.instance().showMainMenu();
    }
    
    protected void openKeyFile(final String dbname, final String password) {
        FileChooserForm fileChooser = new FileChooserForm(new FileChooserForm.FileChooserCallback() {
            public void choosen(String keyfilename) {
                create(dbname, password, keyfilename);
            }
            public void canceled() {
                creationCanceled();
            }
            public void errorOccured(Exception e) {
                Log.p("Error choosing key file - " + e.toString(), Log.ERROR);
                Dialog.show(Messages.get("choosing_error"), Messages.get("choosing_error_text") + e.getMessage(), Messages.get("ok"), null);
                KeePassMobile.instance().showMainMenu();
            }
        });
        fileChooser.show();
    }
    
    protected void create(String dbname, String password, String keyfile) {
        if(dbname == null || dbname.length() == 0) {
            Dialog.show(Messages.get("dbname_empty"), Messages.get("dbname_empty_text"), Messages.get("ok"), null);
            KeePassMobile.instance().showMainMenu();
            return;
        }
        if((password == null || password.length() == 0) && (keyfile == null || keyfile.length() == 0)) {
            Dialog.show(Messages.get("password_empty"), Messages.get("password_empty_text"), Messages.get("ok"), null);
            KeePassMobile.instance().showMainMenu();
            return;
        }
        
        Log.p("Trying to create new database...", Log.DEBUG);
        
        KeePassDatabase kdb = null;
        try {
            kdb = KeePassMobile.instance().getKeePassMobileIO().create(dbname, password, keyfile);
        } catch (IOException e) {
            Log.p("Could not load key file - " + e.toString(), Log.ERROR);
            Dialog.show(Messages.get("keyfile_error"), Messages.get("keyfile_error_text") + e.getMessage(), Messages.get("ok"), null);
            KeePassMobile.instance().showMainMenu();
            return;
        }
        
        Log.p("New database created successfully", Log.DEBUG);
        createDefaultLayout(kdb);
        Log.p("Initialized layout of new database successfully", Log.DEBUG);
        
        KeePassMobile.instance().startSecurityTimer(kdb);
        
        TreeForm treeForm = new TreeForm(kdb, null);
        treeForm.show();
    }

    private void createDefaultLayout(KeePassDatabase kdb) {
        KdbGroup general = kdb.createGroup(null);
        general.setName(Messages.get("dbgroup_general"));
        general.setIconId(48);
        
        KdbGroup internet = kdb.createGroup(general);
        internet.setName(Messages.get("dbgroup_internet"));
        internet.setIconId(01);
        KdbGroup email = kdb.createGroup(general);
        email.setName(Messages.get("dbgroup_email"));
        email.setIconId(19);
        KdbGroup network = kdb.createGroup(general);
        network.setName(Messages.get("dbgroup_network"));
        network.setIconId(03);
        KdbGroup banking = kdb.createGroup(general);
        banking.setName(Messages.get("dbgroup_banking"));
        banking.setIconId(37);
        
        KdbGroup backup = kdb.createGroup(null);
        backup.setName(Messages.get("dbgroup_backup"));
        backup.setIconId(04);
        kdb.setBackupGroup(backup);
    }

    public String toString() {
        return Messages.get("create_db");
    }
}
