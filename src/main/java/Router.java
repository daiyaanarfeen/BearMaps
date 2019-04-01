import java.util.LinkedList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a LinkedList of <code>Long</code>s representing the shortest path from st to dest,
     * where the longs are node IDs.
     */

    public static class SearchNode {
        Long node;
        double distance;
        SearchNode prev;

        public SearchNode(Long n, double d, SearchNode p) {
            node = n;
            distance = d;
            prev = p;
        }
    }

    public static class NodeCompare implements Comparator<GraphDB.Node> {
        GraphDB map;
        Long end;

        public NodeCompare(GraphDB g, Long last) {
            map = g;
            end = last;
        }

        public int compare(GraphDB.Node n1, GraphDB.Node n2) {
            double p1 = n1.distFromStart() + map.distance(n1.id(), end);
            double p2 = n2.distFromStart() + map.distance(n2.id(), end);
            if (p1 > p2) {
                return 1;
            } else if (p1 < p2) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public static LinkedList<Long> shortestPath(
            GraphDB g, double stlon, double stlat, double destlon, double destlat) {
        long startNode = g.closest(stlon, stlat);
        long destNode = g.closest(destlon, destlat);
        PriorityQueue<GraphDB.Node> curPaths = new PriorityQueue<>(new NodeCompare(g, destNode));
        GraphDB.Node currNode = g.nodes().get(startNode);
        HashMap<Long, GraphDB.Node> seen = new HashMap<>();
        while (currNode.id() != destNode) {
            for (long n : g.adjacent(currNode.id())) {
                if (currNode.prev() == null
                        || (n != (currNode.prev().id()) && !seen.containsKey(n))) {
                    GraphDB.Node toAdd = g.nodes().get(n);
                    toAdd.setPrev(currNode);
                    toAdd.setDist(currNode.distFromStart() + g.distance(currNode.id(), n));
                    curPaths.add(toAdd);
                    seen.put(n, toAdd);
                } else if (n != (currNode.prev().id()) && seen.containsKey(n)) {
                    if ((currNode.distFromStart() + g.distance(currNode.id(), n))
                            < seen.get(n).distFromStart()) {
                        curPaths.remove(seen.get(n));
                        GraphDB.Node toAdd = g.nodes().get(n);
                        toAdd.setPrev(currNode);
                        toAdd.setDist(currNode.distFromStart() + g.distance(currNode.id(), n));
                        curPaths.add(toAdd);
                        seen.put(n, toAdd);
                    }
                }
            }
            GraphDB.Node a = curPaths.remove();
            while (a.id() == currNode.id()) {
                a = curPaths.remove();
            }
            currNode = a;
        }
        LinkedList<Long> solution = new LinkedList<>();
        while (currNode != null) {
            solution.add(0, currNode.id());
            GraphDB.Node prevNode = currNode;
            currNode = currNode.prev();
            prevNode.setDist(0);
            prevNode.setPrev(null);
        }
        return solution;
    }
}
