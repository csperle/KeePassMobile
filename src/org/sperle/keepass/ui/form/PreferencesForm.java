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

package org.sperle.keepass.ui.form;

import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.font.Fonts;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.locale.Locales;
import org.sperle.keepass.ui.settings.Settings;
import org.sperle.keepass.ui.theme.Themes;
import org.sperle.keepass.ui.util.SecurityTimer;

import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Label;
import com.sun.lwuit.layouts.BoxLayout;

public class PreferencesForm extends KeePassMobileForm {
    private Label uiLabel;
    private ComboBox uiBox;
//    private Label touchLabel;
//    private ComboBox touchBox;
    private Label fontLabel;
    private ComboBox fontBox;
    private Label backupLabel;
    private ComboBox backupBox;
    private Label quickLabel;
    private ComboBox quickBox;
    private Label langLabel;
    private ComboBox langBox;
    private Label dfLabel;
    private ComboBox dfBox;
    private Label timeoutLabel;
    private ComboBox timeoutBox;
    
    public PreferencesForm() {
        super(Messages.get("preferences"));
        
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setScrollableY(true);
        
        if(Themes.isSupported(Themes.NICE_THEME)) {
            uiLabel = new Label(Messages.get("ui"));
            addComponent(uiLabel);
            uiBox = new ComboBox(new String[]{Messages.get("ui_nice"), Messages.get("ui_fast")});
            if(KeePassMobile.instance().getSettings().getBoolean(Settings.UI_FAST)) {
                uiBox.setSelectedIndex(1);
            } else {
                uiBox.setSelectedIndex(0);
            }
            addComponent(uiBox);
        }
        
// TODO enable again, when touch devices are fully supported
//        touchLabel = new Label(Messages.get("touch_device"));
//        addComponent(touchLabel);
//        touchBox = new ComboBox(new String[]{Messages.get("yes"), Messages.get("no")});
//        if(midlet.getSettings().exists(Settings.TOUCH_DEVICE) && midlet.getSettings().getBoolean(Settings.TOUCH_DEVICE) ||
//           !midlet.getSettings().exists(Settings.TOUCH_DEVICE) && midlet.isTouchDevice()) {
//            touchBox.setSelectedIndex(0);
//        } else {
//            touchBox.setSelectedIndex(1);
//        }
//        addComponent(touchBox);
        
        fontLabel = new Label(Messages.get("font_size"));
        addComponent(fontLabel);
        fontBox = new ComboBox(new String[]{Messages.get("font_small"), Messages.get("font_medium"), Messages.get("font_large")});
        fontBox.setSelectedIndex(KeePassMobile.instance().getSettings().getInt(Settings.FONT_SIZE, Fonts.SIZE_DEFAULT));
        addComponent(fontBox);
        
        langLabel = new Label(Messages.get("language"));
        addComponent(langLabel);
        langBox = new ComboBox(getSupportedLanguages()); // TODO sort language Strings depending on selected lang
        int selectedLang = getSelectedLanguage();
        if(selectedLang >= 0) {
            langBox.setSelectedIndex(selectedLang);
        }
        addComponent(langBox);
        
        backupLabel = new Label(Messages.get("show_backup"));
        addComponent(backupLabel);
        backupBox = new ComboBox(new String[]{Messages.get("yes"), Messages.get("no")});
        if(KeePassMobile.instance().getSettings().getBoolean(Settings.SHOW_BACKUP)) {
            backupBox.setSelectedIndex(0);
        } else {
            backupBox.setSelectedIndex(1);
        }
        addComponent(backupBox);
        
        quickLabel = new Label(Messages.get("quick_view"));
        addComponent(quickLabel);
        quickBox = new ComboBox(new String[]{Messages.get("on"), Messages.get("off")});
        if(KeePassMobile.instance().getSettings().getBoolean(Settings.QUICK_VIEW)) {
            quickBox.setSelectedIndex(0);
        } else {
            quickBox.setSelectedIndex(1);
        }
        addComponent(quickBox);
        
        dfLabel = new Label(Messages.get("date_format"));
        addComponent(dfLabel);
        dfBox = new ComboBox(Locales.SUPPORTED_DATE_FORMATS);
        int selectedDateFormat = getSelectedDateFormat();
        if(selectedDateFormat >= 0) {
            dfBox.setSelectedIndex(selectedDateFormat);
        }
        addComponent(dfBox);
        
        timeoutLabel = new Label(Messages.get("timeout"));
        addComponent(timeoutLabel);
        timeoutBox = new ComboBox(getSecurityTimeouts());
        int selectedTimeoutIndex = getSelectedTimeoutIndex();
        if(selectedTimeoutIndex >= 0) {
            timeoutBox.setSelectedIndex(selectedTimeoutIndex);
        }
        addComponent(timeoutBox);
        updateCommands();
    }
    
    private String[] getSupportedLanguages() {
        String[] langs = new String[Messages.SUPPORTED_LANGS.length];
        for (int i = 0; i < Messages.SUPPORTED_LANGS.length; i++) {
            langs[i] = Messages.get(Messages.SUPPORTED_LANGS[i]);
        }
        return langs;
    }
    
//    private String[] getSupportedLanguagesSorted() {
//        Vector langs = new Vector(Messages.SUPPORTED_LANGS.length);
//        for (int i = 0; i < Messages.SUPPORTED_LANGS.length; i++) {
//            langs.addElement(new ComparableString(Messages.get(Messages.SUPPORTED_LANGS[i])));
//        }
//        
//        QuickSorter.sort(langs);
//        
//        String[] langsSorted = new String[langs.size()];
//        for (int i = 0; i < langs.size(); i++) {
//            langsSorted[i] = langs.elementAt(i).toString();
//        }
//        return langsSorted;
//    }
    
    private int getSelectedLanguage() {
        for (int i = 0; i < Messages.SUPPORTED_LANGS.length; i++) {
            if(Messages.SUPPORTED_LANGS[i].equals(Messages.getSelectedLang())) return i;
        }
        return -1;
    }
    
    private int getSelectedDateFormat() {
        for (int i = 0; i < Locales.SUPPORTED_DATE_FORMATS.length; i++) {
            if(Locales.SUPPORTED_DATE_FORMATS[i].equals(Locales.getDateFormat())) return i;
        }
        return -1;
    }
    
    private String[] getSecurityTimeouts() {
        String[] timeouts = new String[SecurityTimer.SUPPORTED_TIMEOUT_MINUTES.length];
        for (int i = 0; i < SecurityTimer.SUPPORTED_TIMEOUT_MINUTES.length; i++) {
            if(i == 0) {
                timeouts[i] = Messages.get("never");
            } else if(i == 1) {
                timeouts[i] = SecurityTimer.SUPPORTED_TIMEOUT_MINUTES[i] + " " +Messages.get("minute");
            } else {
                timeouts[i] = SecurityTimer.SUPPORTED_TIMEOUT_MINUTES[i] + " " +Messages.get("minutes");
            }
        }
        return timeouts;
    }
    
    private int getSelectedTimeoutIndex() {
        long timeout = SecurityTimer.DEFAULT_TIMEOUT_MILLIS;
        if(KeePassMobile.instance().getSettings().exists(Settings.SECURITY_TIMEOUT)) {
            try {
                timeout = Long.parseLong(KeePassMobile.instance().getSettings().get(Settings.SECURITY_TIMEOUT));
            } catch (NumberFormatException e) {
            }
        }
        if(timeout < 0) return 0;
        
        int tmins = (int) (timeout/60000);
        for (int i = 0; i < SecurityTimer.SUPPORTED_TIMEOUT_MINUTES.length; i++) {
            if(SecurityTimer.SUPPORTED_TIMEOUT_MINUTES[i] == tmins) return i;
        }
        return -1;
    }

    ComboBox getUiBox() {
        return uiBox;
    }

    ComboBox getBackupBox() {
        return backupBox;
    }

    ComboBox getQuickBox() {
        return quickBox;
    }

    ComboBox getLangBox() {
        return langBox;
    }

    ComboBox getDfBox() {
        return dfBox;
    }

    ComboBox getTimeoutBox() {
        return timeoutBox;
    }

    ComboBox getFontBox() {
        return fontBox;
    }
}
