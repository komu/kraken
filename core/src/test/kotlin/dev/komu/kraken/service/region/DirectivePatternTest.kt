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
