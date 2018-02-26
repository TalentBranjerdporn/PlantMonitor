package talent.plantmonitor;

import java.util.Random;

/**
 * Created by Talent on 8/10/2017.
 */

public class DatabaseClass {

    public static int sensLight = 10000;
    public static int sensWater = 50;
    public static double sensTemp = 25;
    public static int sensCond = 240;

    public static final String KEY_CONTACTS = "Sheet1";
    public static final String KEY_NAME = "Column_1";
    public static final String KEY_COUNTRY = "Column_2";

    public static void updateReadings() {
        Random rnd = new Random();
        sensLight = rnd.nextInt(100000);
        sensWater = rnd.nextInt(100);
        sensTemp = rnd.nextInt(33-24) + 24 + rnd.nextInt(10)/10.0;
        sensCond = rnd.nextInt(1500);
    }

    public final static String plantNames[] = {
            "Tomato",
            "Lettuce",
            "Brocolli",
            "Celery",
            "Kale",
            "Peas",
            "Spinach",
            "Onion",
            "Watermelon",
            "Pumpkin"
    };

    public final static int plantImg[] = {
            R.drawable.img_tomato,
            R.drawable.img_lettuce,
            R.drawable.img_broccoli,
            R.drawable.img_celery,
            R.drawable.img_kale,
            R.drawable.img_peas,
            R.drawable.img_spinach,
            R.drawable.img_onion,
            R.drawable.img_watermelon,
            R.drawable.img_pumpkin
    };

    public final static int minTemps[] = {
            10,
            7,
            10,
            7,
            10,
            10,
            10,
            10,
            15,
            10
    };

    public final static int maxTemps[] = {
            30,
            25,
            30,
            40,
            30,
            25,
            30,
            30,
            30,
            30
    };

    public final static int soilWater[] = {
            50,
            60,
            70,
            70,
            70,
            40,
            70,
            70,
            40,
            40
    };

    public final static int levelLight[] = {
            5,
            5,
            5,
            5,
            4,
            5,
            4,
            5,
            5,
            5
    };
}
