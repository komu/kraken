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

import java.util.*

private val random = Random()

inline fun <reified T: Enum<T>> randomEnum(): T =
    T::class.java.enumConstants.toList().randomElement()

fun <T> randomItem(vararg items: T): T =
    items.randomElement()

fun rollDie(sides: Int, times: Int = 1): Int {
    var total = 0

    repeat(times) {
        total += 1 + random.nextInt(sides)
    }

    return total
}

fun randomInt(n: Int): Int = random.nextInt(n)
fun randomInt(min: Int, max:Int) = min + random.nextInt(max - min + 1)

fun MutableList<*>.shuffle() {
    Collections.shuffle(this, random)
}

fun <T> Collection<T>.shuffled(): MutableList<T> {
    val result = this.toMutableList()
    result.shuffle()
    return result
}

fun <T> List<T>.randomElement(): T =
        this[randomInt(this.size)]

fun <T> Array<T>.randomElement(): T =
        this[randomInt(this.size)]
