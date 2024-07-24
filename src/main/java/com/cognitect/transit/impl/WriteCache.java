// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import java.util.HashMap;
import java.util.Map;

public class WriteCache {

    public static final int MIN_SIZE_CACHEABLE = 4;
    public static final int CACHE_CODE_DIGITS = 44;
    public static final int MAX_CACHE_ENTRIES = CACHE_CODE_DIGITS * CACHE_CODE_DIGITS;
    public static final int BASE_CHAR_IDX = 48;

    private static final float CACHE_LOADFACTOR = 0.75f;
    private static final int INITIAL_CACHE_CAPACITY = (int) Math.ceil(MAX_CACHE_ENTRIES / 2 / CACHE_LOADFACTOR);

    private static final char[] CODE_DIGITS;
    static {
        CODE_DIGITS = new char[CACHE_CODE_DIGITS];
        for (int i = 0; i < CACHE_CODE_DIGITS; i++) {
            CODE_DIGITS[i] = (char) (i + BASE_CHAR_IDX);
        }
    }

    private static final String[] ONE_DIGIT_CODES;
    static {
        ONE_DIGIT_CODES = new String[CACHE_CODE_DIGITS];
        for (int i = 0; i < CACHE_CODE_DIGITS; i++) {
            ONE_DIGIT_CODES[i] = new String(new char[] { Constants.SUB, CODE_DIGITS[i] });
        }
    }

    private final Map<String, String> cache;
    private final boolean enabled;
    private int index;

    public WriteCache() { this(true); }

    public WriteCache(boolean enabled) {
        index = 0;
        cache = enabled ? new HashMap<>(INITIAL_CACHE_CAPACITY, CACHE_LOADFACTOR) : null;
        this.enabled = enabled;
    }

    public static boolean isCacheable(String s, boolean asMapKey) {
        return (s.length() >= MIN_SIZE_CACHEABLE) &&
                 (asMapKey ||
                    (s.charAt(0) == Constants.ESC &&
                    (s.charAt(1) == ':' || s.charAt(1) == '$' || s.charAt(1) == '#')));
    }

    private static String indexToCode(int index) {
        if (index < CACHE_CODE_DIGITS)
            return ONE_DIGIT_CODES[index];

        int hi = index / CACHE_CODE_DIGITS;
        int lo = index % CACHE_CODE_DIGITS;
        if (hi >= CACHE_CODE_DIGITS)
            return null;

        return new String(new char[] { Constants.SUB, CODE_DIGITS[hi], CODE_DIGITS[lo] });
    }


    public String cacheWrite(String s, boolean asMapKey) {
        if (enabled && isCacheable(s, asMapKey)) {
            int indexBefore = index;
            String val = cache.computeIfAbsent(s, _k-> indexToCode(index++));
            if (index > MAX_CACHE_ENTRIES) {
                init();
                cache.put(s, indexToCode(index++));
                return s;
            }
            return indexBefore == index ? val : s;
        }
        return s;
    }

    public WriteCache init() {
        index = 0;
        if (enabled)
            cache.clear();
        return this;
    }
}
