package talent.plantmonitor;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.zip.Inflater;

public class PlantAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
    private Context context;
    private String plantList[];
    private int plantImg[];
    private LayoutInflater inflter;

    private TextView plant;
    private ImageView icon;

    public PlantAdapter() {
        // default constructor
    }

    public PlantAdapter(Context applicationContext, String[] plantList, int[] plantImg) {
        this.context = context;
        this.plantList = plantList;
        this.plantImg = plantImg;
        inflter = (LayoutInflater.from(applicationContext));

    }

    @Override
    public int getCount() {
        return plantList.length;
    }

    @Override
    public Object getItem(int i) {
        return plantList[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.activity_listview, null);
        plant = (TextView) view.findViewById(R.id.scroll_text);
        icon = (ImageView) view.findViewById(R.id.scroll_icon);

        plant.setText(plantList[i]);
        icon.setImageResource(plantImg[i]);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}