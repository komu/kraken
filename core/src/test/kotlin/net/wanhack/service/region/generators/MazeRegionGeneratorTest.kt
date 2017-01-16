package net.wanhack.service.region.generators

import net.wanhack.common.Direction
import net.wanhack.model.Game
import net.wanhack.model.GameConfiguration
import net.wanhack.model.common.Console
import net.wanhack.model.item.Item
import net.wanhack.model.region.World
import net.wanhack.model.region.generators.MazeRegionGenerator
import org.junit.Test
import kotlin.test.assertTrue

class MazeRegionGeneratorTest {

    val world = World(Game(GameConfiguration(), MyConsole, {} ))

    @Test
    fun generateRandomRegions() {
        repeat(1000) {
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
