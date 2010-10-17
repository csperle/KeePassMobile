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

package org.sperle.keepass.ui;

import java.io.IOException;
import java.util.Vector;

import org.sperle.keepass.KeePassMobileIO;
import org.sperle.keepass.kdb.KdbEntry;
import org.sperle.keepass.kdb.KeePassDatabase;
import org.sperle.keepass.rand.Random;
import org.sperle.keepass.ui.command.CommandManager;
import org.sperle.keepass.ui.font.Fonts;
import org.sperle.keepass.ui.form.Forms;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.icon.Icons;
import org.sperle.keepass.ui.locale.Locales;
import org.sperle.keepass.ui.menu.MainMenuForm;
import org.sperle.keepass.ui.settings.Settings;
import org.sperle.keepass.ui.settings.j2me.RecordStoreSettings;
import org.sperle.keepass.ui.source.create.CreateDatabaseSource;
import org.sperle.keepass.ui.source.file.FileSource;
import org.sperle.keepass.ui.source.file.LastFileSource;
import org.sperle.keepass.ui.source.midlet.MidletSource;
import org.sperle.keepass.ui.theme.Themes;
import org.sperle.keepass.ui.util.SecurityTimer;
import org.sperle.keepass.ui.util.Vectors;

import com.sun.lwuit.util.Log;

/**
 * This class represents the KeePassMobile application by itself and should be completely platform independent.
 * It implements the Application interface for startup and shutdown and to react on the platform events.
 */
public class KeePassMobile implements Application {
    public static final String NAME = "KeePassMobile";
    public static final String VERSION = "V0.9";
    public static final String COPYRIGHT = "Copyright (c) 2010";
    public static final String ME = "Christoph Sperle";
    public static final String LICENSE = "Copyright (c) 2009-2010 Christoph Sperle <keepassmobile@gmail.com>\n\n" +
        "KeePassMobile is free software: you can redistribute it and/or modify it under the terms of the GNU General " +
        "Public License as published by the Free Software Foundation, either version 3 of the License, or (at your " +
        "option) any later version.\n\n" +
        "KeePassMobile is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the " +
        "implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License " +
        "for more details.\n\n" +
        "You should have received a copy of the GNU General Public License along with KeePassMobile.  If not, see " +
        "<http://www.gnu.org/licenses/>.";
    
    // Application platform properties (J2ME: defined in JAD file)
    public static final String KPM_BETA_VERSION = "KPM-BetaVersion";
    public static final String KPM_THEME = "KPM-Theme";
    public static final String KPM_DEFAULT_LANGUAGE = "KPM-DefaultLanguage";
    public static final String KPM_PASSWORD_FIELD_WORKAROUND = "KPM-PasswordFieldWorkaround";
    
    private static KeePassMobile instance;
    private Platform platform;
    private KeePassMobileIO keePassMobileIO;
    private CommandManager commandManager;
    
    private Settings settings;
    private MainMenuForm mainMenu;
    private boolean fastUI;
    private SecurityTimer securityTimer;
    private KdbEntry clipboardEntry;
    private boolean passwordFieldWorkaroundEnabled = false;
    
    private KeePassMobile(Platform platform) {
        this.platform = platform;
    }
    
    /**
     * Call this once, during platform startup to initialize the application.
     */
    public static void init(Platform platform) {
        instance = new KeePassMobile(platform);
    }
    
    /**
     * Returns the configured application.
     */
    public static KeePassMobile instance() {
        return instance;
    }
    
    /**
     * Starts up the KeePassMobile application.
     */
    public void startUp() {
        Log.p("KeePassMobile starts up...", Log.DEBUG);
        
        // init app properties
        passwordFieldWorkaroundEnabled = "enabled".equalsIgnoreCase(getAppProperty(KPM_PASSWORD_FIELD_WORKAROUND));
        
        // show splash image, if available and not showing fast theme
        String themeProp = getAppProperty(KPM_THEME);
        if(themeProp == null || !Themes.FAST_THEME.equals(themeProp)) {
            Forms.showSplashScreen();
        }
        
        // open settings
        settings = new RecordStoreSettings();
        try {
            settings.open();
        } catch (IOException e) {
            Log.p("KeePassMobile is not able to store any settings/preferences - " + e.toString(), Log.WARNING);
        }
        
        // load and apply theme
        Themes.load();
        
        // apply theme from application descriptor if available
        if(themeProp != null && !Themes.NOT_SET.equals(themeProp)) {
            if(Themes.isSupported(themeProp)) {
                Themes.apply(themeProp);
                Log.p("Show theme [" + themeProp + "] from application descriptor", Log.DEBUG);
            } else {
                Log.p("Theme [" + themeProp + "] is not supported. Please change (or delete) the 'KPM-Theme' property in the application descriptor!", Log.WARNING);
            }
        }
        
        // apply standard theme
        if(Themes.inUse() == null) {
            fastUI = settings.getBoolean(Settings.UI_FAST);
            if(fastUI || !Themes.isSupported(Themes.NICE_THEME)) {
                Themes.apply(Themes.FAST_THEME);
                Log.p("Show fast UI", Log.DEBUG);
            } else {
                Themes.apply(Themes.NICE_THEME);
                Log.p("Show nice UI", Log.DEBUG);
            }
        }
        
        
        // create and initialize platform specific components
        KeePassMobileFactory keePassMobileFactory = platform.getKeePassMobileFactory();
        keePassMobileIO = keePassMobileFactory.createKeePassMobileIO();
        commandManager = keePassMobileFactory.createCommandManager();
        commandManager.init();
        
        // load icons
        Icons.load();
        Log.p("Icons loaded", Log.DEBUG);
        
        // load fonts
        Fonts.load( settings.getInt(Settings.FONT_SIZE, Fonts.SIZE_DEFAULT));
        Log.p("Fonts loaded", Log.DEBUG);
        
        // load UI language
        Messages.load();
        Log.p("UI langugages loaded", Log.DEBUG);
        
        // apply default language
        String defaultLangProp = getAppProperty(KPM_DEFAULT_LANGUAGE);
        if(defaultLangProp != null) {
            if(Messages.supported(defaultLangProp)) {
                Messages.setDefaultLang(defaultLangProp);
            } else {
                Log.p("Default language [" + defaultLangProp + "] is not supported. Please change the 'DefaultLanguage' property in the application descriptor!", Log.WARNING);
            }
        }
        if(Messages.getDefaultLang() == null) {
            Messages.setDefaultLang("en");
        }
        
        // apply UI language
        String lang = null;
        if(settings.exists(Settings.LANGUAGE)) {
            String slang = settings.get(Settings.LANGUAGE);
            if(Messages.supported(slang)) {
                lang = slang;
                Log.p("Use locale/language [" + lang + "] from preferences", Log.DEBUG);
            }
            else try {settings.delete(Settings.LANGUAGE);} catch (IOException e) {/* not important */}
        }
        if(lang == null) { // get user language form mobile
            String mlang = Locales.getMobileLocale();
            if(Messages.supported(mlang)) {
                lang = mlang;
                Log.p("Use locale [" + lang + "] from mobile", Log.DEBUG);
            } else {
                String mllang = Locales.getMobileLanguge();
                if(Messages.supported(mllang)) {
                    lang = mllang;
                    Log.p("Use language [" + mllang + "] from mobile", Log.DEBUG);
                } else Log.p("Your mobile language [" + mllang + "] is not supported. You can help! " +
                                "Please write an email to contact@keepassmobile.com and offer to translate KeePassMobile into your language. Thank you!", Log.INFO);
            }
        }
        if(lang == null) {
            lang = Messages.getDefaultLang();
            Log.p("Fallback to default language [" + lang + "]", Log.DEBUG);
        }
        Messages.setSelectedLang(lang);
        
        // locale settings
        Locales.load();
        Log.p("Locale settings loaded for mobile locale [" + Locales.getMobileLocale() + "]", Log.DEBUG);
        
        String dateFormat = null;
        if(settings.exists(Settings.DATE_FORMAT)) {
            dateFormat = settings.get(Settings.DATE_FORMAT);
            Locales.setDateFormat(dateFormat);
            Log.p("Use date format [" + dateFormat + "] from preferences", Log.DEBUG);
        }
        if(dateFormat == null) {
            String countryCode = Locales.getMobileCountry();
            if(countryCode == null || countryCode.length() == 0) {
                Log.p("Country code of mobile undefined.", Log.INFO);
            } else {
                dateFormat = Locales.getDateFormat(countryCode);
                if(dateFormat != null) {
                    Locales.setDateFormat(dateFormat);
                    Log.p("Use date format [" + dateFormat + "] for country [" + countryCode + "]", Log.DEBUG);
                } else {
                    Log.p("Date format for country code [" + countryCode + "] unknown. You can help!" +
                            "Please write an email to contact@keepassmobile.com that contains the country code and your preferred date format. Thank you!", Log.INFO);
                }
            }
        }
        if(dateFormat == null) {
            dateFormat = Locales.getDateFormat(lang);
            if(dateFormat != null) {
                Locales.setDateFormat(dateFormat);
                Log.p("Use date format [" + dateFormat + "] for language fallback [" + lang + "]", Log.DEBUG);
            } else {
                Locales.setDateFormat(Locales.DEFAULT_DATE_FORMAT);
                Log.p("Use default date format [" + Locales.DEFAULT_DATE_FORMAT + "].", Log.INFO);
            }
        }
        
        // show used memory
        int freeMem  = (int)Runtime.getRuntime().freeMemory()/1024;
        int totalMem = (int)Runtime.getRuntime().totalMemory()/1024;
        int usedMem  = totalMem - freeMem;
        int usedPerc = usedMem*100/totalMem;
        Log.p("Used memory after KeePassMobile initialization: " + usedMem + "kb/" + totalMem + "kb (" + usedPerc + "%)", Log.DEBUG);
        
        // show main menu
        mainMenu = new MainMenuForm(Vectors.toMenuItemArray(getMainMenuItems()));
        showMainMenu();
        
        // show beta version warning
        if("true".equalsIgnoreCase(getAppProperty(KPM_BETA_VERSION))) {
            Log.p("KeePassMobile " + VERSION + " BETA version!", Log.DEBUG);
            Forms.showBetaWarning();
        }
        
        Log.p("KeePassMobile " + VERSION + " started", Log.DEBUG);
    }
    
    public void pause() {
    }
    
    public void resume() {
    }
    
    /**
     * Sends the platform the signal to exit the application.
     */
    public void exit() {
        shutdown();
        platform.exit();
    }

    /**
     * Asks the platform for an application property.
     */
    public String getAppProperty(String key) {
        return platform.getProperty(key);
    }
    
    public KeePassMobileIO getKeePassMobileIO() {
        return keePassMobileIO;
    }
    
    public CommandManager getCommandManager() {
        return commandManager;
    }
    
    public Random createRandom() {
        return platform.getKeePassMobileFactory().createRandom();
    }
    
    private Vector getMainMenuItems() {
        Vector menuItems = new Vector();
        if(settings.exists(Settings.LAST_FILE)) {
            menuItems.addElement(new LastFileSource(settings.get(Settings.LAST_FILE)));
        }
        menuItems.addElement(new CreateDatabaseSource());
        menuItems.addElement(new FileSource());
        if(System.getProperty("development") != null) {
            menuItems.addElement(new MidletSource());
        }
        return menuItems;
    }
    
    public void showMainMenu() {
        // wipe out references to closed kdb
        System.gc();
        System.gc();
        
        mainMenu.refresh(Vectors.toMenuItemArray(getMainMenuItems()));
        mainMenu.show();
    }
    
    public void startSecurityTimer(KeePassDatabase kdb) {
        long timeout = SecurityTimer.DEFAULT_TIMEOUT_MILLIS;
        if(settings.exists(Settings.SECURITY_TIMEOUT)) {
            try {
                timeout = Long.parseLong(settings.get(Settings.SECURITY_TIMEOUT));
            } catch (NumberFormatException e) {
            }
        }
        if(timeout > 0) {
            securityTimer = new SecurityTimer(kdb, timeout);
            new Thread(securityTimer).start();
            Log.p("Security timer set up. Set timeout to: " + (timeout/60000) + "min", Log.DEBUG);
        }
    }
    
    public void keyPressed() {
        if(securityTimer != null) securityTimer.keyPressed();
    }
    
    public void stopSecurityTimer() {
        if(securityTimer != null) securityTimer.stopTimer();
    }
    
    public void releaseSecurityTimer() {
        if(securityTimer != null) securityTimer = null;
    }
    
    public Settings getSettings() {
        return settings;
    }
    
    public boolean isFastUI() {
        return fastUI;
    }
    
    public void addToClipboard(KdbEntry entry) {
        this.clipboardEntry = entry;
    }
    
    public KdbEntry getClipboardEntry() {
        return clipboardEntry;
    }

    public void emptyClipboard() {
        this.clipboardEntry = null;
    }
    
    public boolean isPasswordFieldWorkaroundEnabled() {
        return passwordFieldWorkaroundEnabled;
    }

    public void shutdown() {
        Log.p("KeePassMobile shuts down...", Log.DEBUG);
        if(settings != null) settings.close();
    }
}
