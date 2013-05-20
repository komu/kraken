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

package net.wanhack.service.region

import java.util.regex.Pattern

class DirectivePattern(pattern: String) {

    val pattern = Pattern.compile(pattern.replace("[int]", "(\\d+)").replace("[str]", "\"([^\"]+)\"").replaceAll("\\ +", "\\\\s+"))

    fun getTokens(str: String): Array<String>? {
        val m = pattern.matcher(str)
        return if (m.matches())
            Array<String>(m.groupCount()) { i -> m.group(i+1)!! }
        else
            return null
    }
}
