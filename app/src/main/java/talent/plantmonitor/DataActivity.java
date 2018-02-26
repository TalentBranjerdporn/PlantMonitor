package talent.plantmonitor;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

import talent.plantmonitor.Model.SensorModel;
import talent.plantmonitor.parser.JSONParser;

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

        if (DatabaseClass.sensorModels.size() > 0) {
            int readLight = DatabaseClass.sensorModels.get(DatabaseClass.sensorModels.size()-1).getLight();
            int readWater = DatabaseClass.sensorModels.get(DatabaseClass.sensorModels.size()-1).getWater();
            double readTemp = DatabaseClass.sensorModels.get(DatabaseClass.sensorModels.size()-1).getTemp();
            int readCond = DatabaseClass.sensorModels.get(DatabaseClass.sensorModels.size()-1).getCond();
            realLight.setText(Integer.toString(readLight) + " lux");
            realWater.setText(Integer.toString(readWater) + "%");
            realTemp.setText(String.format("%.2f\u00B0C", readTemp));
            realCond.setText(Integer.toString(readCond) + " uS/cm");
        }
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
        }, 0, 10000); // updates each 5 secs
    }

    @Override
    public void onPause() {
        autoUpdate.cancel();
        super.onPause();
    }

    private void update() {
        // DatabaseClass.updateReadings();
        new GetDataTask().execute();
        checkHealth();

        realLight.setText(Integer.toString(DatabaseClass.sensLight) + " lux");
        realWater.setText(Integer.toString(DatabaseClass.sensWater) + "%");
        realTemp.setText(Double.toString(DatabaseClass.sensTemp) + "\u00B0C");
        realCond.setText(Integer.toString(DatabaseClass.sensCond) + " uS/cm");

        if (DatabaseClass.sensorModels.size() > 0) {
            int readLight = DatabaseClass.sensorModels.get(DatabaseClass.sensorModels.size()-1).getLight();
            int readWater = DatabaseClass.sensorModels.get(DatabaseClass.sensorModels.size()-1).getWater();
            double readTemp = DatabaseClass.sensorModels.get(DatabaseClass.sensorModels.size()-1).getTemp();
            int readCond = DatabaseClass.sensorModels.get(DatabaseClass.sensorModels.size()-1).getCond();
            realLight.setText(Integer.toString(readLight) + " lux");
            realWater.setText(Integer.toString(readWater) + "%");
            realTemp.setText(String.format("%.2f\u00B0C", readTemp));
            realCond.setText(Integer.toString(readCond) + " uS/cm");
        }
    }

    class GetDataTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;
        int jIndex;
        int x;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            x = DatabaseClass.sensorModels.size();

            if(x==0)
                jIndex=0;
            else
                jIndex=x;

            Log.d("DataTask","Made it here");
        }

        @Nullable
        @Override
        protected Void doInBackground(Void... params) {
            JSONObject jsonObject = JSONParser.getDataFromWeb();

            Log.d("DataTask","DoInBackground");

            try {
                if (jsonObject != null) {
                    /**
                     * Check Length...
                     */
                    if(jsonObject.length() > 0) {
                        /**
                         * Getting Array named "contacts" From MAIN Json Object
                         */
                        JSONArray array = jsonObject.getJSONArray(DatabaseClass.KEY_SHEET);

                        /**
                         * Check Length of Array...
                         */
                        int lenArray = array.length();

                        Log.d(JSONParser.TAG, "doInBackground: length = " + lenArray + " jIndex = " + jIndex);
                        if(lenArray > 0) {
                            for( ; jIndex < lenArray; jIndex++) {
                                JSONObject innerObject = array.getJSONObject(jIndex);
                                int time = innerObject.getInt(DatabaseClass.KEY_TIME);
                                int light = innerObject.getInt(DatabaseClass.KEY_LIGHT);
                                int water = innerObject.getInt(DatabaseClass.KEY_WATER);
                                double temp = innerObject.getDouble(DatabaseClass.KEY_TEMP);
                                int cond = innerObject.getInt(DatabaseClass.KEY_COND);

                                SensorModel model = new SensorModel();
                                model.setTime(time);
                                model.setLight(light);
                                model.setWater(water);
                                model.setTemp(temp);
                                model.setCond(cond);

                                Log.d(JSONParser.TAG, "doInBackground:" + model);

                                DatabaseClass.sensorModels.add(model);
                            }
                        }
                    }
                } else {

                }
            } catch (JSONException je) {
                Log.i(JSONParser.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /**
             * Checking if List size if more than zero then
             * Update ListView
             */
            if(DatabaseClass.sensorModels.size() > 0) {
                //Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                Log.d(JSONParser.TAG, "onPostExecute: " + DatabaseClass.sensorModels.get(DatabaseClass.sensorModels.size()-1).getLight());
            } else {
                //Toast.makeText(MainActivity.this, "None", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkHealth() {

        int light = DatabaseClass.getLastModel().getLight();
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

        int water = DatabaseClass.getLastModel().getWater();
        int optWater = DatabaseClass.soilWater[plant];
        if (optWater - 10 > water) {
            realWater.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        } else if (optWater + 10 < water) {
            realWater.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        } else {
            realWater.setTextColor(getResources().getColor(R.color.green));
        }

        double temp = DatabaseClass.getLastModel().getTemp();
        int minTemp = DatabaseClass.minTemps[plant];
        int maxTemp = DatabaseClass.maxTemps[plant];
        if (minTemp > temp) {
            realTemp.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        } else if (maxTemp < temp) {
            realTemp.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        } else {
            realTemp.setTextColor(getResources().getColor(R.color.green));
        }

        int cond = DatabaseClass.getLastModel().getCond();
        if (200 > cond) {
            realCond.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        } else if (1200 < cond) {
            realCond.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        } else {
            realCond.setTextColor(getResources().getColor(R.color.green));
        }
    }
}
