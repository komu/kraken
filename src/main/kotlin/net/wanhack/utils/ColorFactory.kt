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
import org.apache.commons.logging.LogFactory

object ColorFactory {

    private val log = LogFactory.getLog(javaClass)!!

    private val colorMap = mapOf(
        "black"         to Color.BLACK,
        "blackish"      to Color(10, 10, 10),
        "blue"          to Color.BLUE,
        "light blue"    to Color(100, 100, 255),
        "brown"         to Color(100, 100, 0),
        "brownish"      to Color(120, 100, 10),
        "light brown"   to Color(200, 200, 0),
        "cyan"          to Color.CYAN,
        "dark gray"     to Color.DARK_GRAY,
        "dark green"    to Color(0, 130, 0),
        "dark grey"     to Color.DARK_GRAY,
        "gray"          to Color.GRAY,
        "grey"          to Color.GRAY,
        "green"         to Color(0, 150, 0),
        "light gray"    to Color.LIGHT_GRAY,
        "light grey"    to Color.LIGHT_GRAY,
        "magenta"       to Color.MAGENTA,
        "orange"        to Color.ORANGE,
        "pink"          to Color.PINK,
        "red"           to Color.RED,
        "aluminium"     to Color(220, 230, 250),
        "white"         to Color.WHITE,
        "whiteish"      to Color(240, 240, 230),
        "yellow"        to Color.YELLOW,
        "yellowish"     to Color(250, 240, 140))

    fun getColor(exp: String): Color =
        colorMap.getOrElse(exp) {
            log.error("unknown color: <$exp>")
            Color.BLACK
        }
}
