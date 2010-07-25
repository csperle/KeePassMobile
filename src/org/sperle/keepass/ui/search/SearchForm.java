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

package org.sperle.keepass.ui.search;

import java.io.IOException;
import java.util.Vector;

import org.sperle.keepass.kdb.KdbEntry;
import org.sperle.keepass.kdb.KdbItem;
import org.sperle.keepass.kdb.KeePassDatabase;
import org.sperle.keepass.kdb.SearchOptions;
import org.sperle.keepass.kdb.v1.KdbEntryV1;
import org.sperle.keepass.monitor.ProgressMonitor;
import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.KeePassMobileMIDlet;
import org.sperle.keepass.ui.edit.EntryForm;
import org.sperle.keepass.ui.form.Forms;
import org.sperle.keepass.ui.form.ItemListForm;
import org.sperle.keepass.ui.form.ProgressDialog;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.settings.Settings;
import org.sperle.keepass.ui.tree.QuickViewTreeListCellRenderer;
import org.sperle.keepass.ui.tree.TreeListCellRenderer;
import org.sperle.keepass.ui.util.QuickSorter;

import com.sun.lwuit.Command;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.DefaultListModel;
import com.sun.lwuit.util.Log;

public class SearchForm extends ItemListForm {
    private static final DefaultListModel NOT_FOUND_MODEL = new DefaultListModel(new Object[]{Messages.get("search_notfound")});
    private KeePassDatabase kdb;
    
    private Container searchPanel;
    private Label searchLabel;
    private TextArea searchField;
    private List searchResultList;
    private Command defaultCommand;
    
    private String searchText = "";
    private volatile Vector searchResults = new Vector(); // volatile = needed because values are set/read in different threads
    
    private SearchOptionsForm options;
    
    private static SearchForm instance;
    public static synchronized SearchForm create(final KeePassMobile app, final KeePassDatabase kdb) {
        if(instance == null || instance.kdb != kdb) { // return same search form for same database
            instance = new SearchForm(app, kdb);
            
            SearchOptions defaultOptions = null;
            if(app.getSettings() == null || app.getSettings().getSearchOptions() == null) {
                defaultOptions = new SearchOptions();
            } else {
                defaultOptions = app.getSettings().getSearchOptions();
            }
            instance.options = new SearchOptionsForm(app, instance, defaultOptions);
        }
        instance.setFocused(instance.searchField);
        return instance;
    }
    
    private SearchForm(final KeePassMobile app, final KeePassDatabase kdb) {
        super(app, Messages.get("search") + " " + kdb.getDatabaseName());
        this.kdb = kdb;
        
        setLayout(new BorderLayout());
        setScrollable(false);
        
        app.getCommandManager().addCommands(this, createCommands(), defaultCommand);
        
        addComponent(BorderLayout.NORTH, getSearchPanel());
        addComponent(BorderLayout.CENTER, getSearchResultList());
    }
    
    private Command[] createCommands() {
        Command[] commands = new Command[3];
        commands[0] = backCommand;
        commands[1] = new Command(Messages.get("search_options")) {
            public void actionPerformed(ActionEvent evt) {
                options.show();
            }
        };
        commands[2] = new Command(Messages.get("help")) {
            public void actionPerformed(ActionEvent evt) {
                Forms.showHelp(Messages.get("search_help"));
            }
        };
        defaultCommand = backCommand;
        return commands;
    }
    
    public void refresh() {
        searchResultList.repaint();
    }
    
    public void setSelected(KdbItem item) {
        searchResultList.setSelectedItem(item);
    }
    
    private Container getSearchPanel() {
        if(searchPanel == null) {
            searchPanel = new Container();
            searchPanel.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
            searchPanel.setScrollable(false);
            
            searchLabel = new Label(Messages.get("search_text"));
            searchPanel.addComponent(searchLabel);
            searchField = new TextArea(searchText);
            searchField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    searchText = searchField.getText();
                    Display.getInstance().callSerially(new Runnable() {
                        public void run() {
                            search(options.getSearchOptions());
                            if(searchResults.size() > 0) {
                                searchResultList.setModel(new DefaultListModel(searchResults));
                                searchResultList.setSelectedIndex(0);
                                SearchForm.this.setFocused(searchResultList);
                            } else {
                                searchResultList.setModel(NOT_FOUND_MODEL);
                                SearchForm.this.setFocused(searchField);
                            }
                            Settings settings = app.getSettings();
                            try {
                                if(settings.available()) settings.setSearchOptions(options.getSearchOptions());
                            } catch (IOException e) {
                                Log.p("Could not write search option settings - " + e.toString(), Log.ERROR);
                            }
                        }
                    });
                }
            });
            searchPanel.addComponent(searchField);
        }
        return searchPanel;
    }
    
    private List getSearchResultList() {
        if(searchResultList == null) {
            searchResultList = new List();
            if(app.getSettings().getBoolean(Settings.QUICK_VIEW))
                searchResultList.setListCellRenderer(new QuickViewTreeListCellRenderer(app, kdb, app.isFastUI()));
            else searchResultList.setListCellRenderer(new TreeListCellRenderer(app, kdb, app.isFastUI()));
            searchResultList.setOrientation(List.VERTICAL);
            searchResultList.setFixedSelection(List.FIXED_NONE_CYCLIC);
            if(!app.isFastUI()) searchResultList.setSmoothScrolling(true);
            else searchResultList.setSmoothScrolling(false);
            searchResultList.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if(searchResultList.getSelectedItem() instanceof KdbEntryV1) {
                        KdbEntryV1 selectedEntry = (KdbEntryV1)searchResultList.getSelectedItem();
                        if(selectedEntry != null) {
                            showEntry(selectedEntry);
                        }
                    }
                }
            });
        }
        return searchResultList;
    }
    
    // synchronized = needed because values of 'searchResults' are set/read in different threads
    private synchronized void search(final SearchOptions so) {
        searchResults.removeAllElements();
        if(searchText != null && !"".equals(searchText)) {
            final ProgressMonitor pm = new ProgressMonitor();
            final ProgressDialog pd = new ProgressDialog(pm);
            pd.show();
            
            new Thread(new Runnable() {
                public void run() {
                    try{
                        Vector sr = kdb.search(searchText, so, pm);
                        QuickSorter.sort(sr);
                        addAllEntries(sr, searchResults);
                    } finally {
                        pm.finish();
                    }
                }
            }).start();
            pd.blockUntilTaskFinished();
            pd.dispose();
        }
    }
    
    private void showEntry(KdbEntry selectedEntry) {
        Forms.setTransitionOut(this, true, app.isFastUI());
        new EntryForm(app, kdb, selectedEntry, false, app.isFastUI()).show();
    }
    
    private void addAllEntries(Vector from, Vector to) {
        for (int i = 0; i < from.size(); i++) {
            to.addElement(from.elementAt(i));
        }
    }
}
