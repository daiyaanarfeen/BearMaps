import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daiyaan on 4/15/2017.
 */
public class Tester {
     public static void main(String[] args) {
        Rasterer rasterer = new Rasterer("img");
        HashMap<String, Double> input = new HashMap<>();
        input.put("lrlon", -122.24053369025242);
        input.put("ullon", -122.24163047377972);
        input.put("w", 892.0);
        input.put("h", 875.0);
        input.put("ullat", 37.87655856892288);
        input.put("lrlat", 37.87548268822065);
        Map answer = rasterer.getMapRaster(input);
        //{raster_ul_lon=-122.24212646484375, depth=7, raster_lr_lon=-122.24006652832031, raster_lr_lat=37.87538940251607, render_grid=[[img/2143411.png, img/2143412.png, img/2143421.png], [img/2143413.png, img/2143414.png, img/2143423.png], [img/2143431.png, img/2143432.png, img/2143441.png]] , raster_ul_lat=37.87701580361881, query_success=true}
     }
}
