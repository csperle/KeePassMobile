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

package org.sperle.keepass.ui.component;

import org.sperle.keepass.kdb.KdbDate;
import org.sperle.keepass.ui.util.DateFormatter;
import org.sperle.keepass.ui.util.DateFormatter.ParseException;

import com.sun.lwuit.TextArea;

public class DateField extends TextArea {
    private DateFormatter formatter;
    
    public DateField(KdbDate date) throws ParseException {
        super();
        formatter = new DateFormatter();
        setDate(date);
    }
    
    public void setDate(KdbDate date) throws ParseException {
        if(date == null) setText("");
        else setText(formatter.format(date));
    }

    public KdbDate getDate() throws ParseException {
        return getText().length() > 0 ? formatter.parse(getText()) : null;
    }
}
