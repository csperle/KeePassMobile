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

import org.sperle.keepass.ui.settings.Setting;

public class RecordStoreSetting extends Setting {
    private final static String SEPERATOR = "|";
    
    private final int recordId;
    
    public RecordStoreSetting(String key, String value) {
        this(-1, key, value);
    }
    
    public RecordStoreSetting(int recordId, String key, String value) {
        super(key, value);
        this.recordId = recordId;
    }

    public int getRecordId() {
        return recordId;
    }
    
    public static Setting valueOf(int recordId, byte[] record) {
        String rec = new String(record);
        int sep = rec.indexOf(SEPERATOR);
        return new RecordStoreSetting(recordId, rec.substring(0, sep), rec.substring(sep + 1, rec.length()));
    }
    
    public byte[] getRecordData() {
        String s = getKey() + "|" + getValue();
        return s.getBytes();
    }
}
