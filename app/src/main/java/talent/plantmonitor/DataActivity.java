package talent.plantmonitor;

import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class DataActivity extends AppCompatActivity {

    private TextView plantName;
    private TextView plantLight;
    private TextView plantWater;
    private TextView plantTemp;
    private TextView plantCond;

    private TextView realLight;
    private TextView realWater;
    private TextView realTemp;
    private TextView realCond;

    private ImageView plantImage;

    private Timer autoUpdate;

    private int plant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        this.plantName = (TextView) this.findViewById(R.id.text_plant_name);
        String name = getIntent().getExtras().getString("EXTRA_PLANT_NAME");
        plant = getIntent().getExtras().getInt("EXTRA_POSITION");
        plantName.setText(DatabaseClass.plantNames[plant]);

        this.plantImage = (ImageView) this.findViewById(R.id.img_plant);
        plantImage.setImageResource(DatabaseClass.plantImg[plant]);

        this.plantLight = (TextView) this.findViewById(R.id.text_opt_light);
        this.plantWater = (TextView) this.findViewById(R.id.text_opt_water);
        this.plantTemp = (TextView) this.findViewById(R.id.text_opt_temp);
        this.plantCond = (TextView) this.findViewById(R.id.text_opt_cond);

        int lightLevel = DatabaseClass.levelLight[plant];
        int minLight = 10000;
        int maxLight = 20000;
        if (lightLevel == 1) {
            minLight = 1000;
            maxLight = 2000;
        } else if (lightLevel == 2) {
            minLight = 2000;
            maxLight = 10000;
        } else if (lightLevel == 3) {
            minLight = 10000;
            maxLight = 20000;
        } else if (lightLevel == 4) {
            minLight = 20000;
            maxLight = 32000;
        } else if (lightLevel == 5) {
            minLight = 32000;
            maxLight = 100000;
        }

        plantLight.setText(minLight + "-" + maxLight + " lux");
        plantWater.setText(DatabaseClass.soilWater[plant] + "%");
        plantTemp.setText(DatabaseClass.minTemps[plant] + "-" + DatabaseClass.maxTemps[plant] + "\u00B0C");
        plantCond.setText("200-1200uS/cm");

        this.realLight = (TextView) this.findViewById(R.id.text_real_light);
        this.realWater = (TextView) this.findViewById(R.id.text_real_water);
        this.realTemp = (TextView) this.findViewById(R.id.text_real_temp);
        this.realCond = (TextView) this.findViewById(R.id.text_real_cond);

        checkHealth();

        realLight.setText(Integer.toString(DatabaseClass.sensLight) + " lux");
        realWater.setText(Integer.toString(DatabaseClass.sensWater) + "%");
        realTemp.setText(Double.toString(DatabaseClass.sensTemp) + "\u00B0C");
        realCond.setText(Integer.toString(DatabaseClass.sensCond) + " uS/cm");
    }

    @Override
    public void onResume() {
        super.onResume();
        autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        update();
                    }
                });
            }
        }, 0, 5000); // updates each 5 secs
    }

    @Override
    public void onPause() {
        autoUpdate.cancel();
        super.onPause();
    }

    private void update() {
        DatabaseClass.updateReadings();
        checkHealth();
        realLight.setText(Integer.toString(DatabaseClass.sensLight) + " lux");
        realWater.setText(Integer.toString(DatabaseClass.sensWater) + "%");
        realTemp.setText(Double.toString(DatabaseClass.sensTemp) + "\u00B0C");
        realCond.setText(Integer.toString(DatabaseClass.sensCond) + " uS/cm");
    }

    private void checkHealth() {

        int light = DatabaseClass.sensLight;
        int lightLevel = DatabaseClass.levelLight[plant];
        int minLight = 10000;
        int maxLight = 20000;
        if (lightLevel == 1) {
            minLight = 1000;
            maxLight = 2000;
        } else if (lightLevel == 2) {
            minLight = 2000;
            maxLight = 10000;
        } else if (lightLevel == 3) {
            minLight = 10000;
            maxLight = 20000;
        } else if (lightLevel == 4) {
            minLight = 20000;
            maxLight = 32000;
        } else if (lightLevel == 5) {
            minLight = 32000;
            maxLight = 100000;
        }
        if (minLight > light) {
            realLight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        } else if (maxLight < light) {
            realLight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        } else {
            realLight.setTextColor(getResources().getColor(R.color.green));
        }

        int water = DatabaseClass.sensWater;
        int optWater = DatabaseClass.soilWater[plant];
        if (optWater - 10 > water) {
            realWater.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        } else if (optWater + 10 < water) {
            realWater.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        } else {
            realWater.setTextColor(getResources().getColor(R.color.green));
        }

        double temp = DatabaseClass.sensTemp;
        int minTemp = DatabaseClass.minTemps[plant];
        int maxTemp = DatabaseClass.maxTemps[plant];
        if (minTemp > temp) {
            realTemp.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        } else if (maxTemp < temp) {
            realTemp.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        } else {
            realTemp.setTextColor(getResources().getColor(R.color.green));
        }

        int cond = DatabaseClass.sensCond;
        if (200 > cond) {
            realCond.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        } else if (1200 < cond) {
            realCond.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        } else {
            realCond.setTextColor(getResources().getColor(R.color.green));
        }
    }
}
