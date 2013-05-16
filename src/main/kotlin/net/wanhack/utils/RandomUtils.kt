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

import java.util.Collections
import java.util.Random
import net.wanhack.utils.collections.randomElement

object RandomUtils {

    private val random = Random()

    fun randomEnum<T: Enum<T>>(cl: Class<T>): T =
        cl.getEnumConstants()!!.toList().randomElement()

    fun randomItem<T>(vararg items: T): T =
        items.randomElement()

    fun rollDie(sides: Int, times: Int = 1): Int {
        var total = 0

        times.times {
            total += 1 + random.nextInt(sides)
        }

        return total
    }

    fun shuffle(list: MutableList<*>) {
        Collections.shuffle(list, random)
    }

    fun randomInt(n: Int): Int = random.nextInt(n)
    fun randomInt(min: Int, max:Int) = min + random.nextInt(max - min + 1)
}
