package remotedoors.gabi.kazav.net.remotedoors;


import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabik on 5/16/16.
 * On data change
 */
public class dataLayer extends WearableListenerService { //implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static Handler toastThread = new Handler();
    private static Node mNode;
    public static final String TAG = "DoorsDataLayer";
    private static GoogleApiClient googleClient;
    private static String OPEN_DOOR = "/open/";

    private static void initGoogleApiClient(Context c)
    {
        if (googleClient == null)
        {
            Log.w(TAG, "Building google client id...");
            googleClient = new GoogleApiClient.Builder(c)
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
        initGoogleApiClient(this);
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
        Log.w(TAG, "MSG");
        if( messageEvent.getPath().startsWith("/acks") ) {
            Log.w(TAG, "ACK");
            String message = new String(messageEvent.getData());
            boolean is_ok = Boolean.parseBoolean(message);
            Log.w(TAG, "Door MSG - " + message);
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (is_ok) {
                long[] ptrn = {0, 50,100,60,100,80};
                v.vibrate(ptrn, -1);
            } else {
                Log.w(TAG, "Door cannot be found");

                toastThread.post(new Runnable() {
                    public void run() { Toast.makeText(getApplicationContext(), "הדלת אינה מחוברת", Toast.LENGTH_SHORT).show(); }
                });
                v.vibrate(1000);
            }
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

    public static void sendMessage(final Context c, final String doorid) {
        Log.w(TAG, "Sending msg");
        if (googleClient==null || !googleClient.isConnected()) {
            initGoogleApiClient(c);
        }
        Log.w(TAG, "G API is connected");
        Wearable.NodeApi.getConnectedNodes(googleClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(@NonNull NodeApi.GetConnectedNodesResult nodes) {
                Log.w(TAG, "Getting connected nodes");
                for (Node node : nodes.getNodes()) {
                    mNode = node;
                    Log.w(TAG, "Node is " + node.getDisplayName());
                }
                Log.w(TAG, "Path: " + OPEN_DOOR + doorid);
                Wearable.MessageApi.sendMessage(googleClient, mNode.getId(), OPEN_DOOR + doorid, null).setResultCallback(
                        new ResultCallback<MessageApi.SendMessageResult>() {
                            @Override
                            public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                                Log.e(TAG, "message status: " + sendMessageResult.getStatus().getStatusCode());
                            }
                        });
            }
        });
    }

}
