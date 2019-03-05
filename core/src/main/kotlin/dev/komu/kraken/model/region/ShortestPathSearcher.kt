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

package dev.komu.kraken.model.region

import java.util.*

open class ShortestPathSearcher(val region: Region) {

    open val allowSubDirections = true

    fun findFirstCellOnShortestPath(start: Cell, goal: Cell): Cell? =
        findShortestPath(start, goal)?.drop(1)?.firstOrNull()

    fun findShortestPath(start: Cell, goal: Cell): Iterable<Cell>? {
        val openHeap = PriorityQueue<Node>()
        val openMap = CellMap<Node>(region)
        val closedMap = CellMap<Node>(region)
        val startNode = Node(goal, null, 0)

        startNode.heuristic = estimateCost(goal, start)
        openHeap.add(startNode)

        while (!openHeap.isEmpty()) {
            val current = openHeap.remove()
            if (current.cell == start)
                return current

            for (successor in current.successors(allowSubDirections)) {
                val closedNode = closedMap[successor.cell]
                if (closedNode != null) {
                    if (closedNode.cost <= successor.cost) {
                        continue
                    } else {
                        closedMap.remove(successor.cell)
                    }
                }

                successor.parent = current
                successor.heuristic = successor.cost + estimateCost(successor.cell, goal)

                val openNode = openMap[successor.cell]
                if (openNode == null || openNode.heuristic > successor.heuristic) {
                    openHeap.offer(successor)
                    openMap[successor.cell] = successor
                }
            }

            closedMap[current.cell] = current
        }

        return null
    }

    protected open fun estimateCost(from: Cell, target: Cell): Int = from.distance(target)

    protected open fun costToEnter(cell: Cell): Int = 1

    protected open fun canEnter(cell: Cell): Boolean = cell.isPassable

    private inner class Node(val cell: Cell, var parent: Node?, val cost: Int): Comparable<Node>, Iterable<Cell> {
        var heuristic = 0

        override fun iterator() = object : AbstractIterator<Cell>() {
            var next: Node? = this@Node

            override fun computeNext() {
                val node = next
                if (node != null) {
                    setNext(node.cell)
                    next = node.parent
                } else {
                    done()
                }
            }
        }

        fun successors(allowSubDirections: Boolean): Sequence<Node> {
            val adjacentCells = if (allowSubDirections) cell.adjacentCells else cell.adjacentCellsInMainDirections
            val enterable = adjacentCells.asSequence().filter { it != parent?.cell && canEnter(it) }

            return enterable.map { Node(it, this, cost + costToEnter(it)) }
        }

        override fun compareTo(other: Node) = heuristic - other.heuristic

        override fun toString() = cell.toString()
    }
}
