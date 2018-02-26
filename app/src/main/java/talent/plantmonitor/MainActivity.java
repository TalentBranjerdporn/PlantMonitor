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

    // list for reading data from database
    private ArrayList<String> list1;
    private ArrayList<String> list2;

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

        list1 = new ArrayList<String>();
        list2 = new ArrayList<String>();
        new GetDataTask().execute();
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
        light.setText(Integer.toString(DatabaseClass.sensLight) + " lux");
        water.setText(Integer.toString(DatabaseClass.sensWater) + "%");
        temp.setText(Double.toString(DatabaseClass.sensTemp) + "\u00B0C");
        cond.setText(Integer.toString(DatabaseClass.sensCond) + " uS/cm");
    }

    /**
     * Creating Get Data Task for Getting Data From Web
     */
    class GetDataTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;
        int jIndex;
        int x,y;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            x = list1.size();
            y = list2.size();

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
                        JSONArray array = jsonObject.getJSONArray(DatabaseClass.KEY_CONTACTS);

                        /**
                         * Check Length of Array...
                         */


                        int lenArray = array.length();
                        if(lenArray > 0) {
                            for( ; jIndex < lenArray; jIndex++) {

                                /**
                                 * Creating Every time New Object
                                 * and
                                 * Adding into List
                                 */
                                //MyDataModel model = new MyDataModel();

                                /**
                                 * Getting Inner Object from contacts array...
                                 * and
                                 * From that We will get Name of that Contact
                                 *
                                 */
                                JSONObject innerObject = array.getJSONObject(jIndex);
                                String name = innerObject.getString(DatabaseClass.KEY_NAME);
                                String country = innerObject.getString(DatabaseClass.KEY_COUNTRY);

                                Log.d("datatask", "doInBackground:" + name + " - " + country);

                                /**
                                 * Getting Object from Object "phone"
                                 */
                                //JSONObject phoneObject = innerObject.getJSONObject(Keys.KEY_PHONE);
                                //String phone = phoneObject.getString(Keys.KEY_MOBILE);

//                                model.setName(name);
//                                model.setCountry(country);

                                /**
                                 * Adding name and phone concatenation in List...
                                 */
                                list1.add(name);
                                list2.add(country);
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
            if(list1.size() > 0) {
                Toast.makeText(MainActivity.this, list1.get(0), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "None", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
