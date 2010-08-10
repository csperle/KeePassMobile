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

import org.sperle.keepass.KeePassMobileIO;
import org.sperle.keepass.rand.Random;
import org.sperle.keepass.ui.command.CommandManager;

/**
 * The KeePassMobileFactory should be able to construct the (platform dependent) components of the
 * KeePassMobile application.
 */
public interface KeePassMobileFactory {
    /**
     * Creates and returns a fully configured instance of the KeePassMobileIO facade.
     */
    KeePassMobileIO createKeePassMobileIO();
    
    /**
     * Creates and returns a command manager instance.
     */
    CommandManager createCommandManager();
    
    /**
     * Creates and returns a Random instance.
     */
    Random createRandom();
}
