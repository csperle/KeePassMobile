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

import org.sperle.keepass.kdb.SearchOptions;
import org.sperle.keepass.ui.form.KeePassMobileForm;
import org.sperle.keepass.ui.i18n.Messages;

import com.sun.lwuit.CheckBox;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.layouts.BoxLayout;

public class SearchOptionsForm extends KeePassMobileForm {
    private final SearchForm searchForm;
    
    private Label maxResultsLabel;
    private TextArea maxResultsField;
    private CheckBox backupCB;
    
    private Label fieldsLabel;
    private CheckBox usernameCB;
    private CheckBox titleCB;
    private CheckBox urlCB;
    private CheckBox notesCB;
    private CheckBox attachmentCB;
    
    public SearchOptionsForm(final SearchForm searchForm, SearchOptions defaultOptions) {
        super(Messages.get("searchoptions"));
        this.searchForm = searchForm;
        
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setScrollableY(true);
        
        maxResultsLabel = new Label(Messages.get("searchoptions_maxresults"));
        addComponent(maxResultsLabel);
        maxResultsField = new TextArea(""+ defaultOptions.searchResultsMax);
        maxResultsField.setConstraint(TextArea.NUMERIC);
        addComponent(maxResultsField);
        backupCB = new CheckBox(Messages.get("searchoptions_backup"));
        backupCB.setSelected(defaultOptions.searchBackupFolder);
        addComponent(backupCB);

        fieldsLabel = new Label(Messages.get("searchoptions_fields"));
        addComponent(fieldsLabel);
        usernameCB = new CheckBox(Messages.get("searchoptions_username"));
        usernameCB.setSelected(defaultOptions.searchUsername);
        addComponent(usernameCB);
        titleCB = new CheckBox(Messages.get("searchoptions_title"));
        titleCB.setSelected(defaultOptions.searchTitle);
        addComponent(titleCB);
        urlCB = new CheckBox(Messages.get("searchoptions_url"));
        urlCB.setSelected(defaultOptions.searchUrl);
        addComponent(urlCB);
        notesCB = new CheckBox(Messages.get("searchoptions_notes"));
        notesCB.setSelected(defaultOptions.searchNotes);
        addComponent(notesCB);
        attachmentCB = new CheckBox(Messages.get("searchoptions_attachment"));
        attachmentCB.setSelected(defaultOptions.searchBinaryDescription);
        addComponent(attachmentCB);
        updateCommands();
    }
    
    protected void goBack() {
        this.searchForm.show();
    }
    
    public SearchOptions getSearchOptions() {
        SearchOptions options = new SearchOptions();
        options.searchResultsMax = getMaxSearchResults();
        options.searchBackupFolder = backupCB.isSelected();
        options.searchUsername = usernameCB.isSelected();
        options.searchTitle = titleCB.isSelected();
        options.searchUrl = urlCB.isSelected();
        options.searchNotes = notesCB.isSelected();
        options.searchBinaryDescription = attachmentCB.isSelected();
        return options;
    }

    private int getMaxSearchResults() {
        try {
            int max = Integer.parseInt(maxResultsField.getText());
            if(max > SearchOptions.MAX_SEARCH_RESULTS) return SearchOptions.MAX_SEARCH_RESULTS;
            else return max;
        } catch (NumberFormatException e) {
            maxResultsField.setText(""+SearchOptions.DEFAULT_SEARCH_RESULTS);
            return SearchOptions.DEFAULT_SEARCH_RESULTS;
        }
    }
}
