package dev.komu.kraken.model.region.loader

import dev.komu.kraken.model.region.*
import dev.komu.kraken.utils.ResourceLoader
import dev.komu.kraken.utils.logger
import dev.komu.kraken.utils.maxLength
import java.util.*

class RegionLoader(private val world: World) {

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
                    '#' -> cell.type = CellType.HALLWAY_FLOOR
                    '<' -> cell.type = CellType.STAIRS_UP
                    '>' -> cell.type = CellType.STAIRS_DOWN
                    ' ' -> {
                    }
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
            is Directive.Start -> region.addStartPoint(d.c, d.name)
            is Directive.Light -> region[d.c].lightPower = d.power
            is Directive.Creature -> region.addCreature(d.c, objectFactory.createCreature(d.name))
            is Directive.Item -> region.addItem(d.c, objectFactory.createItem(d.name))
            is Directive.Portal -> region.addPortal(d.c, getRegion(d.target, regionAliases), d.location, d.up)
        }
    }

    private fun parseDirective(line: String): Directive {
        CREATURE_PATTERN.getTokens(line)?.let { ts ->
            return Directive.Creature(Coordinate(ts[0].toInt(), ts[1].toInt()), ts[2])
        }

        ITEM_PATTERN.getTokens(line)?.let { ts ->
            return Directive.Item(Coordinate(ts[0].toInt(), ts[1].toInt()), ts[2])
        }

        DOWN_PATTERN.getTokens(line)?.let { ts ->
            return Directive.Portal(Coordinate(ts[0].toInt(), ts[1].toInt()), ts[2], ts[3], false)
        }

        UP_PATTERN.getTokens(line)?.let { ts ->
            return Directive.Portal(Coordinate(ts[0].toInt(), ts[1].toInt()), ts[2], ts[3], true)
        }

        START_PATTERN.getTokens(line)?.let { ts ->
            return Directive.Start(Coordinate(ts[1].toInt(), ts[2].toInt()), ts[0])
        }

        LIGHT_PATTERN.getTokens(line)?.let { ts ->
            return Directive.Light(Coordinate(ts[0].toInt(), ts[1].toInt()), ts[2].toInt())
        }

        error("invalid directive: $line")
    }

    sealed class Directive {
        class Start(val c: Coordinate, val name: String) : Directive()
        class Light(val c: Coordinate, val power: Int) : Directive()
        class Creature(val c: Coordinate, val name: String) : Directive()
        class Item(val c: Coordinate, val name: String) : Directive()
        class Portal(val c: Coordinate, val target: String, val location: String, val up: Boolean) : Directive()
    }

    companion object {
        private val START_PATTERN = DirectivePattern(":start [str] [int],[int]")
        private val CREATURE_PATTERN = DirectivePattern(":creature [int],[int] [str]")
        private val ITEM_PATTERN = DirectivePattern(":item [int],[int] [str]")
        private val DOWN_PATTERN = DirectivePattern(":portal down [int],[int] [str] [str]")
        private val UP_PATTERN = DirectivePattern(":portal up [int],[int] [str] [str]")
        private val LIGHT_PATTERN = DirectivePattern(":light [int],[int] [int]")
        private val log = RegionLoader::class.java.logger()

        private fun getRegion(name: String, aliases: Map<String, String>): String =
            aliases[name] ?: name
    }
}
