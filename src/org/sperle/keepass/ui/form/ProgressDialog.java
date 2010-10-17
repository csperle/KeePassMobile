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

import org.sperle.keepass.monitor.ProgressMonitor;
import org.sperle.keepass.ui.component.ProgressBar;
import org.sperle.keepass.ui.i18n.Messages;

import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.Layout;
import com.sun.lwuit.plaf.Style;

public class ProgressDialog extends Dialog {
    private ProgressMonitor pm;
    private ProgressBar progressBar;
    
    public ProgressDialog(ProgressMonitor pm) {
        super("");
        this.pm = pm;

        this.setLayout(new CenterLayout());
        setScrollable(false);

        getStyle().setBorder(null);
        getStyle().setBgTransparency(0);
        getStyle().setBgImage(null);
        getDialogStyle().setBorder(null);
        getDialogStyle().setBgTransparency(0);
        getDialogStyle().setBgImage(null);

        progressBar = new ProgressBar(pm.getStatusMessage());
        this.addComponent(progressBar);

        Command cancelCommand = new Command(Messages.get("cancel")) {
            public void actionPerformed(ActionEvent evt) {
                ProgressDialog.this.pm.cancel();
            }
        };
        addCommand(cancelCommand);
    }
    
    /**
     * Shows the progress dialog (returns immediately).
     */
    public void show() {
        super.showPacked(BorderLayout.CENTER, false);
    }
    
    /**
     * Updates the progress bar until the task has finished.
     */
    public void blockUntilTaskFinished() {
        Display.getInstance().invokeAndBlock(new Runnable() {
            private int progress = -1;
            public void run() {
                synchronized (pm) {
                    while (!pm.isFinished()) {
                        try {
                            pm.wait(50);
                        } catch (InterruptedException e) {
                        }
                        
                        if (pm.started() && progress != pm.getProgress()) {
                            progress = pm.getProgress();
                            boolean hasParam = pm.getStatusParams() != null && pm.getStatusParams().length > 0;
                            progressBar.setProgress(pm.getProgress(), Messages.get(pm.getStatusMessage())
                                    + (hasParam ? (" " + pm.getStatusParams()[0]) : ""));
                        }
                    }
                }
            }
        });
    }
    
    private static class CenterLayout extends Layout {
        public void layoutContainer(Container parent) {
            int components = parent.getComponentCount();
            Style parentStyle = parent.getStyle();
            int centerPos = parent.getLayoutWidth() / 2 + parentStyle.getMargin(Component.LEFT);
            int y = parentStyle.getMargin(Component.TOP);
            for (int iter = 0; iter < components; iter++) {
                Component current = parent.getComponentAt(iter);
                Dimension d = current.getPreferredSize();
                current.setSize(d);
                current.setX(centerPos - d.getWidth() / 2);
                Style currentStyle = current.getStyle();
                y += currentStyle.getMargin(Component.TOP);
                current.setY(y);
                y += d.getHeight() + currentStyle.getMargin(Component.BOTTOM);
            }
        }

        public Dimension getPreferredSize(Container parent) {
            int components = parent.getComponentCount();
            Style parentStyle = parent.getStyle();
            int height = parentStyle.getMargin(Component.TOP) + parentStyle.getMargin(Component.BOTTOM);
            int marginX = parentStyle.getMargin(Component.RIGHT) + parentStyle.getMargin(Component.LEFT);
            int width = marginX;
            for (int iter = 0; iter < components; iter++) {
                Component current = parent.getComponentAt(iter);
                Dimension d = current.getPreferredSize();
                Style currentStyle = current.getStyle();
                width = Math.max(d.getWidth() + marginX + currentStyle.getMargin(Component.RIGHT)
                        + currentStyle.getMargin(Component.LEFT), width);
                height += currentStyle.getMargin(Component.TOP) + d.getHeight()
                        + currentStyle.getMargin(Component.BOTTOM);
            }
            Dimension size = new Dimension(width, height);
            return size;
        }
    }
}
