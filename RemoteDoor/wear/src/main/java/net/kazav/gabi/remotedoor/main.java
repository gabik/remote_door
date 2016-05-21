package net.kazav.gabi.remotedoor;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;

import com.google.android.gms.wearable.DataMap;

public class main extends Activity implements WearableListView.ClickListener {

    private door_list_adapter dla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);
        WearableListView listView = (WearableListView) findViewById(R.id.doors_list);
        Log.w(dataLayer.TAG, "Starting Adapter");
        dla = new door_list_adapter(this, prefs.get_doors(this));
        listView.setAdapter(dla);
        listView.setClickListener(this);
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        int pos = viewHolder.getAdapterPosition();
        Log.w(dataLayer.TAG, "Clicked on ID: " + Integer.toString(pos));
        DataMap dm = dla.get_by_pos(pos);
        Log.w(dataLayer.TAG, "Door params - caption: " + dm.getString("caption")
                + " name: " + dm.getString("name")
                + " secret: " + dm.getString("secret")
                + " ID: " + dm.getString("doorid")
        );
        String[] door = {dm.getString("name"), dm.getString("secret")};
        new HttpRequestTask(this).execute(door);
        dm.putLong("time", System.currentTimeMillis());
        prefs.save_door(this, Integer.parseInt(dm.getString("doorid")), dm);
    }

    @Override
    public void onTopEmptyRegionClick() {

    }
}
