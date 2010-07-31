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

import java.io.IOException;

import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.icon.Icons;

import com.sun.lwuit.Command;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.layouts.BorderLayout;

public class Forms {
    public static void showHelp(String help) {
        showInfoForm(Messages.get("help"), help);
    }
    
    public static void showLog(String log) {
        showForm(Messages.get("log"), log);
    }
    
    public static void showBetaWarning() {
        showForm(Messages.get("beta_warning_title"), Messages.get("beta_warning_text"), Icons.getWarningIcon(), Messages.get("ok"));
    }
    
    public static void showInfoForm(String title, String info) {
        showForm(title, info, Icons.getInfoIcon(), Messages.get("back"));
    }
    
    public static void showWarningForm(String title, String info) {
        showForm(title, info, Icons.getWarningIcon(), Messages.get("back"));
    }
    
    public static void showErrorForm(String title, String info) {
        showForm(title, info, Icons.getErrorIcon(), Messages.get("back"));
    }
    
    public static void showForm(String title, String info) {
        showForm(title, info, null, Messages.get("back"));
    }
    
    public static void showForm(String title, String info, Image icon, String commandMessage) {
        TextArea area = new TextArea(info, 5, 20);
        area.setEditable(false);
        area.getSelectedStyle().setBgColor(0x6600cc);
        Form f = new Form(title);
        if(icon != null) f.getTitleComponent().setIcon(icon);
        f.setScrollable(false);
        final Form current = Display.getInstance().getCurrent();
        Command back = new Command(commandMessage) {
            public void actionPerformed(ActionEvent ev) {
                current.show();
            }
        };
        f.addCommand(back);
        f.setBackCommand(back);
        f.setLayout(new BorderLayout());
        f.addComponent(BorderLayout.CENTER, area);
        f.show();
    }
    
    // add splash.png to show splash image at startup
    public static void showSplashScreen() {
        Image splashImage = null;
        try {
            splashImage = Image.createImage("/splash.png");
        } catch (IOException e) {}
        
        if(splashImage != null) {
            splashImage = splashImage.scaledSmallerRatio(Display.getInstance().getDisplayWidth(), Display.getInstance().getDisplayHeight());
            Form f = new Form("");
            f.setScrollable(false);
            f.setLayout(new BorderLayout());
            f.addComponent(BorderLayout.CENTER, new Label(splashImage));
            f.show();
        }
    }
    
    public static void setNoTransitionOut(Form form) {
        form.setTransitionOutAnimator(CommonTransitions.createEmpty());
    }
    
    public static void setTransitionOut(Form form, boolean forward, boolean fastUI) {
        if(fastUI) {
            form.setTransitionOutAnimator(CommonTransitions.createEmpty());
        } else {
            form.setTransitionOutAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, !forward, 300));
        }
    }
}
