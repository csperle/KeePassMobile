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

package org.sperle.keepass.ui.command;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;

/**
 * A command manager is responsible to add the commands of a form in the order
 * that fits best to the underlying platform. A command manager therefore
 * implements only a specific algorithm and has no state!
 */
public interface CommandManager {

    /**
     * Is called on application startup.
     */
    void init();

    /**
     * Implements the algorithm to order (and add) the commands of a form and sets the
     * default command (that is shown on the second soft button).
     */
    void addCommands(Form form, Command[] commands, Command defaultCommand);
}
