package remotedoors.gabi.kazav.net.remotedoors;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;

import com.google.android.gms.wearable.DataMap;

public class main extends Activity implements WearableListView.ClickListener {

    private door_list_adapter dla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);

        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                WearableListView listView = (WearableListView) stub.findViewById(R.id.doors_list);
                Log.w(dataLayer.TAG, "Starting Adapter");
                dla = new door_list_adapter(stub.getContext(), prefs.get_doors(stub.getContext()));
                listView.setAdapter(dla);
                listView.setClickListener(main.this);
            }
        });
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
//        String[] door = {dm.getString("name"), dm.getString("secret")};
//        new HttpRequestTask(this).execute(door);
        dataLayer.sendMessage(this, dm.getString("doorid"));
        dm.putLong("time", System.currentTimeMillis());
        prefs.save_door(this, Integer.parseInt(dm.getString("doorid")), dm);
    }

    @Override
    public void onTopEmptyRegionClick() {}
}
