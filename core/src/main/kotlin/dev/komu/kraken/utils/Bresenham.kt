import dev.komu.kraken.model.region.Coordinate
import kotlin.math.abs

inline fun matchAllBetween(source: Coordinate, target: Coordinate, predicate: (Int, Int) -> Boolean): Boolean {
    produceCoordinatesBetween(source, target) { x, y ->
        if (!predicate(x, y))
            return false
    }

    return true
}

// see http://en.wikipedia.org/wiki/Bresenham's_line_algorithm
inline fun produceCoordinatesBetween(source: Coordinate, target: Coordinate, callback: (Int, Int) -> Unit) {
    var x0 = source.x
    var y0 = source.y
    val x1 = target.x
    val y1 = target.y

    val dx = abs(x1 - x0)
    val dy = abs(y1 - y0)

    val sx = if (x0 < x1) 1 else -1
    val sy = if (y0 < y1) 1 else -1
    var err = dx - dy

    while (true) {
        callback(x0, y0)

        if (x0 == x1 && y0 == y1)
            break

        val e2 = 2 * err
        if (e2 > -dy) {
            err -= dy
            x0 += sx
        }
        if (x0 == x1 && y0 == y1) {
            callback(x0, y0)
            break
        }
        if (e2 < dx) {
            err += dx
            y0 += sy
        }
    }
}
