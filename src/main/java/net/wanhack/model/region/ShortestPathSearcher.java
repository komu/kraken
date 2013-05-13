/*
 *  Copyright 2005 The Wanhack Team
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.wanhack.model.region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Calculates shortest path from node to another using A* search algorithm.
 * See http://en.wikipedia.org/wiki/A-star_algorithm for details.
 */
public class ShortestPathSearcher {

    private final Region region;
    private boolean allowSubdirections = true;
    
    public ShortestPathSearcher(Region region) {
        this.region = region;
    }
    
    public void setAllowSubdirections(boolean allowSubdirections) {
        this.allowSubdirections = allowSubdirections;
    }
    
    public Cell findFirstCellOnShortestPath(Cell start, Cell goal) {
        List<Cell> path = findShortestPath(start, goal);
        if (path != null && path.size() > 1) {
            return path.get(1); // path[0] is start, path[1] the first node
        } else {
            return null;
        }
    }
    
    public List<Cell> findShortestPath(Cell start, Cell goal) {
        PriorityQueue<Node> openHeap = new PriorityQueue<Node>();
        Map<Cell, Node> openMap = new CellMap<Node>(region);
        Map<Cell, Node> closedMap = new CellMap<Node>(region);
        
        Node startNode = new Node(start, null, 0);
        startNode.heuristic = estimateCost(start, goal);
        openHeap.add(startNode);
        
        while (!openHeap.isEmpty()) {
            Node current = openHeap.poll();
            if (current.cell == goal) {
                List<Cell> result = new ArrayList<Cell>();
                for (Node node = current; node != null; node = node.parent) {
                    result.add(node.cell);
                }
                
                Collections.reverse(result);
                return result;
            }

            for (Node successor : current.getSuccessors(allowSubdirections)) {
                Node closedNode = closedMap.get(successor.cell);
                if (closedNode != null) {
                    if (closedNode.cost <= successor.cost) {
                        continue;
                    } else {
                        closedMap.remove(successor.cell);
                    }
                }

                /**
                 * Next, an estimate of the new node's distance to the goal is
                 * added to the cost to form the heuristic for that node. This
                 * is then added to the "open" priority queue, unless an
                 * identical node with lesser or equal heuristic is found there.
                 */
                successor.parent = current;
                successor.heuristic = successor.cost + estimateCost(successor.cell, goal);
                
                Node openNode = openMap.get(successor.cell);
                if (openNode == null || openNode.heuristic > successor.heuristic) {
                    openHeap.offer(successor);
                    openMap.put(successor.cell, successor);
                }
            }

            closedMap.put(current.cell, current);
        }
        return null;
    }
    
    protected int estimateCost(Cell from, Cell target) {
        return from.distance(target);
    }
    
    protected int costToEnter(Cell cell) {
        return 1;
    }
    
    protected boolean canEnter(Cell cell) {
        return cell.isPassable();
    }
    
    private class Node implements Comparable<Node> {
        private final Cell cell;
        private Node parent;
        private int cost;
        private int heuristic = 0;
        
        public Node(Cell cell, Node parent, int cost) {
            this.cell = cell;
            this.parent = parent;
            this.cost = cost;
        }
        
        public List<Node> getSuccessors(boolean allowSubdirections) {
            List<Node> nodes = new ArrayList<Node>(7);
            
            List<Cell> adjacent = allowSubdirections ? cell.getAdjacentCells() 
                                : cell.getAdjacentCellsInMainDirections();
            
            for (Cell adj : adjacent) {
                Cell parentCell = (parent != null) ? parent.cell : null;
                if (canEnter(adj) && adj != parentCell) {
                    nodes.add(new Node(adj, this, cost + costToEnter(adj)));
                }
            }
            
            return nodes;
        }

        public int compareTo(Node node) {
            return heuristic - node.heuristic;
        }
        
        @Override
        public String toString() {
            return cell.toString();
        }
    }
}

