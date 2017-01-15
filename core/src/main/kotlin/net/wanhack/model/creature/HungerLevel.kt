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

package net.wanhack.model.creature

enum class HungerLevel(val min: Int) {
    SATIATED(2001),
    NOT_HUNGRY(301),
    HUNGRY(101),
    WEAK(1),
    FAINTING(0);

    val hungry: Boolean
        get() = min <= HUNGRY.min

    override fun toString(): String =
        if (this == NOT_HUNGRY)
            ""
        else
            name.toLowerCase()
}

fun getHungerLevel(level: Int): HungerLevel {
    for (hunger in HungerLevel.values())
        if (level >= hunger.min)
            return hunger

    return HungerLevel.FAINTING
}
