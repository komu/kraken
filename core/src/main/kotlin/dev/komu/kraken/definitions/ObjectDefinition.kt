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

package dev.komu.kraken.definitions

import dev.komu.kraken.utils.randomInt

abstract class ObjectDefinition<out T> {
    abstract val level: Int?
    var probability = 100

    abstract fun create(): T
}

fun <T : ObjectDefinition<*>> Collection<T>.weightedRandom(): T {
    val probabilitySum = sumBy { it.probability }

    var item = randomInt(probabilitySum)
    for (dp in this) {
        if (item < dp.probability)
            return dp

        item -= dp.probability
    }

    error("could not randomize definition")
}

fun <T : ObjectDefinition<*>> Collection<T>.betweenLevels(minLevel: Int, maxLevel: Int): List<T> =
    filter { d->
        val level = d.level
        level == null || (level >= minLevel && level <= maxLevel)
    }

