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

package org.sperle.keepass.ui.tree;

import java.util.Vector;

import org.sperle.keepass.kdb.KdbEntry;
import org.sperle.keepass.kdb.KdbGroup;
import org.sperle.keepass.kdb.KdbItem;
import org.sperle.keepass.kdb.KeePassDatabase;
import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.edit.EntryForm;
import org.sperle.keepass.ui.form.Forms;
import org.sperle.keepass.ui.form.ItemListForm;
import org.sperle.keepass.ui.settings.Settings;
import org.sperle.keepass.ui.util.Vectors;

import com.sun.lwuit.Display;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.SelectionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.DefaultListModel;

public class TreeForm extends ItemListForm {
    private KeePassDatabase kdb;
    private KdbGroup parentGroup;
    private List list;
    
    public TreeForm(final KeePassDatabase kdb, final KdbGroup parentGroup) {
        super();
        this.kdb = kdb;
        this.parentGroup = parentGroup;
        
        String title = null;
        if(parentGroup == null) {
            title = kdb.getDatabaseName();
        } else {
            title = kdb.getGroupPath(parentGroup);
        }
        setTitle(title);
        
        setLayout(new BorderLayout());
        setScrollable(false);
        
        list = new List(getTreeObjects());
        if(KeePassMobile.instance().getSettings().getBoolean(Settings.QUICK_VIEW))
             list.setListCellRenderer(new QuickViewTreeListCellRenderer(kdb));
        else list.setListCellRenderer(new TreeListCellRenderer(kdb));
        list.setOrientation(List.VERTICAL);
        list.setFixedSelection(List.FIXED_NONE_CYCLIC);
        if(!KeePassMobile.instance().isFastUI()) list.setSmoothScrolling(true);
        else list.setSmoothScrolling(false);
        list.addSelectionListener(new SelectionListener() {
            public void selectionChanged(int oldSelected, int newSelected) {
                if(list.getSelectedItem() != null) {
                }
//                if(quickView) { // TODO bug: quick view does not work, when group consists of subgroups and entries!
//                    Object oldObject = list.getModel().getItemAt(oldSelected);
//                    Object newObject = list.getModel().getItemAt(newSelected);
//                    if(oldObject != null && newObject != null && !oldObject.getClass().equals(newObject.getClass())) {
//                        TreeForm.this.revalidate();
//                    }
//                }
                updateCommands();
            }
        });
        list.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(list.getSelectedItem() != null) {
                    showSelectedItem();
                }
            }
        });
        if(list.getModel().getSize() > 0) {
            list.setSelectedIndex(0);
        }
        addGameKeyListener(Display.GAME_RIGHT, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(list.getSelectedItem() != null) {
                    showSelectedItem();
                }
            }
        });
        addGameKeyListener(Display.GAME_LEFT, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(parentGroup != null) {
                    showParents();
                }
            }
        });
        addComponent(BorderLayout.CENTER, list);
        // is called implicitly with SelectionListener
        // updateCommands();
    }
    
    protected void goBack() {
        if(parentGroup != null) {
            showParents();
        }
    }
    
    private DefaultListModel getTreeObjects() {
        Vector entries = parentGroup == null ? kdb.getRootGroups() : Vectors.append(kdb.getChildGroups(parentGroup), kdb.getEntries(parentGroup));
        if(!KeePassMobile.instance().getSettings().getBoolean(Settings.SHOW_BACKUP)) {
            Object backupGroup = null;
            for (int i = 0; i < entries.size(); i++) {
                if (entries.elementAt(i) instanceof KdbGroup) {
                    KdbGroup group = (KdbGroup)entries.elementAt(i);
                    if (kdb.isBackupGroup(group)) {
                        backupGroup = group;
                        break;
                    }
                }
            }
            if(backupGroup != null) entries.removeElement(backupGroup);
        }
        return new DefaultListModel(Vectors.toItemArray(entries));
    }
    
    public void setSelected(KdbItem item) {
        list.setSelectedItem(item);
    }
    
    public void refresh() {
        list.setModel(getTreeObjects());
        updateCommands();
    }
    
    private void showSelectedItem() {
        Object selectedItem = list.getSelectedItem();
        if(selectedItem instanceof KdbGroup) {
            showChilds();
        } else {
            showEntry(false);
        }
    }
    
    private void showChilds() {
        Forms.setTransitionOut(this, true);
        TreeForm treeForm = new TreeForm(kdb, (KdbGroup)list.getSelectedItem());
        treeForm.show();
    }

    private void showEntry(boolean editTitle) {
        Forms.setNoTransitionOut(this);
        new EntryForm((KdbEntry)list.getSelectedItem(), editTitle).show();
    }
    
    private void showParents() {
        Forms.setTransitionOut(this, false);
        TreeForm treeForm = new TreeForm(kdb, kdb.getParentGroup(parentGroup));
        treeForm.setSelected(parentGroup);
        treeForm.show();
    }
    
    public KeePassDatabase getKdb() {
        return kdb;
    }
    
    public List getList() {
        return list;
    }

    public KdbGroup getParentGroup() {
        return parentGroup;
    }
}
