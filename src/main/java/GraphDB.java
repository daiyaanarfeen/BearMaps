import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */
    private HashMap<Long, Node> nodes;
    private HashMap<Long, Set<Long>> edges;
    private ArrayList<Long> curWaynodes;

    public static class Node {
        private long id;
        private double lat, lon;
        private double distFromStart;
        private Node prev;


        public Node(long newID, double newLat, double newLon) {
            id = newID;
            lat = newLat;
            lon = newLon;
            distFromStart = 0;
            prev = null;
        }

        public void setPrev(Node p) {
            prev = p;
        }

        public void setDist(double d) {
            distFromStart = d;
        }

        public long id() {
            return id;
        }

        public double lat() {
            return lat;
        }

        public double lon() {
            return lon;
        }

        public double distFromStart() {
            return distFromStart;
        }

        public Node prev() {
            return prev;
        }
    }
    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        nodes = new HashMap<>();
        edges = new HashMap<>();
        curWaynodes = new ArrayList<>();
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        ArrayList<Long> nodesToRemove = new ArrayList<>();
        for (Long n : edges.keySet()) {
            edges.get(n).remove(n);
        }
        for (Long n : nodes.keySet()) {
            if (edges.get(n).isEmpty()) {
                nodesToRemove.add(n);
            }
        }
        for (Long n : nodesToRemove) {
            nodes.remove(n);
            edges.remove(n);
        }
    }

    /** Returns an iterable of all vertex IDs in the graph. */
    Iterable<Long> vertices() {
        //YOUR CODE HERE, this currently returns only an empty list.
        ArrayList<Long> returnArrayList = new ArrayList<>();
        for (Long n : nodes.keySet()) {
            returnArrayList.add(n);
        }
        return returnArrayList;
    }

    /** Returns ids of all vertices adjacent to v. */
    Iterable<Long> adjacent(long v) {
        return edges.get(v);
    }

    /** Returns the Euclidean distance between vertices v and w, where Euclidean distance
     *  is defined as sqrt( (lonV - lonV)^2 + (latV - latV)^2 ). */
    double distance(long v, long w) {
        Node a = nodes.get(v);
        Node b = nodes.get(w);
        return Math.sqrt(Math.pow(a.lon - b.lon, 2) + Math.pow(a.lat - b.lat, 2));
    }

    /** Returns the vertex id closest to the given longitude and latitude. */
    long closest(double lon, double lat) {
        Node closest = nodes.values().iterator().next();
        for (Node n : nodes.values()) {
            if (Math.sqrt(Math.pow(n.lon - lon, 2) + Math.pow(n.lat - lat, 2)) 
                    < Math.sqrt(Math.pow(closest.lon - lon, 2) + Math.pow(closest.lat - lat, 2))) {
                closest = n;
            }
        }
        return closest.id;
    }

    /** Longitude of vertex v. */
    double lon(long v) {
        return nodes.get(v).lon;
    }

    /** Latitude of vertex v. */
    double lat(long v) {
        return nodes.get(v).lat;
    }

    HashMap<Long, Node> nodes() {
        return nodes;
    }

    HashMap<Long, Set<Long>> edges() {
        return edges;
    }
}
