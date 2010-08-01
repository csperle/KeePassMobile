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

package org.sperle.keepass.ui.locale;

import java.io.IOException;

import org.sperle.keepass.ui.util.Properties;

public class Locales {
    public static final String DATE_FORMAT_KEY = "date-format";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SUPPORTED_DATE_FORMATS[] = {"yyyy-MM-dd", "MM/dd/yyyy", "dd.MM.yyyy", "dd/MM/yyyy", "dd-MM-yyyy"};
    
    private static Properties locales;
    private static String dateFormat;
    
    public static void load() {
        try {
            locales = Properties.load("/locales.properties");
        } catch (IOException e) {
            throw new IllegalStateException("locales missing");
        }
    }

    public static String getMobileLocale() {
        return System.getProperty("microedition.locale");
    }
    
    public static String getMobileLanguge() {
        String lang = getMobileLocale();
        if(lang.indexOf('-') > -1) {
            return lang.substring(0, lang.indexOf('-')).toLowerCase();
        } else if(lang.indexOf('_') > -1) {
            return lang.substring(0, lang.indexOf('_')).toLowerCase();
        } else {
            return lang.toLowerCase();
        }
    }
    
    public static String getMobileCountry() {
        String locale = getMobileLocale();
        if(locale.indexOf('-') > -1) {
            return locale.substring(locale.indexOf('-') + 1, locale.length()).toUpperCase();
        } else if(locale.indexOf('_') > -1) {
            return locale.substring(locale.indexOf('_') + 1, locale.length()).toUpperCase();
        } else {
            return null;
        }
    }

    public static String getDateFormat(String countryCode) {
        return locales.getProperty(countryCode + "_" + DATE_FORMAT_KEY);
    }
    
    public static void setDateFormat(String dateFormat) {
        Locales.dateFormat = dateFormat;
    }

    public static String getDateFormat() {
        return dateFormat;
    }
}
