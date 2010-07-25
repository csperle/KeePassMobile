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

package org.sperle.keepass.ui.source.file;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import org.sperle.keepass.ui.KeePassMobile;
import org.sperle.keepass.ui.form.Forms;
import org.sperle.keepass.ui.form.KeePassMobileForm;
import org.sperle.keepass.ui.i18n.Messages;
import org.sperle.keepass.ui.util.QuickSorter;

import com.sun.lwuit.Command;
import com.sun.lwuit.Display;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.DefaultListModel;
import com.sun.lwuit.list.ListModel;
import com.sun.lwuit.util.Log;

/**
 * Standard file chooser form.
 */
public class FileChooserForm extends KeePassMobileForm {
    /** The path separator string */
    private static final String SEPARATOR = "/";
    /** The path separator as a character */
    private static final char SEP_CHAR = '/';
    /** The prefix for a FileConnection URL */
    private static final String URL_PREFIX = "file:///";
    
    private String currentDir;
    private List dirList;
    private String fileEndingFilter;
    private boolean directoriesOnly = false;
    private FileChooserCallback callback;
    private Command defaultCommand;
    
    public FileChooserForm(final KeePassMobile app, FileChooserCallback callback, boolean fastUI) {
        super(app, Messages.get("select_db"));
        this.callback = callback;
        
        setLayout(new BorderLayout());
        setScrollable(false);
        
        app.getCommandManager().addCommands(this, createCommands(), defaultCommand);
        
        dirList = new List();
        dirList.setListCellRenderer(new FileChooserListCellRenderer(fastUI));
        dirList.setOrientation(List.VERTICAL);
        dirList.setFixedSelection(List.FIXED_NONE_CYCLIC);
        if(!app.isFastUI()) dirList.setSmoothScrolling(true);
        else dirList.setSmoothScrolling(false);
        dirList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DirEntry dirEntry = (DirEntry) dirList.getSelectedItem();
                if (dirEntry instanceof Directory) {
                    if (dirEntry instanceof UpDirectory) {
                        currentDir = currentDir.substring(0,
                                currentDir.lastIndexOf(SEP_CHAR, currentDir.length() - 2) + 1);
                    } else {
                        if(directoriesOnly) {
                            FileChooserForm.this.callback.choosen((currentDir == null ? URL_PREFIX : currentDir) + dirEntry.getName());
                            return;
                        }
                        currentDir = currentDir == null ? URL_PREFIX + dirEntry.getName() : currentDir
                                + dirEntry.getName();
                    }
                    if (URL_PREFIX.equals(currentDir))
                        currentDir = null;

                    try {
                        dirList.setModel(getDirModel());
                    } catch (Exception e) {
                        Log.p("Error loading directory entries for [" + (currentDir == null ? "ROOT" : currentDir) + "] - " + e.toString(), Log.ERROR);
                        FileChooserForm.this.callback.errorOccured(e);
                        return;
                    }
                } else {
                    FileChooserForm.this.callback.choosen(currentDir + dirEntry.getName());
                    return;
                }
            }
        });
        addGameKeyListener(Display.GAME_RIGHT, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DirEntry dirEntry = (DirEntry) dirList.getSelectedItem();
                if (dirEntry instanceof Directory) {
                    currentDir = currentDir == null ? URL_PREFIX + dirEntry.getName() : currentDir
                            + dirEntry.getName();
                    
                    try {
                        dirList.setModel(getDirModel());
                    } catch (Exception e) {
                        Log.p("Error loading directory entries for [" + (currentDir == null ? "ROOT" : currentDir) + "] - " + e.toString(), Log.ERROR);
                        FileChooserForm.this.callback.errorOccured(e);
                        return;
                    }
                }
            }
        });
        addGameKeyListener(Display.GAME_LEFT, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(currentDir != null) {
                    currentDir = currentDir.substring(0,
                            currentDir.lastIndexOf(SEP_CHAR, currentDir.length() - 2) + 1);
                    if (URL_PREFIX.equals(currentDir))
                        currentDir = null;
                    
                    try {
                        dirList.setModel(getDirModel());
                    } catch (Exception e) {
                        Log.p("Error loading directory entries for [" + (currentDir == null ? "ROOT" : currentDir) + "] - " + e.toString(), Log.ERROR);
                        FileChooserForm.this.callback.errorOccured(e);
                        return;
                    }
                }
            }
        });
        
        addComponent(BorderLayout.CENTER, dirList);
    }
    
    private Command[] createCommands() {
        Command[] commands = new Command[3];
        commands[0] = new Command(Messages.get("cancel")) {
            public void actionPerformed(ActionEvent evt) {
                goBack();
            }
        };
        commands[1] = new Command(Messages.get("show_all_files")) {
            public void actionPerformed(ActionEvent evt) {
                setFileEndingFilter(null);
                try {
                    dirList.setModel(getDirModel());
                } catch (Exception e) {
                    Log.p("Error loading directory entries for [" + (currentDir == null ? "ROOT" : currentDir) + "] - " + e.toString(), Log.ERROR);
                    FileChooserForm.this.callback.errorOccured(e);
                    return;
                }
            }
        };
        commands[2] = new Command(Messages.get("help")) {
            public void actionPerformed(ActionEvent evt) {
                Forms.showHelp(Messages.get("filechooser_help"));
            }
        };
        defaultCommand = commands[0]; // cancel
        return commands;
    }
    
    protected void goBack() {
        FileChooserForm.this.callback.canceled();
    }
    
    private ListModel getDirModel() throws IOException {
        Vector dirList = new Vector();

        Enumeration dirContent = null;
        if (currentDir == null) {
            dirContent = FileSystemRegistry.listRoots();
        } else {
            FileConnection conn = (FileConnection) Connector.open(currentDir, Connector.READ);
            if(conn.exists()) dirContent = conn.list();
            else {
                currentDir = null;
                dirContent = FileSystemRegistry.listRoots();
            }
        }

        if (dirContent != null) {
            if (currentDir != null) dirList.addElement(new UpDirectory());
            while (dirContent.hasMoreElements()) {
                String filename = (String) dirContent.nextElement();

                if (isDirectory(filename)) {
                    dirList.addElement(new Directory(filename));
                } else {
                    if(!directoriesOnly && (fileEndingFilter == null || "".equals(fileEndingFilter) || filename.toLowerCase().endsWith(fileEndingFilter))) {
                        dirList.addElement(new File(filename));
                    }
                }
            }
        }
        
        // sorts the list alphabetically: first up, second dirs, third files
        QuickSorter.sort(dirList);
        
        return new DefaultListModel(dirList);
    }

    private boolean isDirectory(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("filename null");
        }
        return filename.endsWith(SEPARATOR);
    }
    
    public String getFileEndingFilter() {
        return fileEndingFilter;
    }
    
    public void setDirectory(String dir) {
        this.currentDir = dir;
    }
    
    public void setFileEndingFilter(String fileEndingFilter) {
        this.fileEndingFilter = fileEndingFilter;
    }
    
    public void setDirectoriesOnly(boolean directoriesOnly) {
        this.directoriesOnly = directoriesOnly;
    }

    public boolean isDirectoriesOnly() {
        return directoriesOnly;
    }
    
    public void show() {
        try {
            dirList.setModel(getDirModel());
            super.show();
        } catch (Exception e) {
            Log.p("Error loading directory entries for [" + (currentDir == null ? "ROOT" : currentDir) + "] - " + e.toString(), Log.ERROR);
            callback.errorOccured(e);
            return;
        }
    }
    
    abstract static class DirEntry implements org.sperle.keepass.util.Comparable {
        final private String name;

        public DirEntry(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            DirEntry other = (DirEntry) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        public String toString() {
            return name;
        }
    }

    static class File extends DirEntry {
        public File(String name) {
            super(name);
        }

        // directories come first then files alphabetically
        public int compareTo(Object obj) {
            return (obj instanceof Directory ? 1 : getName().compareTo(((DirEntry)obj).getName()));
        }
    }

    static class Directory extends DirEntry {
        public Directory(String name) {
            super(name);
        }

        // up directory comes first, files last, other directories alphabetically
        public int compareTo(Object obj) {
            return (obj instanceof UpDirectory ? 1 : (obj instanceof File ? -1 : getName().compareTo(((DirEntry)obj).getName())));
        }
    }

    static class UpDirectory extends Directory {
        public UpDirectory() {
            super("..");
        }
        
        // up directory comes first
        public int compareTo(Object obj) {
            return -1;
        }
    }
    
    public static interface FileChooserCallback {
        void canceled();
        void choosen(String filename);
        void errorOccured(Exception e);
    }
}
