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

package dev.komu.kraken.utils.exp

import java.util.Collections.emptyMap
import java.util.Collections.singletonMap

abstract class Expression {

    fun evaluate(): Int =
        evaluate(emptyMap<String, Int>())

    fun evaluate(name: String, value: Int): Int =
        evaluate(singletonMap(name, value))

    abstract fun evaluate(env: Map<String, Int>): Int

    companion object {
        fun evaluate(exp: String): Int =
            parse(exp).evaluate()

        fun evaluate(exp: String, name: String, value: Int): Int =
            parse(exp).evaluate(name, value)

        fun evaluate(exp: String, env: Map<String, Int>): Int =
            parse(exp).evaluate(env)

        fun parse(exp: String): Expression =
            ExpressionParser(exp).parse()

        fun constant(value: Int): Expression =
            ConstantExpression(value)
    }
}
