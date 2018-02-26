package talent.plantmonitor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import talent.plantmonitor.Model.SensorModel;
import talent.plantmonitor.parser.JSONParser;

public class MainActivity extends AppCompatActivity {

    private TextView light;
    private TextView water;
    private TextView temp;
    private TextView cond;

    private ListView scrollList;
    private String plantList[] = DatabaseClass.plantNames;
    private int plantImg[] = DatabaseClass.plantImg;
    //private int flags[] = {R.drawable.india, R.drawable.china, R.drawable.australia, R.drawable.portugle, R.drawable.america, R.drawable.new_zealand};

    private Button refresh;

    // Timer for updating
    private Timer autoUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.light = (TextView) this.findViewById(R.id.text_light);
        this.water = (TextView) this.findViewById(R.id.text_water);
        this.temp = (TextView) this.findViewById(R.id.text_temp);
        this.cond= (TextView) this.findViewById(R.id.text_cond);

        scrollList = (ListView) findViewById(R.id.list_view);
        final PlantAdapter plantAdapter = new PlantAdapter(getApplicationContext(), plantList, plantImg);
        scrollList.setAdapter(plantAdapter);
        scrollList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) plantAdapter.getItem(position);

                Intent intent = new Intent(MainActivity.this, DataActivity.class);
                intent.putExtra("EXTRA_PLANT_NAME", item);
                intent.putExtra("EXTRA_POSITION", position);
                startActivity(intent);
            }
        });
//
//        this.refresh = (Button) this.findViewById(R.id.refresh);

        light.setText("20000lux");
        water.setText("60%");
        temp.setText("26.8");
        cond.setText("236uS/cm");

        DatabaseClass.sensorModels = new ArrayList<SensorModel>();

//        new GetDataTask().execute();
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
        //DatabaseClass.updateReadings();
        new GetDataTask().execute();
        light.setText(Integer.toString(DatabaseClass.sensLight) + " lux");
        water.setText(Integer.toString(DatabaseClass.sensWater) + "%");
        temp.setText(Double.toString(DatabaseClass.sensTemp) + "\u00B0C");
        cond.setText(Integer.toString(DatabaseClass.sensCond) + " uS/cm");

        if (DatabaseClass.sensorModels.size() > 0) {
            int readLight = DatabaseClass.sensorModels.get(DatabaseClass.sensorModels.size()-1).getLight();
            int readWater = DatabaseClass.sensorModels.get(DatabaseClass.sensorModels.size()-1).getWater();
            double readTemp = DatabaseClass.sensorModels.get(DatabaseClass.sensorModels.size()-1).getTemp();
            int readCond = DatabaseClass.sensorModels.get(DatabaseClass.sensorModels.size()-1).getCond();
            light.setText(Integer.toString(readLight) + " lux");
            water.setText(Integer.toString(readWater) + "%");
            temp.setText(String.format("%.2f\u00B0C", readTemp));
            cond.setText(Integer.toString(readCond) + " uS/cm");
        }
    }

    /**
     * Creating Get Data Task for Getting Data From Web
     */
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
}
