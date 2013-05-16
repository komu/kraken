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

import java.awt.Color
import net.wanhack.definitions.Colors

object ColorFactory {

    private val log = javaClass.logger()

    private val colorMap = mapOf(
        "black"         to Color.BLACK,
        "blackish"      to Colors.BLACKISH,
        "blue"          to Color.BLUE,
        "light blue"    to Colors.LIGHT_BLUE,
        "brown"         to Colors.BROWN,
        "brownish"      to Colors.BROWNISH,
        "light brown"   to Colors.LIGHT_BROWN,
        "cyan"          to Color.CYAN,
        "dark gray"     to Color.DARK_GRAY,
        "dark green"    to Colors.DARK_GREEN,
        "dark grey"     to Color.DARK_GRAY,
        "gray"          to Color.GRAY,
        "grey"          to Color.GRAY,
        "green"         to Colors.GREEN,
        "light gray"    to Color.LIGHT_GRAY,
        "light grey"    to Color.LIGHT_GRAY,
        "magenta"       to Color.MAGENTA,
        "orange"        to Color.ORANGE,
        "pink"          to Color.PINK,
        "red"           to Color.RED,
        "aluminium"     to Colors.ALUMINIUM,
        "white"         to Color.WHITE,
        "whiteish"      to Colors.WHITEISH,
        "yellow"        to Color.YELLOW,
        "yellowish"     to Colors.YELLOWISH)

    fun getColor(exp: String): Color =
        colorMap.getOrElse(exp) {
            log.warning("unknown color: <$exp>")
            Color.BLACK
        }
}
