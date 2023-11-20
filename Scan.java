import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeMap;

public class Scan {
    public static void main(String[] args) {
     System.out.println(vod(8902));   
    }

    public static int vod(int num) {
        int ret=0;
        for(int i = 0; i < num; i++) {
            ret++;
        }
        return ret;
    }

}
