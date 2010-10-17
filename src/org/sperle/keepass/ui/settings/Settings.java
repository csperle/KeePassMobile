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

package org.sperle.keepass.ui.settings;

import java.io.IOException;

import org.sperle.keepass.kdb.SearchOptions;

public interface Settings {
    public static final String DELIMITER = "|";
    public static final String LAST_FILE = "KPM_LAST_FILE";
    public static final String LAST_FOLDER = "KPM_LAST_FOLDER";
    public static final String LANGUAGE  = "KPM_LANGUAGE";
    public static final String DATE_FORMAT  = "KPM_DATE_FORMAT";
    public static final String SECURITY_TIMEOUT = "KPM_SECURITY_TIMEOUT";
    public static final String UI_FAST = "KPM_UI_FAST";
    public static final String SHOW_BACKUP = "KPM_SHOW_BACKUP";
    public static final String QUICK_VIEW = "KPM_QUICK_VIEW";
    public static final String SEARCH_OPTIONS = "KPM_SEARCH_OPTIONS";
    public static final String FONT_SIZE = "KPM_FONT_SIZE";
    public static final String TOUCH_DEVICE = "KPM_TOUCH_DEVICE";
    
    void open() throws IOException;
    
    boolean available();
    
    boolean exists(String key);
    
    String get(String key);
    
    boolean getBoolean(String key);
    
    int getInt(String key, int fallback);
    
    void set(String key, String value) throws IOException;
    
    void setBoolean(String key, boolean value) throws IOException;
    
    void setInt(String key, int value) throws IOException;
    
    void setSearchOptions(SearchOptions searchOptions) throws IOException;

    SearchOptions getSearchOptions();
    
    void delete(String key) throws IOException;
    
    void close();
}
