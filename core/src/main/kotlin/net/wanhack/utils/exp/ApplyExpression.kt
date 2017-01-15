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

package net.wanhack.utils.exp

class ApplyExpression(val function: String, val args: List<Expression>): Expression() {

    override fun evaluate(env: Map<String, Int>): Int {
        val func = Functions.findFunction(function, args.size) ?: throw EvaluationException("No such function: $function/${args.size}")
        return func(args.map { it.evaluate(env) })
    }

    override fun toString() =
        function + args.joinToString(", ", "(", ")")
}
