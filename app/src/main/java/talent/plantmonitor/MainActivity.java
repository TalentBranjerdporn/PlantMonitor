package talent.plantmonitor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
}
