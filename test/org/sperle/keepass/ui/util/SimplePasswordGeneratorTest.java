package org.sperle.keepass.ui.util;

import org.sperle.keepass.TestRandom;
import org.sperle.keepass.ui.KeePassMobileTest;
import org.sperle.keepass.ui.util.DateFormatter.ParseException;

public class SimplePasswordGeneratorTest extends KeePassMobileTest {

    private TestRandom rand;
    private SimplePasswordGenerator spg;
    
    public SimplePasswordGeneratorTest() {
        super(1, "SimplePasswordGeneratorTest");
    }

    public void test(int testNumber) throws Throwable {
        switch (testNumber) {
        case 0:testGeneratePassword();break;
        default:break;
        }
    }

    public void setUp() throws Exception {
        rand = new TestRandom();
        spg = new SimplePasswordGenerator(rand);
    }
    
    public void testGeneratePassword() throws ParseException {
        rand.setRandomInt(new int[]{0,1,2});
	assertEquals("abc", spg.generatePassword(3, true, false, false, false));
	rand.setRandomInt(new int[]{0,23,46,54});
        assertEquals("aA2!", spg.generatePassword(3, true, true, true, true));
        rand.setRandomInt(new int[]{0,23});
        assertEquals("A!", spg.generatePassword(3, false, true, false, true));
    }
}
