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

package dev.komu.kraken.model.skill

import dev.komu.kraken.utils.square

enum class Proficiency(val level: Int, val bonus: Int) {
    UNSKILLED(1, -2),
    BASIC(2, 0),
    SKILLED(3, 1),
    EXPERT(4, 2),
    MASTER(5, 3) ,
    GRAND_MASTER(6, 5);

    val next: Proficiency?
        get() = values().elementAtOrNull(ordinal + 1)

    val trainingToReachThisLevel = square(level - 1) * 20

    override fun toString() =
        if (this == GRAND_MASTER)
            "grand master"
        else
            name.toLowerCase()
}
