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

package org.sperle.keepass.ui.i18n;

import java.io.IOException;
import java.util.Hashtable;

import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;

public class Messages {
    public static final String SUPPORTED_LANGS[] = {"da", "de", "en", "es", "he", "it", "nl", "nn", "sv", "ru", "uk", "zh"};
    
    private static Resources langs;
    private static String selectedLang;
    private static String defaultLang;
    
    private transient static Hashtable defaultResourceBundle;
    private transient static Hashtable selectedResourceBundle;
    
    public static void load() {
        try {
            langs = Resources.open("/i18n.res");
        } catch (IOException e) {
            throw new IllegalStateException("can not load ui languages: " + e.getMessage());
        }
    }
    
    public static void setSelectedLang(String lang) {
        Messages.selectedLang = lang;
        selectedResourceBundle = langs.getL10N("localization", lang);
        UIManager.getInstance().setResourceBundle(selectedResourceBundle);
    }
    
    public static String getSelectedLang() {
        return Messages.selectedLang;
    }
    
    public static void setDefaultLang(String defaultLang) {
        Messages.defaultLang = defaultLang;
        defaultResourceBundle = langs.getL10N("localization", defaultLang);
    }
    
    public static String getDefaultLang() {
        return Messages.defaultLang;
    }
    
    public static String get(String key, String defaultMessage) {
        String message = get(key);
        return message.startsWith("!") && message.endsWith("!") ? defaultMessage : message;
    }
    
    public static String get(String key) {
        if(selectedResourceBundle != null) {
            String message = (String)selectedResourceBundle.get(key);
            if(message != null && message.length() > 0) {
                return message;
            }
        }
        if(defaultResourceBundle != null) {
            String message = (String)defaultResourceBundle.get(key);
            if(message != null && message.length() > 0) {
                return message;
            }
        }
        return "!"+key.toUpperCase()+"!";
    }
    
    public static boolean supported(String lang) {
        for (int i = 0; i < SUPPORTED_LANGS.length; i++) {
            if(SUPPORTED_LANGS[i].equals(lang)) {
                return true;
            }
        }
        return false;
    }
}
