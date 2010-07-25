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

import com.sun.lwuit.Component;
import com.sun.lwuit.Display;
import com.sun.lwuit.Font;
import com.sun.lwuit.Graphics;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.plaf.Style;

public class ProgressBar extends Component {
    private int percent;
    private String message;
    
    public ProgressBar() {
        setFocusable(false);
    }
    
    public ProgressBar(String message) {
        this();
        this.message = message;
    }

    public String getUIID() {
        return "ProgressBar";
    }

    public void setProgress(int percent) {
        this.percent = percent;
        repaint();
    }
    
    public void setProgress(int percent, String message) {
        this.percent = percent;
        this.message = message;
        repaint();
    }

    public void setMessage(String message) {
        this.message = message;
        repaint();
    }
    
    protected Dimension calcPreferredSize() {
        return new Dimension(Display.getInstance().getDisplayWidth(), Font.getDefaultFont().getHeight() + 5);
    }

    public void paint(Graphics g) {
        int width = (int) ((((float) percent) / 100.0f) * getWidth());

        Style s = getUnselectedStyle();
        g.setColor(s.getBgColor());
        int curve = getHeight() / 2 - 1;
        g.fillRoundRect(getX(), getY(), getWidth() - 1, getHeight() - 1, curve, curve); // draw filled background rect
        if(width > 0) {
            g.setColor(getSelectedStyle().getBgColor());
            g.fillRoundRect(getX(), getY(), width - 1, getHeight() - 1, curve, curve); // draw filled progress rect
        }
        g.setColor(s.getFgColor());
        g.drawRoundRect(getX(), getY(), getWidth() - 1, getHeight() - 1, curve, curve); // draw (white) frame around progress bar
        
        if(message != null && !"".equals(message)) {
            g.setColor(s.getFgColor());
            int msgWidth = g.getFont().stringWidth(message);
            g.drawString(message, getX() + ((getWidth() - msgWidth) / 2), getY());
        }
    }
}
