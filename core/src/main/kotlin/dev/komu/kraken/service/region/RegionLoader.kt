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

package dev.komu.kraken.service.region

import dev.komu.kraken.model.region.*
import dev.komu.kraken.service.resources.ResourceLoader
import dev.komu.kraken.utils.logger
import dev.komu.kraken.utils.maxLength
import java.util.*

class RegionLoader(val world: World) {

    fun loadRegion(info: RegionInfo): Region {
        val lines = ArrayList<String>()
        val directives = ArrayList<Directive>()

        for (line in ResourceLoader.readLines("/regions/${info.id}.region")) {
            if (!line.startsWith(";") && (line != "" || directives.isEmpty())) {
                if (line.startsWith(":")) {
                    directives.add(parseDirective(line))
                } else {
                    lines.add(line)
                }
            }
        }

        val region = Region(world, info.id, info.level, lines.maxLength() + 1, lines.size + 1)

        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, ch ->
                val cell = region[x, y]
                when (ch) {
                    '#'  -> cell.setType(CellType.HALLWAY_FLOOR)
                    '<'  -> cell.setType(CellType.STAIRS_UP)
                    '>'  -> cell.setType(CellType.STAIRS_DOWN)
                    ' '  -> { }
                    else -> log.severe("unknown tile: $ch")
                }
            }
        }

        val regionAliases = HashMap<String, String>()
        val next = info.next
        if (next != null)
            regionAliases["%next"] = next.id

        val previous = info.previous
        if (previous != null)
            regionAliases["%previous"] = previous.id

        for (directive in directives)
            processDirective(directive, region, regionAliases)

        return region
    }

    private fun processDirective(d: Directive, region: Region, regionAliases: Map<String, String>) {
        val objectFactory = world.game.objectFactory
        when (d) {
            is StartDirective    -> region.addStartPoint(d.c, d.name)
            is LightDirective    -> region[d.c].lightPower = d.power
            is CreatureDirective -> region.addCreature(d.c, objectFactory.createCreature(d.name))
            is ItemDirective     -> region.addItem(d.c, objectFactory.createItem(d.name))
            is PortalDirective   -> region.addPortal(d.c, getRegion(d.target, regionAliases), d.location, d.up)
            else                 -> log.severe("unknown directive: $d")
        }
    }

    private fun parseDirective(line: String): Directive {
        val tokens1 = CREATURE_PATTERN.getTokens(line)
        if (tokens1 != null)
            return CreatureDirective(Coordinate(tokens1[0].toInt(), tokens1[1].toInt()), tokens1[2])

        val tokens2 = ITEM_PATTERN.getTokens(line)
        if (tokens2 != null)
            return ItemDirective(Coordinate(tokens2[0].toInt(), tokens2[1].toInt()), tokens2[2])

        val tokens3 = DOWN_PATTERN.getTokens(line)
        if (tokens3 != null)
            return PortalDirective(Coordinate(tokens3[0].toInt(), tokens3[1].toInt()), tokens3[2], tokens3[3], false)

        val tokens4 = UP_PATTERN.getTokens(line)
        if (tokens4 != null)
            return PortalDirective(Coordinate(tokens4[0].toInt(), tokens4[1].toInt()), tokens4[2], tokens4[3], true)

        val tokens6 = START_PATTERN.getTokens(line)
        if (tokens6 != null)
            return StartDirective(Coordinate(tokens6[1].toInt(), tokens6[2].toInt()), tokens6[0])

        val tokens7 = LIGHT_PATTERN.getTokens(line)
        if (tokens7 != null)
            return LightDirective(Coordinate(tokens7[0].toInt(), tokens7[1].toInt()), tokens7[2].toInt())

        throw Exception("invalid directive: $line")
    }

    abstract class Directive
    class StartDirective(val c: Coordinate, val name: String) : Directive()
    class LightDirective(val c: Coordinate, val power: Int) : Directive()
    class CreatureDirective(val c: Coordinate, val name: String) : Directive()
    class ItemDirective(val c: Coordinate, val name: String) : Directive()
    class PortalDirective(val c: Coordinate, val target: String, val location: String, val up: Boolean) : Directive()

    companion object {
        private val START_PATTERN    = DirectivePattern(":start [str] [int],[int]")
        private val CREATURE_PATTERN = DirectivePattern(":creature [int],[int] [str]")
        private val ITEM_PATTERN     = DirectivePattern(":item [int],[int] [str]")
        private val DOWN_PATTERN     = DirectivePattern(":portal down [int],[int] [str] [str]")
        private val UP_PATTERN       = DirectivePattern(":portal up [int],[int] [str] [str]")
        private val LIGHT_PATTERN    = DirectivePattern(":light [int],[int] [int]")
        private val log = RegionLoader::class.java.logger()

        private fun getRegion(name: String, aliases: Map<String, String>): String =
            aliases[name] ?: name
    }
}
