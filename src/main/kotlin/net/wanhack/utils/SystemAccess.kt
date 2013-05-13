/*
 * Copyright 2013 The Wanhack Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wanhack.utils

import java.security.AccessControlException

object SystemAccess {

    /**
     * Returns value named system property.
     *
     * If the program does not have the right to access given system property (i.e. in WebStart environment),
     * or if the property is not defined, defaultValue is returned.
     */
    fun getSystemProperty(name: String, defaultValue: String?): String? =
        try {
            System.getProperty(name, defaultValue)
        } catch (e: AccessControlException) {
            defaultValue
        }

    /**
     * Returns value named system property.
     *
     * If the program does not have the right to access given system property (i.e. in WebStart environment),
     * null is returned.
     */
    fun getSystemProperty(name: String): String? =
        getSystemProperty(name, null)
}
