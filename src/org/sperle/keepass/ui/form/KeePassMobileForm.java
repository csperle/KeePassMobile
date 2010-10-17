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
import org.sperle.keepass.ui.command.FormCommands;
import org.sperle.keepass.ui.command.KeePassMobileCommand;
import org.sperle.keepass.ui.i18n.Messages;

import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.events.ActionEvent;

/**
 * Super class of all KeePassMobile forms.
 */
public abstract class KeePassMobileForm extends Form {
    
    protected final Form previousForm;
    private final FormCommands commands;
    
    public KeePassMobileForm() {
        super();
        this.previousForm = Display.getInstance().getCurrent();
        this.setBackCommand(new KeePassMobileCommand(Messages.get("back")) {
            public void actionPerformed(ActionEvent ev) {
                Forms.setNoTransitionOut(KeePassMobileForm.this);
                goBack();
            }
        });
        this.commands = KeePassMobile.instance().getCommandManager().getCommands(this);
    }
    
    public KeePassMobileForm(String title) {
        this();
        this.setTitle(title);
    }
    
    /**
     * Default implementation of back command -> show previous form. Override in concrete implementations!
     */
    protected void goBack() {
        if(previousForm != null) previousForm.show();
    }
    
    /**
     * Restart security timer.
     */
    public void keyPressed(int keyCode) {
        super.keyPressed(keyCode);
        
        KeePassMobile.instance().keyPressed();
    }

    public void updateCommands() {
        this.commands.update();
        KeePassMobile.instance().getCommandManager().updateCommands(this, commands);
    }
}
