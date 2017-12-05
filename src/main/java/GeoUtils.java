import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

public class GeoUtils {

//    public static boolean contains(String wkt, double n, double w) {
//        if (wkt.charAt(0) == 'P') {
//            String stripped = stripPoly(wkt);
//        }
//    }

    //Check which, parse and check
    boolean contains(String geometry, double n, double w){
        if(geometry.charAt(0)=='P'){ //POLY
            String stripped = stripPoly(geometry);
            double[] wArray = toWestArray(stripped);
            double[] nArray = toNorthArray(stripped);
            return pointInPolygon(wArray , nArray, w, n );
        }
        else{ //multi
            String[] stripped = stripMulti(geometry);
            for(int i=0;i<stripped.length;i++){
                double[] wArray = toWestArray(stripped[i]);
                double[] nArray = toNorthArray(stripped[i]);
                boolean c = pointInPolygon(wArray , nArray, w, n );
                if(c)
                    return c;
            }
        }
        return false;
    }
    //  float  polyX[]      =  horizontal coordinates of corners
    //  float  polyY[]      =  vertical coordinates of corners
    //  float  x, y         =  point to be tested

    //  The function will return True if the point x,y is inside the polygon, or
    //  false if it is not.  If the point is exactly on the edge of the polygon,
    //  then the function may return True or false.

    public boolean pointInPolygon(double   vertx[] , double   verty[], double   testx, double  testy ) {

        int    nvert = vertx.length;
        int   i, j=nvert-1 ;
        boolean   oddNodes = false;

        for (i = 0, j = nvert-1; i < nvert; j = i++) {
            if ( ((verty[i]>testy) != (verty[j]>testy)) &&
                    (testx < (vertx[j]-vertx[i]) * (testy-verty[i]) / (verty[j]-verty[i]) + vertx[i]) )
                oddNodes = !oddNodes;
        }
        return oddNodes;
    }
    //pass in stripped string ie no POLYGON(( or part of MULTIPOLYGON (((
    //MULTIPOLYGON (((-8.31237486330895 54.4741146949518, -8.31973457609406 54.472332199815, -8.32175605832524 54.4734643448654, -8.32571386479372 54.4728074162798, -8.33002533533919 54.4739876818649, -8.33452331800075 54.4705948126682, -8.3446992067295 54.4718400124297, -8.35768758558821 54.4674532243135,
    //POLYGON ((-8.63275151448997 52.6850712428534, -8.63706990547265 52.6860516777178, -8.63702214958035 52.6861357634509, -8.63697340575165 52.6862038071593, -8.63592133857606 52.687414468831,
    // west north, etc.. west is negative
    double[] toWestArray(String text){
        List<Double> contours = new LinkedList<Double>();
        int	i=0;
        while(i<(text.length()-16)){
            String curr = 	text.substring(i, i+16);
            Double n = Double.parseDouble(curr);
            contours.add(n);
            i+=36;
        }
        Double[] a = new Double[contours.size()];
        a = contours.toArray(a);
        double[] d = ArrayUtils.toPrimitive(a);
        return d;
    }
    double[] toNorthArray(String text){
        List<Double> contours = new LinkedList<Double>();
        int	i=18;
        while(i<(text.length()-16)){
            String curr = 	text.substring(i, i+16);
            Double n = Double.parseDouble(curr);
            contours.add(n);
            i+=36;
        }
        Double[] a = new Double[contours.size()];
        a = contours.toArray(a);
        double[] d = ArrayUtils.toPrimitive(a);
        return d;
    }
    //parseMULTIPOLYGONstring
    String[] stripMulti(String orginal){
        int l = orginal.length();
        String trimmed = orginal.substring(11, l-3);
        //"), (" divides polygons
        String regex = "\\),\\(";
        return trimmed.split(regex);
    }
    //parsePolystring
    String stripPoly(String orginal){
        int l = orginal.length();
        return orginal.substring(10, l-2);
    }


}