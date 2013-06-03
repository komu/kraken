package net.wanhack.model.region

import org.junit.Test as test
import kotlin.test.*

class CoordinateTest {

    test fun adjacency() {
        assertTrue(Coordinate(5, 5).isAdjacent(Coordinate(4, 5)))
        assertTrue(Coordinate(5, 5).isAdjacent(Coordinate(6, 5)))

        assertTrue(Coordinate(5, 5).isAdjacent(Coordinate(5, 4)))
        assertTrue(Coordinate(5, 5).isAdjacent(Coordinate(5, 6)))

        assertTrue(Coordinate(5, 5).isAdjacent(Coordinate(4, 4)))
        assertTrue(Coordinate(5, 5).isAdjacent(Coordinate(4, 6)))
        assertTrue(Coordinate(5, 5).isAdjacent(Coordinate(6, 4)))
        assertTrue(Coordinate(5, 5).isAdjacent(Coordinate(6, 6)))

        assertFalse(Coordinate(5, 5).isAdjacent(Coordinate(5, 5)))

        assertFalse(Coordinate(5, 5).isAdjacent(Coordinate(5, 7)))
        assertFalse(Coordinate(5, 5).isAdjacent(Coordinate(7, 5)))
    }
}
