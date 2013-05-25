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

package net.wanhack.model.common

import java.lang.Math.max

data class Color(val r: Int, val g: Int, val b: Int) {

    val FACTOR = 0.7

    fun darker(): Color =
        Color(max((0.7*r).toInt(), 0),
              max((0.7*g).toInt(), 0),
              max((0.7*b).toInt(), 0))

    class object {
        val ALUMINIUM       = Color(220, 230, 250)
        val BLACK           = Color(0, 0, 0)
        val BLACKISH        = Color(10, 10, 10)
        val BLUE            = Color(0, 0, 255)
        val BROWN           = Color(100, 100, 0)
        val BROWNISH        = Color(120, 100, 10)
        val CYAN            = Color(0, 255, 255)
        val DARK_GRAY       = Color(64, 64, 64)
        val DARK_GREEN      = Color(0, 130, 0)
        val GRAY            = Color(128, 128, 128)
        val GREEN           = Color(0, 150, 0)
        val LIGHT_BLUE      = Color(100, 100, 255)
        val LIGHT_BROWN     = Color(200, 200, 0)
        val RED             = Color(255, 0, 0)
        val WHITE           = Color(255, 255, 255)
        val WHITEISH        = Color(240, 240, 230)
        val YELLOW          = Color(255, 255, 0)
        val YELLOWISH       = Color(250, 240, 140)
    }
}
