import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    public static final double ROOT_ULLAT = 37.892195547244356, ROOT_ULLON = -122.2998046875,
            ROOT_LRLAT = 37.82280243352756, ROOT_LRLON = -122.2119140625;
    private QuadTree map;
    // Recommended: QuadTree instance variable. You'll need to make
    //              your own QuadTree since there is no built-in quadtree in Java.

    /** imgRoot is the name of the directory containing the images.
     *  You may not actually need this for your class. */
    public Rasterer(String imgRoot) {
        map = new QuadTree(imgRoot, "", 0.00034332275390625,
                ROOT_ULLAT, ROOT_ULLON, ROOT_LRLAT, ROOT_LRLON);
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     * </p>
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified:
     * "render_grid"   -> String[][], the files to display
     * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     *                    Can also be interpreted as the length of the numbers in the image
     *                    string. <br>
     * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
     *                    forget to set this to true! <br>
     * @see #REQUIRED_RASTER_REQUEST_PARAMS
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        // System.out.println(params);
        HashMap<String, Object> returnMap = new HashMap<>();
        double dpp = (params.get("lrlon") - params.get("ullon")) / params.get("w");
        QuadTree upperLeft = map;
        QuadTree lowerRight = map;
        while (upperLeft.dpp > dpp) {
            if (upperLeft.UR == null) {
                break;
            }
            if (upperLeft.UR.ULLON <= params.get("ullon")) {
                if (upperLeft.LR.ULLAT >= params.get("ullat")) {
                    upperLeft = upperLeft.LR;
                } else {
                    upperLeft = upperLeft.UR;
                }
            } else {
                if (upperLeft.LL.ULLAT >= params.get("ullat")) {
                    upperLeft = upperLeft.LL;
                } else {
                    upperLeft = upperLeft.UL;
                }
            }
        }
        while (lowerRight.dpp > dpp) {
            if (lowerRight.UR == null) {
                break;
            }
            if (lowerRight.LL.LRLON >= params.get("lrlon")) {
                if (lowerRight.UL.LRLAT <= params.get("lrlat")) {
                    lowerRight = lowerRight.UL;
                } else {
                    lowerRight = lowerRight.LL;
                }
            } else {
                if (lowerRight.UR.LRLAT <= params.get("lrlat")) {
                    lowerRight = lowerRight.UR;
                } else {
                    lowerRight = lowerRight.LR;
                }
            }
        }
        double difLon = upperLeft.LRLON - upperLeft.ULLON;
        double difLat = upperLeft.ULLAT - upperLeft.LRLAT;
        int dimLon = (int) Math.round(((lowerRight.LRLON - upperLeft.ULLON) / difLon));
        int dimLat = (int) Math.round(((upperLeft.ULLAT - lowerRight.LRLAT) / difLat));
        String[][] grid = new String[dimLat][dimLon];
        String rowStart = upperLeft.root;
        for (int i = 0; i < dimLat; i++) {
            String curr = rowStart;
            for (int j = 0; j < dimLon; j++) {
                grid[i][j] = upperLeft.dir + curr + ".png";
                curr = right(curr);
            }
            rowStart = down(rowStart);
        }
        returnMap.put("render_grid", grid);
        returnMap.put("raster_ul_lon", upperLeft.ULLON);
        returnMap.put("raster_ul_lat", upperLeft.ULLAT);
        returnMap.put("raster_lr_lon", lowerRight.LRLON);
        returnMap.put("raster_lr_lat", lowerRight.LRLAT);
        returnMap.put("depth", upperLeft.root.length());
        returnMap.put("query_success", true);
        return returnMap;
    }

    public static String down(String pos) {
        if (pos.equals("3") || pos.equals("4")) {
            return pos;
        }
        String lastNum = pos.substring(pos.length() - 1, pos.length());
        if (lastNum.equals("1") || lastNum.equals("2")) {
            return pos.substring(0, pos.length() - 1)
                    + Integer.toString(Integer.parseInt(lastNum) + 2);
        } else {
            return down(pos.substring(0, pos.length() - 1))
                    + Integer.toString(Integer.parseInt(lastNum) - 2);
        }
    }

    public static String right(String pos) {
        if (pos.equals("2") || pos.equals("4")) {
            return pos;
        }
        String lastNum = pos.substring(pos.length() - 1, pos.length());
        if (lastNum.equals("1") || lastNum.equals("3")) {
            return pos.substring(0, pos.length() - 1)
                    + Integer.toString(Integer.parseInt(lastNum) + 1);
        } else {
            return right(pos.substring(0, pos.length() - 1))
                    + Integer.toString(Integer.parseInt(lastNum) - 1);
        }
    }
}

