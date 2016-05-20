package remotedoors.gabi.kazav.net.remotedoors;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by gabik on 5/16/16.
 * On data change
 */
public class dataLayer extends WearableListenerService { //implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "DoorsDataLayer";
    private GoogleApiClient googleClient;

    private void initGoogleApiClient()
    {
        if (googleClient == null)
        {
            Log.w(TAG, "Building google client id...");
            googleClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .build();
            Log.w(TAG, "Google client id = " + googleClient.toString());
        }
        if (!googleClient.isConnected())
        {
            Log.w(TAG, "Connecting to Google API");
            googleClient.connect();
        }
        Log.w(TAG, "Google Client ID = " + googleClient.toString());
    }


    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.w(TAG, "onCreate");
        initGoogleApiClient();
    }

    @Override
    public void onDestroy()
    {
        if (null != googleClient && googleClient.isConnected())
        {
            Log.w("onDestroy", "Disconnecting googleClient");
            googleClient.disconnect();
        }

        super.onDestroy();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.w(TAG, "Door MSG: " + "Cool");
        if( messageEvent.getPath().startsWith("/doors") ) {
            final String message = new String(messageEvent.getData());
            Log.w(TAG, "Door MSG - " + message);
        } else {
            super.onMessageReceived( messageEvent );
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        Log.w(TAG, "Wear got on_change");
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);

        for (DataEvent dataEvent : events) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = dataEvent.getDataItem();
                Log.w(TAG, "item path: " + item.getUri().getPath());
                if(item.getUri().getPath().equals("/doors")){
                    prefs.clear_all(this);
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    //int doornum = Integer.parseInt(item.getUri().getPath().split("/")[2]);
                    //Log.w(TAG, "door num: " + Integer.toString(doornum));
                    ArrayList<DataMap> all_doors = dataMap.getDataMapArrayList("ALL");
                    for (DataMap dm : all_doors) {
                        prefs.save_door(this, dm.getInt("doorid"), dm);
                    }
                }
            }
        }
    }

}
