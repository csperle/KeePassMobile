package org.sperle.keepass.ui;

import jmunit.framework.cldc11.TestSuite;

import org.sperle.keepass.ui.util.DateFormatterTest;
import org.sperle.keepass.ui.util.SimplePasswordGeneratorTest;

public class KeePassMobileTestSuite extends TestSuite {
    public KeePassMobileTestSuite() {
        super("KeePassMobileTestSuite");
        add(new DateFormatterTest());
        add(new SimplePasswordGeneratorTest());
    }
}
