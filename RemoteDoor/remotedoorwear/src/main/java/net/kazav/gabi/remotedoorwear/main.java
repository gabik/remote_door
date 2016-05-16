package net.kazav.gabi.remotedoorwear;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.view.WearableListView;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

public class main extends Activity {

    private static dataLayer dl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);
        dl = new dataLayer(this);
        WearableListView listView = (WearableListView) findViewById(R.id.doors_list);
        Log.w("Starting", "Adapter");
        listView.setAdapter(new door_list_adapter(this, prefs.get_doors(this)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        dl.connect();
    }
}
