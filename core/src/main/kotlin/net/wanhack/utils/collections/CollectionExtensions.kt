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

package net.wanhack.utils.collections

import java.util.ArrayList
import net.wanhack.utils.RandomUtils

fun <T> MutableCollection<T>.addAll(it: Iterator<T>) {
    for (x in it)
        add(x)
}

fun <T> Collection<T>.shuffled(): MutableList<T> {
    val result = ArrayList(this)
    RandomUtils.shuffle(result)
    return result
}

fun <T> List<T>.randomElement(): T =
    this[RandomUtils.randomInt(this.size)]

fun <T> Array<T>.randomElement(): T =
    this[RandomUtils.randomInt(this.size)]

fun <T> Collection<*>.filterByType(cl: Class<T>): List<T> {
    val result = listBuilder<T>()
    for (x in this)
        if (cl.isInstance(x))
            result.add(cl.cast(x)!!)

    return result.build()
}

fun <T : Any> Collection<T>.unique(): T? =
    if (size == 1) first() else null

fun <T : Any, C: Comparable<C>> Collection<T>.maximumBy(comparison: (T) -> C): T? {
    var maxScore: C? = null
    var largest: T? = null

    for (item in this) {
        val score = comparison(item)
        val max = maxScore
        if (max == null || score > max) {
            maxScore = score
            largest = item
        }
    }

    return largest
}

fun <T: Any> T?.toOption(): Set<T> = if (this == null) setOf<T>() else setOf<T>(this)
