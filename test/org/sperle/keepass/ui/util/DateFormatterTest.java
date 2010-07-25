package org.sperle.keepass.ui.util;

import org.sperle.keepass.kdb.KdbDate;
import org.sperle.keepass.ui.KeePassMobileTest;
import org.sperle.keepass.ui.util.DateFormatter.ParseException;

public class DateFormatterTest extends KeePassMobileTest {

    public DateFormatterTest() {
        super(2, "DateFormatterTest");
    }

    public void test(int testNumber) throws Throwable {
        switch (testNumber) {
        case 0:testFormat();break;
        case 1:testParse();break;
        default:break;
        }
    }

    public void testFormat() throws ParseException {
	assertEquals("30.04.1974", new DateFormatter("dd.MM.yyyy").format(new KdbDate(1974, 04, 30)));
	assertEquals("1983-07-01", new DateFormatter("yyyy-MM-dd").format(new KdbDate(1983, 07, 01)));
	assertEquals("30/12/2006", new DateFormatter("dd/MM/yyyy").format(new KdbDate(2006, 12, 30)));
	assertEquals("12/30/2006", new DateFormatter("MM/dd/yyyy").format(new KdbDate(2006, 12, 30)));
	assertEquals("1983-7-1", new DateFormatter("yyyy-M-d").format(new KdbDate(1983, 07, 01)));
	assertEquals("30.4.1974", new DateFormatter("d.M.yyyy").format(new KdbDate(1974, 04, 30)));
    }
    
    public void testParse() throws ParseException {
        try {
            new DateFormatter("dd.MM.yyyy").parse("");
            fail("Should throw ParseException");
        } catch (ParseException e) {
            // OK
        }
        try {
            new DateFormatter("dd.MM.yyyy").parse("         .");
            fail("Should throw ParseException");
        } catch (ParseException e) {
            // OK
        }
        assertEquals(new KdbDate(1974, 04, 30, 23, 59, 59), new DateFormatter("dd.MM.yyyy").parse("30.04.1974"));
        assertEquals(new KdbDate(1983, 07, 01, 23, 59, 59), new DateFormatter("yyyy-MM-dd").parse("1983-07-01"));
        assertEquals(new KdbDate(2006, 12, 30, 23, 59, 59), new DateFormatter("dd/MM/yyyy").parse("30/12/2006"));
        assertEquals(new KdbDate(2006, 12, 30, 23, 59, 59), new DateFormatter("MM/dd/yyyy").parse("12/30/2006"));
        assertEquals(new KdbDate(1983, 07, 01, 23, 59, 59), new DateFormatter("yyyy-MM-dd").parse("1983-7-1"));
        assertEquals(new KdbDate(1983, 07, 01, 23, 59, 59), new DateFormatter("yyyy-M-d").parse("1983-7-1"));
        assertEquals(new KdbDate(1974, 04, 30, 23, 59, 59), new DateFormatter("dd.MM.yyyy").parse("30.4.1974"));
        assertEquals(new KdbDate(1974, 04, 30, 23, 59, 59), new DateFormatter("d.M.yyyy").parse("30.4.1974"));
    }
}
