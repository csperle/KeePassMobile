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

import org.sperle.keepass.KeePassMobileIO;
import org.sperle.keepass.crypto.CryptoManager;
import org.sperle.keepass.crypto.bc.AESCipher;
import org.sperle.keepass.crypto.bc.BcRandom;
import org.sperle.keepass.crypto.bc.SHA256Hash;
import org.sperle.keepass.io.IOManager;
import org.sperle.keepass.io.j2me.J2meIOManager;
import org.sperle.keepass.kdb.KeePassDatabaseCryptoAlgorithm;
import org.sperle.keepass.kdb.KeePassDatabaseManager;
import org.sperle.keepass.kdb.v1.KeePassDatabaseAESCryptoAlgorithmV1;
import org.sperle.keepass.kdb.v1.KeePassDatabaseManagerV1;
import org.sperle.keepass.rand.Random;
import org.sperle.keepass.ui.command.CommandManager;
import org.sperle.keepass.ui.command.DefaultCommandManager;

/**
 * Factory class to construct the KeePassMobile components for the J2ME platform.
 */
public class KeePassMobileFactoryJ2ME implements KeePassMobileFactory {

    public CommandManager createCommandManager() {
        return new DefaultCommandManager();
    }
    
    /**
     * Construct a fully configured KeePassIO instance for the J2ME platform.
     */
    public KeePassMobileIO createKeePassMobileIO() {
	return new KeePassMobileIO(createKeePassDatabaseManager());
    }

    protected KeePassDatabaseManager createKeePassDatabaseManager() {
        CryptoManager cm = createCryptoManager();
	KeePassDatabaseManagerV1 kdbm = new KeePassDatabaseManagerV1(createIOManager(), cm, createRandom());
	KeePassDatabaseCryptoAlgorithm[] cryptoAlgorithms = getKeePassDatabaseCryptoAlgorithms(cm);
	for (int i = 0; i < cryptoAlgorithms.length; i++) {
    	    kdbm.registerCryptoAlgorithm(cryptoAlgorithms[i]);
	}
	return kdbm;
    }

    protected IOManager createIOManager() {
	return new J2meIOManager();
    }
    
    public Random createRandom() {
        return new BcRandom();
    }
    
    protected CryptoManager createCryptoManager() {
	CryptoManager cm = new CryptoManager();
	cm.addCipher(new AESCipher());
	cm.addHash(new SHA256Hash());
	return cm;
    }
    
    protected KeePassDatabaseCryptoAlgorithm[] getKeePassDatabaseCryptoAlgorithms(CryptoManager cm) {
        KeePassDatabaseCryptoAlgorithm[] algorithms = new KeePassDatabaseCryptoAlgorithm[1];
        algorithms[0] = new KeePassDatabaseAESCryptoAlgorithmV1(cm);
	return algorithms;
    }
}
