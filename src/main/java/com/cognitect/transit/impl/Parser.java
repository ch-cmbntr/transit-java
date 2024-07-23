// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.ArrayReadHandler;
import com.cognitect.transit.MapReadHandler;

import java.io.EOFException;
import java.io.IOException;

public interface Parser {

    default Object parse(ReadCache cache, Object eofValue) throws IOException {
        try {
            return parse(cache);
        } catch (@SuppressWarnings("unused") EOFException eof) {
            return eofValue;
        } catch (final RuntimeException e) {
            if (e.getCause() instanceof EOFException) {
                return eofValue;
            }
            throw e;
        }
    }

    Object parse(ReadCache cache) throws IOException;
    Object parseVal(boolean asMapKey, ReadCache cache) throws IOException;
    Object parseMap(boolean asMapKey, ReadCache cache, MapReadHandler<Object, ?, Object, Object, ?> handler) throws IOException;
    Object parseArray(boolean asMapKey, ReadCache cache, ArrayReadHandler<Object, ?, Object, ?> handler) throws IOException;
}
