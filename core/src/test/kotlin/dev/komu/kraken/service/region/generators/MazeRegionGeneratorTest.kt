package dev.komu.kraken.service.region.generators

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.GameConfiguration
import dev.komu.kraken.model.common.Console
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.region.World
import dev.komu.kraken.model.region.generators.MazeRegionGenerator
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

        override fun selectDirection(): dev.komu.kraken.common.Direction? = null
    }
}
