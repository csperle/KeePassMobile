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

import org.sperle.keepass.kdb.KdbDate;
import org.sperle.keepass.ui.locale.Locales;

/**
 * Own, very simple implementation of Date formating/parsing (String -> Date, Date -> String).
 * Does J2ME really does not provide this? *UPDATE*: JSR238 seems to implement this, but it is not
 * available on all phones. So, keep own implementation for now.
 */
public class DateFormatter {
    private DateFormat format;
    
    /**
     * Uses date format from lang.properties resource files.
     */
    public DateFormatter() throws ParseException {
        this(Locales.getDateFormat());
    }
    
    public DateFormatter(String format) throws ParseException {
        this.format = new DateFormat(format);
    }

    public String format(KdbDate date) throws ParseException {
        StringBuffer formatedDate = new StringBuffer();
        for (int i = 0; i < 3; i++) {
            if(format.dateComponents[i] == DateFormat.YEAR) {
                formatedDate.append(date.getYear());
            }
            if(format.dateComponents[i] == DateFormat.MONTH) {
                int month = date.getMonth();
                String fillMonth = month < 10 && format.monthFormat.length() == 2 ? "0" : "";
                formatedDate.append(fillMonth+month);
            }
            if(format.dateComponents[i] == DateFormat.DAY) {
                int day = date.getDay();
                String fillDay = day < 10 && format.dayFormat.length() == 2 ? "0" : "";
                formatedDate.append(fillDay+day);
            }
            if(i < 2) formatedDate.append(format.seperator);
        }
        return formatedDate.toString();
    }
    
    public KdbDate parse(String text) throws ParseException {
        int year = -1;
        int month = -1;
        int day = -1;
        
        if(text == null) {
            throw new NullPointerException("text null");
        }
        if(text.length() < format.formatLength - 2) { // -2: day can be a single digit + month can be a single digit
            throw new ParseException("Date [" + text + "] is not valid in format [" + format + "]", "date_format_invalid");
        }
        
        int sep1 = text.indexOf(format.seperator);
        int sep2 = text.indexOf(format.seperator, sep1 + 1);
        if(sep1 < 0 || sep2 < 0) {
            throw new ParseException("Date [" + text + "] is not valid in format [" + format + "]", "date_format_invalid");
        }
        
        try {
            int dateComponent[] = new int[3];
            dateComponent[0] = Integer.parseInt(text.substring(0, sep1));
            dateComponent[1] = Integer.parseInt(text.substring(sep1 + 1, sep2));
            dateComponent[2] = Integer.parseInt(text.substring(sep2 + 1, text.length()));
            
            year = dateComponent[format.getIndexOf(DateFormat.YEAR)];
            month = dateComponent[format.getIndexOf(DateFormat.MONTH)];
            day = dateComponent[format.getIndexOf(DateFormat.DAY)];
        } catch (NumberFormatException e) {
            throw new ParseException("Date [" + text + "] is not valid in format [" + format + "]", "date_format_invalid");
        }
        
        KdbDate date = new KdbDate(year, month, day, 23, 59, 59);
        if(!date.isValid()) {
            throw new ParseException("Date [" + text + "] is not valid", "date_invalid");
        }
        
        return date;
    }
    
    private static class DateFormat {
        public static final char SUPPORTED_SEPERATORS[] = {'.', '/', '-'};
        
        public static final int YEAR  = 0;
        public static final int MONTH = 1;
        public static final int DAY   = 2;
        
        public char seperator;
        public int[] dateComponents;
        public String monthFormat;
        public String dayFormat;
        public int formatLength;
        
        public DateFormat(String format) throws ParseException {
            extractFormat(format);
        }

        private void extractFormat(String format) throws ParseException {
            extractSeperator(format);
            extractDateComponents(format);
            extractMonthFormat(format);
            extractDayFormat(format);
            formatLength = format.length();
        }
        
        private void extractSeperator(String format) throws ParseException {
            for (int i = 0; i < SUPPORTED_SEPERATORS.length; i++) {
                if(format.indexOf(SUPPORTED_SEPERATORS[i]) != -1) {
                    seperator = SUPPORTED_SEPERATORS[i];
                    return;
                }
            }
            throw new ParseException("seperator not supported", "seperator_not_supported");
        }
        
        private void extractDateComponents(String format) throws ParseException {
            int y = format.indexOf('y');
            int m = format.indexOf('M');
            int d = format.indexOf('d');
            if(y == -1 || m == -1 || d == -1) throw new ParseException("date format invalid", "date_format_invalid");
            
            if(y < m && m < d) dateComponents = new int[]{YEAR, MONTH, DAY};
            else if(y < d && d < m) dateComponents = new int[]{YEAR, DAY, MONTH};
            else if(m < y && y < d) dateComponents = new int[]{MONTH, YEAR, DAY};
            else if(m < d && d < y) dateComponents = new int[]{MONTH, DAY, YEAR};
            else if(d < y && y < m) dateComponents = new int[]{DAY, YEAR, MONTH};
            else if(d < m && m < y) dateComponents = new int[]{DAY, MONTH, YEAR};
        }
        
        private void extractMonthFormat(String format) {
            int m1 = format.indexOf('M');
            int m2 = format.lastIndexOf('M');
            monthFormat = format.substring(m1, m2 + 1);
        }
        
        private void extractDayFormat(String format) {
            int d1 = format.indexOf('d');
            int d2 = format.lastIndexOf('d');
            dayFormat = format.substring(d1, d2 + 1);
        }
        
        public int getIndexOf(int val) {
            for (int i = 0; i < dateComponents.length; i++) {
                if(dateComponents[i] == val) return i;
            }
            return -1;
        }
    }
    
    public static class ParseException extends Exception {
        private String messageKey;
        
        /**
         * Creates a new parse exception.
         * @param message the text that gets logged out
         * @param messageKey the key of the message that is shown to the user (has to correspond with "i18n.res" file)
         */
        public ParseException(String message, String messageKey) {
            super(message);
            this.messageKey = messageKey;
        }
        public String getMessageKey() {
            return messageKey;
        }
    }
}
