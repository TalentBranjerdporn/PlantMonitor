package talent.plantmonitor;

import java.util.ArrayList;
import java.util.Random;

import talent.plantmonitor.Model.SensorModel;

/**
 * Created by Talent on 8/10/2017.
 *
 * A possibly temporary class to handle global changes and database related information
 */

class DatabaseClass {

    static int sensLight = 10000;
    static int sensWater = 50;
    static double sensTemp = 25;
    static int sensCond = 240;

    static final String KEY_SHEET = "Sheet1";
    static final String KEY_TIME = "Time";
    static final String KEY_LIGHT = "Light";
    static final String KEY_WATER = "Moisture";
    static final String KEY_TEMP = "Temperature";
    static final String KEY_COND = "Conductivity";

    static ArrayList<SensorModel> sensorModels;

    static SensorModel getLastModel() {
        SensorModel model = sensorModels.get(sensorModels.size()-1);
        return model;
    }

    static void updateReadings() {
        Random rnd = new Random();
        sensLight = rnd.nextInt(100000);
        sensWater = rnd.nextInt(100);
        sensTemp = rnd.nextInt(33-24) + 24 + rnd.nextInt(10)/10.0;
        sensCond = rnd.nextInt(1500);
    }

    final static String plantNames[] = {
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

    final static int plantImg[] = {
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

    final static int minTemps[] = {
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

    final static int maxTemps[] = {
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

    final static int soilWater[] = {
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

    final static int levelLight[] = {
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
