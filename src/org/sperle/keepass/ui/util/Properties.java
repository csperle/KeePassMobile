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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;

// TODO write test for this class
public class Properties {
    private Hashtable properties = new Hashtable();

    public static Properties load(String fileName) throws IOException {
        String file = loadPropertiesFile(fileName);
        if(file.length() == 0) return new Properties();
        else return parsePropertiesFile(file);
    }
    
    private static String loadPropertiesFile(String fileName) throws IOException {
        InputStream is = null;
        InputStreamReader reader = null;
        
        StringBuffer file = new StringBuffer();
        try {
            is = Properties.class.getResourceAsStream(fileName);
            if(is == null) {
                throw new IOException("file not found");
            }
            
            reader = new InputStreamReader(is);
            char[] buf = new char[1024];
            int read = -1;
            while ((read = reader.read(buf)) > -1) {
                file.append(buf, 0, read);
            }
        } finally {
            try {if(reader != null) reader.close();} catch (IOException e) {}
            try {if(is != null) is.close();} catch (IOException e) {}
        }
        return file.toString();
    }
    
    private static Properties parsePropertiesFile(String file) {
        Properties props = new Properties();
        String[] lines = Strings.split(file, '\n');
        for (int i = 0; i < lines.length; i++) {
            String[] line = Strings.split(lines[i], '=');
            if (line.length == 1) {
                props.setProperty(line[0], "");
            } else {
                props.setProperty(line[0], line[1]);
            }
        }
        return props;
    }
    
    public void setProperty(String key, String val) {
        properties.put(key, val);
    }

    public String getProperty(String key) {
        return (String) properties.get(key);
    }

    public int size() {
        return properties.size();
    }

    public String[] keys() {
        String[] keys = new String[properties.size()];
        int i = 0;
        Enumeration e = properties.keys();
        while(e.hasMoreElements()) {
            keys[i++] = (String) e.nextElement();
        }
        return keys;
    }
}
