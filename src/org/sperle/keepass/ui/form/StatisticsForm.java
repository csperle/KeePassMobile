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

import org.sperle.keepass.kdb.KeePassDatabase;
import org.sperle.keepass.kdb.PerformanceStatistics;
import org.sperle.keepass.ui.i18n.Messages;

import com.sun.lwuit.Dialog;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.util.Log;

public class StatisticsForm extends KeePassMobileForm {
    private KeePassDatabase kdb;
    
    private static StatisticsForm instance;
    public static StatisticsForm create(final KeePassDatabase kdb) {
        if(instance == null || instance.kdb != kdb) { // return same statistics form for same database
            instance = new StatisticsForm(kdb);
        }
        return instance;
    }
    
    private StatisticsForm(final KeePassDatabase kdb) {
        super(Messages.get("stats") + " " + kdb.getDatabaseName());
        this.kdb = kdb;
        
        setLayout(new BorderLayout());
        setScrollable(false);
        
        TextArea stats = new TextArea("", 5, 20);
        stats.setEditable(false);
        stats.getSelectedStyle().setBgColor(0x6600cc);
        
        StringBuffer buf = new StringBuffer("");
        PerformanceStatistics ps = kdb.getPerformanceStatistics();
        try {
            if(ps != null) {
                buf.append(Messages.get("stats_lt") + " " + (ps.getLoadTime() / 1000) + "s\r\n\r\n");
                
                buf.append(Messages.get("stats_mker") + " " + kdb.getNumKeyEncRounds() + "\r\n");
                buf.append(Messages.get("stats_mket") + " " + (ps.getMasterKeyEncryptionTime() / 1000) + "s\r\n");
                long encryptionPerformance = ps.getMasterKeyEncryptionTime() * 10 / kdb.getNumKeyEncRounds();
                buf.append(Messages.get("stats_mkep") + " " + (encryptionPerformance / 10) + "."  + (encryptionPerformance - (encryptionPerformance / 10) * 10) + "ms/round\r\n\r\n");
                
                long decryptionPerformance = 0;
                if(ps.getEncryptedContentDataLength() >= 1024) {
                    buf.append(Messages.get("stats_ecdl") + " " + (ps.getEncryptedContentDataLength() / 1024) + "kb\r\n");
                    buf.append(Messages.get("stats_dt")   + " " + (ps.getDecryptionTime() / 1000) + "s\r\n");
                    decryptionPerformance = ps.getDecryptionTime() * 10 / (ps.getEncryptedContentDataLength() / 1024);
                    buf.append(Messages.get("stats_dp")   + " " + (decryptionPerformance / 10) + "."  + (decryptionPerformance - (decryptionPerformance / 10) * 10) + "ms/kb\r\n\r\n");
                }
                
                long hashPerformance = 0;
                if(ps.getPlainContentDataLength() >= 1024) {
                    buf.append(Messages.get("stats_pcdl") + " " + (ps.getPlainContentDataLength() / 1024) + "kb\r\n");
                    buf.append(Messages.get("stats_chct") + " " + (ps.getContentHashCalculationTime() / 1000) + "s\r\n");
                    hashPerformance = ps.getContentHashCalculationTime() * 10 / (ps.getPlainContentDataLength() / 1024);
                    buf.append(Messages.get("stats_hp")   + " " + (hashPerformance / 10) + "."  + (hashPerformance - (hashPerformance / 10) * 10) + "ms/kb\r\n\r\n");
                }
                
                buf.append(Messages.get("stats_ne")   + " " + kdb.getNumEntries() + "\r\n");
                buf.append(Messages.get("stats_cet")  + " " + (ps.getContentExtractionTime() / 1000) + "s\r\n");
                long extractionPerformance = ps.getContentExtractionTime() * 10 / kdb.getNumEntries();
                buf.append(Messages.get("stats_ep")   + " " + (extractionPerformance / 10) + "."  + (extractionPerformance - (extractionPerformance / 10) * 10) + "ms/entry\r\n\r\n");
                
                long performanceSum = encryptionPerformance + decryptionPerformance + hashPerformance + extractionPerformance;
                buf.append(Messages.get("stats_mp")   + " " + (performanceSum > 0 ? (""+(600000 / performanceSum)) : "MAX!") + "\r\n\r\n"); // KeePassMobile Performance Rating (VM Speed: 1000bytecodes/ms -> Result: ~100)
            } else {
                buf.append(Messages.get("stats_na") + "\r\n\r\n");
            }
            
            int freeMem  = (int)Runtime.getRuntime().freeMemory()/1024;
            int totalMem = (int)Runtime.getRuntime().totalMemory()/1024;
            int usedMem  = totalMem - freeMem;
            int usedPerc = usedMem*100/totalMem;
            buf.append(Messages.get("stats_mem")  + " " + usedMem + "kb/" + totalMem + "kb (" + usedPerc + "%)\r\n");
        } catch (Exception e) {
            Log.p("Error occured calculating statistics - " + e.toString(), Log.ERROR);
            Dialog.show(Messages.get("stats_error"), Messages.get("stats_error_text"), Messages.get("ok"), null);
        }
        
        stats.setText(buf.toString());
        addComponent(BorderLayout.CENTER, stats);
        updateCommands();
    }
}
