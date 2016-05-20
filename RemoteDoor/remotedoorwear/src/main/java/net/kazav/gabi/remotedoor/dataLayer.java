package net.kazav.gabi.remotedoor;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by gabik on 5/16/16.
 * On data change
 */
public class dataLayer extends WearableListenerService { //implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "DoorsDataLayer";

//    private GoogleApiClient mGoogleApiClient;
//
//    public dataLayer(Context c) {
//        super();
//        mGoogleApiClient = new GoogleApiClient.Builder(c)
//                .addApi(Wearable.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//    }
//
//    public void connect() {mGoogleApiClient.connect();}
//
//    @Override
//    public void onConnected(Bundle bundle) {
//        Wearable.DataApi.addListener(mGoogleApiClient, this);
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    public void pause(Context c) {
//        Wearable.DataApi.removeListener(mGoogleApiClient, this);
//        mGoogleApiClient.disconnect();
//    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        Log.w(TAG, "Wear got on_change");
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult = googleApiClient.blockingConnect(30, TimeUnit.SECONDS);
        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }

        for (DataEvent dataEvent : events) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = dataEvent.getDataItem();
                Log.w(TAG, "item path: " + item.getUri().getPath());
                if(item.getUri().getPath().startsWith("/doors/")){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    int doornum = Integer.parseInt(item.getUri().getPath().split("/")[1]);
                    Log.w(TAG, "door num: " + Integer.toString(doornum));
                    prefs.save_door(this, doornum, dataMap);
                }
            }
        }
    }

}
