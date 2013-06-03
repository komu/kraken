package net.wanhack.service.region.generators

import org.junit.Test as test
import kotlin.test.*
import net.wanhack.model.region.World
import net.wanhack.model.Game
import net.wanhack.model.GameConfiguration
import net.wanhack.model.common.Console
import net.wanhack.model.item.Item
import net.wanhack.model.common.Direction

class MazeRegionGeneratorTest {

    val world = World(Game(GameConfiguration(), MyConsole, {} ))

    test fun generateRandomRegions() {
        1000 times {
            val region = MazeRegionGenerator.generate(world, "foo", 20, "bar", "baz")
            assertTrue(region.isSurroundedByUndiggableWalls())
        }
    }

    object MyConsole : Console {

        override fun message(message: String) { }

        override fun ask(question: String) = true

        override fun <T: Item> selectItem(message: String, items: Collection<T>) = null

        override fun <T: Item> selectItems(message: String, items: Collection<T>) = setOf<T>()

        override fun selectDirection(): Direction? = null
    }
}
