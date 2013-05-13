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

package net.wanhack.model.skill

import net.wanhack.utils.square

enum class Proficiency(val level: Int, val bonus: Int) {
    UNSKILLED : Proficiency(1, -2)
    BASIC : Proficiency(2, 0)
    SKILLED : Proficiency(3, 1)
    EXPERT : Proficiency(4, 2)
    MASTER : Proficiency(5, 3)
    GRAND_MASTER : Proficiency(6, 5)

    val next: Proficiency?
        get() =
            if (ordinal() + 1 < values().size)
                values()[ordinal() + 1]
            else
                null

    val trainingToReachThisLevel = square(level - 1) * 20

    fun toString() =
        if (this == GRAND_MASTER)
            "grand master"
        else
            name().toLowerCase()
}
