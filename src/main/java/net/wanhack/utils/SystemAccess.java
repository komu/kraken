/*
 *  Copyright 2005 The Wanhack Team
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

import java.security.AccessControlException;

public final class SystemAccess {

    /**
     * Returns value named system property.
     * <p>
     * If the program does not have the right to access given system
     * property (i.e. in WebStart environment), or if the property is
     * not defined, defaultValue is returned.
     */
    public static String getSystemProperty(String name, String defaultValue) {
        try {
            return System.getProperty(name, defaultValue);
        } catch (AccessControlException e) {
            return defaultValue;
        }
    }
    
    /**
     * Returns value named system property.
     * <p>
     * If the program does not have the right to access given system
     * property (i.e. in WebStart environment), null is returned.
     */
    public static String getSystemProperty(String name) {
        return getSystemProperty(name, null);
    }
    
    /** Disable instantiation */
    private SystemAccess() { }
}
