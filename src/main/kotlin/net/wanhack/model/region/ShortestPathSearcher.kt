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

package net.wanhack.model.region

import java.util.*

open class ShortestPathSearcher(val region: Region) {

    open val allowSubDirections = true

    fun findFirstCellOnShortestPath(start: Cell, goal: Cell): Cell? {
        val path = findShortestPath(start, goal)
        if (path != null && path.size > 1)
            return path[1]
        else
            return null
    }

    fun findShortestPath(start: Cell, goal: Cell): List<Cell>? {
        val openHeap = PriorityQueue<Node>()
        val openMap = CellMap<Node>(region)
        val closedMap = CellMap<Node>(region)
        val startNode = Node(start, null, 0)

        startNode.heuristic = estimateCost(start, goal)
        openHeap.add(startNode)

        while (!openHeap.empty) {
            val current = openHeap.poll()!!
            if (current.cell == goal) {
                val result = ArrayList<Cell>()
                var node: Node? = current
                while (node != null) {
                    result.add(node!!.cell)
                    node = node!!.parent
                }
                Collections.reverse(result)
                return result
            }

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
                    openMap.put(successor.cell, successor)
                }
            }

            closedMap.put(current.cell, current)
        }

        return null
    }

    protected open fun estimateCost(from: Cell, target: Cell): Int = from.distance(target)

    protected open fun costToEnter(cell: Cell): Int = 1

    protected open fun canEnter(cell: Cell): Boolean = cell.isPassable()

    private inner class Node(val cell: Cell, var parent: Node?, val cost: Int): Comparable<Node> {
        var heuristic = 0

        fun successors(allowSubDirections: Boolean): List<Node> {
            val nodes = ArrayList<Node>(7)
            val adjacent = if (allowSubDirections) cell.getAdjacentCells() else cell.getAdjacentCellsInMainDirections()

            for (adj in adjacent)
                if (adj != parent?.cell && canEnter(adj))
                    nodes.add(Node(adj, this, cost + costToEnter(adj)))

            return nodes
        }

        override fun compareTo(other: Node): Int = heuristic - other.heuristic

        fun toString() = cell.toString()
    }
}
