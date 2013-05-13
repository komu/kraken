/*
 *  Copyright 2006 The Wanhack Team
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.wanhack.utils;

/**
 * Predicate utilities.
 */
public final class Predicates {
    
    private Predicates() { }

    public static final <T> Predicate<T> matchAlways() {
        return new Predicate<T>() {
            public boolean evalute(T obj) {
                return true;
            }
        };
    }
    
    public static final <T> Predicate<T> matchNever() {
        return new Predicate<T>() {
            public boolean evalute(T obj) {
                return false;
            }
        };
    }
    
    public static final <T> Predicate<T> and(final Predicate<T>... predicates) {
        return new Predicate<T>() {
            public boolean evalute(T obj) {
                for (Predicate<T> p : predicates) {
                    if (!p.evalute(obj)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }
    
    public static final <T> Predicate<T> or(final Predicate<T>... predicates) {
        return new Predicate<T>() {
            public boolean evalute(T obj) {
                for (Predicate<T> p : predicates) {
                    if (p.evalute(obj)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static final <T> Predicate<T> not(final Predicate<T> predicate) {
        return new Predicate<T>() {
            public boolean evalute(T obj) {
                return !predicate.evalute(obj);
            }
        };
    }
}
