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
import java.io.InputStream;

import org.sperle.keepass.KeePassMobileIOFactory;
import org.sperle.keepass.io.IOManager;
import org.sperle.keepass.monitor.ProgressMonitor;
import org.sperle.keepass.ui.KeePassMobile;

public class MidletKeePassMobileIOFactory extends KeePassMobileIOFactory {
    protected IOManager createIOManager() {
        return new TestIOManager();
    }
    
    public static class TestIOManager implements IOManager {

        public boolean exists(String name) throws IOException {
            throw new IllegalStateException("MidletKeePassMobileIOFactory is only for loading kdb files");
        }

        public byte[] loadBinary(String name, ProgressMonitor pm) throws IOException {
            InputStream is = null;
            byte buf[] = null;
            try {
                is = KeePassMobile.class.getResourceAsStream(name);
                buf = new byte[is.available()];
                int read = is.read(buf);
                if(read != buf.length) {
                    throw new IOException("Could not read whole file [" + name + "]!");
                }
            } finally {
                try {if(is != null) is.close();} catch (IOException e) {}
            }
            return buf;
        }
        
        public void saveBinary(String filename, byte[] binary, ProgressMonitor pm) throws IOException {
            throw new IllegalStateException("MidletKeePassMobileIOFactory is only for loading kdb files");
        }
        
        public boolean equals(String filename1, String filename2) throws IOException {
            throw new IllegalStateException("MidletKeePassMobileIOFactory is only for loading kdb files");
        }

        public byte[] generateHash(String filename, int packetSize) throws IOException {
            throw new IllegalStateException("MidletKeePassMobileIOFactory is only for loading kdb files");
        }

        public long getFileSize(String filename) throws IOException {
            throw new IllegalStateException("MidletKeePassMobileIOFactory is only for loading kdb files");
        }

        public void delete(String filename) throws IOException {
            throw new IllegalStateException("MidletKeePassMobileIOFactory is only for loading kdb files");
        }
    }
}
