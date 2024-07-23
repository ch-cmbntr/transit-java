// Copyright 2014 Cognitect. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS-IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.cognitect.transit;

import java.io.EOFException;
import java.util.function.Predicate;

/**
 * Interface for reading values in transit format
 */
public interface Reader {
    /**
     * Reads a single value from an input source
     * @return the value
     */
    <T> T read();

    /**
     * Reads a single value from an input source
     * @param eofValue the value if EOF is reached
     * @return the value
     */
    default <T> T read(final T eofValue) {
        try {
            return read();
        } catch (final RuntimeException e) {
            if (e.getCause() instanceof EOFException) {
                return eofValue;
            }
            throw e;
        }
    }

    /**
     * Reads a single value from an input source
     * @param consumeAndContinue consumes the value and tells if the next value shall be read
     */
    default <T> void readWhile(final Predicate<? super T> consumeAndContinue) {
        @SuppressWarnings("unchecked")
        final T eof = (T) new Object();
        T val;
        boolean cont;
        do {
            val = read(eof);
            if (val == eof) {
                return;
            }
            cont = consumeAndContinue.test(eof);
        } while (cont);
    }

}
