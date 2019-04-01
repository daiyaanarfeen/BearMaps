import java.io.File;
import java.io.InterruptedIOException;

/**
 * Created by Daiyaan on 4/13/2017.
 */
public class QuadTree {
    public static String dir;
    public String root;
    public QuadTree UL, UR, LL, LR;
    public double ULLAT, ULLON, LRLAT, LRLON, dpp;

    public QuadTree(String d, String r, double dpp, double ulat, double ulon, double llat, double llon) {
        dir = d;
        root = r;
        this.dpp = dpp;
        ULLAT = ulat;
        ULLON = ulon;
        LRLAT = llat;
        LRLON = llon;
        for (int i = 1; i < 5; i ++) {
            File file = new File(d + "/" + root + Integer.toString(i) + ".png");
            if (file.exists()) {
                addToTree(this, i);
            }
        }
    }

    public static void addToTree(QuadTree q, int i) {
        double ulat, ulon, llat, llon;
        if (i == 1) {
            ulat = q.ULLAT;
            ulon = q.ULLON;
            llat = q.ULLAT + (q.LRLAT - q.ULLAT) / 2;
            llon = q.ULLON + (q.LRLON - q.ULLON) / 2;
            q.UL = new QuadTree(dir, q.root + Integer.toString(i), q.dpp / 2, ulat, ulon, llat, llon);
        } else if (i == 2) {
            ulat = q.ULLAT;
            ulon = q.ULLON + (q.LRLON - q.ULLON) / 2;
            llon = q.LRLON;
            llat = q.ULLAT + (q.LRLAT - q.ULLAT) / 2;
            q.UR = new QuadTree(dir, q.root + Integer.toString(i), q.dpp / 2, ulat, ulon, llat, llon);
        } else if (i == 3) {
            ulon = q.ULLON;
            llat = q.LRLAT;
            ulat = q.ULLAT + (q.LRLAT - q.ULLAT) / 2;
            llon = q.ULLON + (q.LRLON - q.ULLON) / 2;
            q.LL = new QuadTree(dir, q.root + Integer.toString(i), q.dpp / 2, ulat, ulon, llat, llon);
        } else {
            llat = q.LRLAT;
            llon = q.LRLON;
            ulat = q.ULLAT + (q.LRLAT - q.ULLAT) / 2;
            ulon = q.ULLON + (q.LRLON - q.ULLON) / 2;
            q.LR = new QuadTree(dir, q.root + Integer.toString(i), q.dpp / 2, ulat, ulon, llat, llon);
        }
    }
}
