package org.sperle.keepass.ui.util;

import org.sperle.keepass.rand.Random;

public class SimplePasswordGenerator {
    private static final char[] LOW_CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    private static final char[] CAP_CHARS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    private static final char[] NUM_CHARS = { '2', '3', '4', '5', '6', '7', '8', '9' };
    private static final char[] SPEC_CHARS = { '!', '?', '$', '%', '&', '#', '-', '+', '_' };

    private Random rand;

    public SimplePasswordGenerator(Random rand) {
        this.rand = rand;
    }

    public String generatePassword(int passLength) {
        return generatePassword(passLength, true, true, true, true);
    }
    
    public String generatePassword(int passLength, boolean low, boolean cap, boolean num, boolean spec) {
        if (!low && !cap && !num && !spec) {
            throw new IllegalArgumentException("at least one character set must be allowed to generate password");
        }

        char[] allowedChars = getAllowedChars(low, cap, num, spec);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < passLength; i++) {
            sb.append(allowedChars[rand.nextInt(allowedChars.length)]);
        }
        return sb.toString();
    }

    private char[] getAllowedChars(boolean low, boolean cap, boolean num, boolean spec) {
        char[] allowedChars = new char[(low ? LOW_CHARS.length : 0) + (cap ? CAP_CHARS.length : 0)
                + (num ? NUM_CHARS.length : 0) + (spec ? SPEC_CHARS.length : 0)];
        int start = 0;
        if (low) {
            System.arraycopy(LOW_CHARS, 0, allowedChars, start, LOW_CHARS.length);
            start += LOW_CHARS.length;
        }
        if (cap) {
            System.arraycopy(CAP_CHARS, 0, allowedChars, start, CAP_CHARS.length);
            start += CAP_CHARS.length;
        }
        if (num) {
            System.arraycopy(NUM_CHARS, 0, allowedChars, start, NUM_CHARS.length);
            start += NUM_CHARS.length;
        }
        if (spec) {
            System.arraycopy(SPEC_CHARS, 0, allowedChars, start, SPEC_CHARS.length);
            start += SPEC_CHARS.length;
        }
        return allowedChars;
    }
}
