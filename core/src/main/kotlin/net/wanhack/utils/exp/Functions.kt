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

import java.util.HashMap
import net.wanhack.utils.RandomUtils

object Functions {

    val functions = HashMap<Pair<String,Int>, (List<Int>) -> Int>();

    {
        functions["abs" to 1] = { args -> Math.abs(args[0]) }
        functions["max" to 2] = { args -> Math.max(args[0], args[1]) }
        functions["min" to 2] = { args -> Math.min(args[0], args[1]) }
        functions["randint" to 1] = { args -> RandomUtils.randomInt(args[0]) }
        functions["randint" to 2] = { args -> RandomUtils.randomInt(args[0], args[1]) }
    }

    fun findFunction(name: String, arity: Int): ((List<Int>) -> Int)? = functions[name to arity]
}
