/*
 * Copyright 2013 The Releasers of Kraken
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

package dev.komu.kraken.service.region

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DirectivePatternTest {
    @Test fun testNoMatch() {
        assertNoMatch("foo [int] [str]", "foo bar baz")
    }

    @Test fun testParseSimpleTokens() {
        assertMatch("foo [str] [str]", "foo \"bar\" \"baz\"", "bar", "baz")
    }

    companion object {
        fun assertNoMatch(pattern: String, line: String) {
            val directivePattern = DirectivePattern(pattern)
            assertNull(directivePattern.getTokens(line))
        }

        fun assertMatch(pattern: String, line: String, vararg expected: Any)  {
            val directivePattern = DirectivePattern(pattern)
            val tokens = directivePattern.getTokens(line)

            assertEquals(expected.toList(), tokens!!.toList())
        }
    }
}
