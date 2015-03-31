package utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class RandomIDGenerator {

    public static final int MAX = 1000000;

    public static String generateID() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss-");
        Random r = new Random();
        return dateFormat.format(new Date()) + r.nextInt() % MAX + "" + r.nextInt() % MAX;
    }
}
