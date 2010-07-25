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

package org.sperle.keepass.ui.settings.j2me;

import java.io.IOException;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import org.sperle.keepass.kdb.SearchOptions;
import org.sperle.keepass.ui.settings.Setting;
import org.sperle.keepass.ui.settings.Settings;
import org.sperle.keepass.ui.util.StringTokenizer;

import com.sun.lwuit.util.Log;

public class RecordStoreSettings implements Settings {
    private static final String RECORD_STORE = "KPM_STORE";

    private RecordStore rs;

    public void open() throws IOException {
        try {
            rs = RecordStore.openRecordStore(RECORD_STORE, true);
        } catch (RecordStoreException e) {
            try {RecordStore.deleteRecordStore(RECORD_STORE);} catch (Exception e1) {} // sometimes this helps creating a new record store next time
            throw new IOException("error opening record store: " + e.getMessage());
        }
    }

    public boolean available() {
        return rs != null;
    }
    
    public boolean exists(String key) {
        return get(key) != null;
    }

    public String get(String key) {
        if(rs == null) return null;
        
        try {
            RecordEnumeration re = rs.enumerateRecords(null, null, false);
            while (re.hasNextElement()) {
                int recordId = re.nextRecordId();
                Setting setting = RecordStoreSetting.valueOf(recordId, rs.getRecord(recordId));
                if(key.equals(setting.getKey())) return setting.getValue();
            }
        } catch (RecordStoreException e) {
            Log.p("Error reading record store value for key [" + key + "] - " + e.getMessage(), Log.ERROR);
        }
        return null;
    }
    
    public boolean getBoolean(String key) {
        String b = get(key);
        if(b == null) return false;
        if("true".equals(b)) return true;
        else return false;
    }
    
    public int getInt(String key, int fallback) {
        String i = get(key);
        if(i == null) return fallback;
        try {
            return Integer.parseInt(i);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
    
    public void set(String key, String value) throws IOException {
        if(rs == null) throw new IllegalStateException("record store not available");
        
        RecordStoreSetting setting = new RecordStoreSetting(key, value);
        try {
            int recordId = getId(key);
            if(recordId != -1) {
                rs.setRecord(recordId, setting.getRecordData(), 0, setting.getRecordData().length);
            } else {
                rs.addRecord(setting.getRecordData(), 0, setting.getRecordData().length);
            }
        } catch (RecordStoreException e) {
            throw new IOException("error writing record to store: " + e.getMessage());
        }
    }
    
    public void setBoolean(String key, boolean value) throws IOException {
        if(value) set(key, "true");
        else set(key, "false");
    }

    public void setInt(String key, int value) throws IOException {
        set(key, ""+value);
    }
    
    public SearchOptions getSearchOptions() {
        String options = get(Settings.SEARCH_OPTIONS);
        if(options == null || options.length() == 0) return null;
        
        SearchOptions so = new SearchOptions();
        try {
            StringTokenizer tokenizer = new StringTokenizer(options, Settings.DELIMITER);
            so.searchResultsMax = Integer.parseInt(tokenizer.nextToken());
            so.searchBackupFolder = "true".equalsIgnoreCase(tokenizer.nextToken());
            so.searchUsername = "true".equalsIgnoreCase(tokenizer.nextToken());
            so.searchTitle = "true".equalsIgnoreCase(tokenizer.nextToken());
            so.searchUrl = "true".equalsIgnoreCase(tokenizer.nextToken());
            so.searchNotes = "true".equalsIgnoreCase(tokenizer.nextToken());
            so.searchBinaryDescription = "true".equalsIgnoreCase(tokenizer.nextToken());
        } catch (Exception e) {
            Log.p("Error parsing saved search option setting [" + options + "] - " + e.getMessage(), Log.ERROR);
        }
        return so;
    }

    public void setSearchOptions(SearchOptions searchOptions) throws IOException {
        StringBuffer buf = new StringBuffer();
        buf.append(searchOptions.searchResultsMax).append(Settings.DELIMITER);
        buf.append(searchOptions.searchBackupFolder).append(Settings.DELIMITER);
        buf.append(searchOptions.searchUsername).append(Settings.DELIMITER);
        buf.append(searchOptions.searchTitle).append(Settings.DELIMITER);
        buf.append(searchOptions.searchUrl).append(Settings.DELIMITER);
        buf.append(searchOptions.searchNotes).append(Settings.DELIMITER);
        buf.append(searchOptions.searchBinaryDescription);
        set(Settings.SEARCH_OPTIONS, buf.toString());
    }
    
    public void delete(String key) throws IOException {
        if(rs == null) throw new IllegalStateException("record store not available");
        
        try {
            int recordId = getId(key);
            if(recordId != -1) {
                rs.deleteRecord(recordId);
            }
        } catch (RecordStoreException e) {
            throw new IOException("error deleting record from store: " + e.getMessage());
        }
    }
    
    public void close() {
        try {
            if(rs != null) rs.closeRecordStore();
        } catch (RecordStoreException e) {
            Log.p("Error closing record store - " + e.getMessage(), Log.ERROR);
        }
    }
    
    private int getId(String key) {
        try {
            RecordEnumeration re = rs.enumerateRecords(null, null, false);
            while (re.hasNextElement()) {
                int recordId = re.nextRecordId();
                Setting setting = RecordStoreSetting.valueOf(recordId, rs.getRecord(recordId));
                if(key.equals(setting.getKey())) return recordId;
            }
        } catch (RecordStoreException e) {
            Log.p("Error reading record store - " + e.getMessage(), Log.ERROR);
        }
        return -1;
    }
}
