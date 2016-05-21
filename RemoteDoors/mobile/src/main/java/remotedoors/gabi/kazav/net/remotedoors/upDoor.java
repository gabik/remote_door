package remotedoors.gabi.kazav.net.remotedoors;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Implementation of App Widget functionality.
 */
public  class upDoor extends AppWidgetProvider { // implements ResultCallback<DataApi.DataItemResult> {
    public static String CLICK_DOOR = "ClickDoorOpen";
    public static String DOOR_ID = "DoorID";
    public static boolean ack_ok = false;
    private static String ACK_PATH = "/acks";

    private static GoogleApiClient mGoogleApiClient;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        HashMap map = prefs.get_widget(context, appWidgetId);
        if (map.containsKey("caption")) {
            CharSequence widgetText = map.get("caption").toString();
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.up_door_widget);
            views.setTextViewText(R.id.textView, widgetText);

            Intent intent = new Intent(context, upDoor.class);
            intent.setAction(CLICK_DOOR);
            intent.putExtra(DOOR_ID, Integer.toString(appWidgetId));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.houseIcon, pendingIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

            connect_to_data(context, false);
        }
    }

    public static void connect_to_data(final Context context, final boolean is_ack) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.w(prefs.TAG, "Google API con stat: " +Boolean.toString(mGoogleApiClient.isConnected()));
                        Log.w(prefs.TAG, "onConnected connectionHint: " + connectionHint);
                        if (is_ack) {
                            send_back_stats();
                        } else {
                            send_to_wear(context);
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.w(prefs.TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.w(prefs.TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
        Log.w(prefs.TAG, "Google API Connecting");
        mGoogleApiClient.connect();
    }

    private static void send_back_stats() {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), ACK_PATH, Boolean.toString(ack_ok).getBytes()).await();
                    Log.w(prefs.TAG, "Node: " + node.getDisplayName());
                    Log.w(prefs.TAG, "MSG: " + result.getStatus().toString());
                }
            }
        }).start();
    }

    private static void send_to_wear(final Context context) {
        ArrayList<DataMap> all_doors = new ArrayList<>();
        for (Integer i : prefs.get_doors(context)) {
            HashMap dm = prefs.get_widget(context, i);
            DataMap cur_door = new DataMap();
            cur_door.putInt("doorid", i);
            cur_door.putString("caption", dm.get("caption").toString());
            cur_door.putString("name", dm.get("name").toString());
            cur_door.putString("secret", dm.get("secret").toString());
            cur_door.putLong("time", System.currentTimeMillis());
            all_doors.add(cur_door);
        }
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/doors");
        putDataMapReq.getDataMap().putDataMapArrayList("ALL", all_doors);
        putDataMapReq.setUrgent();
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        com.google.android.gms.common.api.PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
        pendingResult.setResultCallback(new  ResultCallback<DataApi.DataItemResult> () {
            @Override
            public void onResult(@NonNull DataApi.DataItemResult result) {
                Log.w(prefs.TAG, "Result: " + result.getStatus().toString());
                Log.w(prefs.TAG, "Reason: " + result.getStatus().getStatusMessage());
                Log.w(prefs.TAG, "Item: " + result.getDataItem().getUri().toString());
                DataMap dataMap = DataMap.fromByteArray(result.getDataItem().getData());
                Log.w(prefs.TAG, "Map: " + dataMap.toString());

            }
        });
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.w(prefs.TAG, "Updating doors - loop");
        for (int appWidgetId : appWidgetIds) {
            Log.w(prefs.TAG, "Door Widget ID:" + Integer.toString(appWidgetId));
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.w(prefs.TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        prefs.clear_all(context);
        connect_to_data(context, false);
    }

    @Override
    public void onDeleted(Context context, int[] WID) {
        for (int i : WID) {
            prefs.del_door(context, i);
        }
        connect_to_data(context, false);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(CLICK_DOOR)) {
            String doorid = intent.getStringExtra(DOOR_ID);
            HashMap map = prefs.get_widget(context, Integer.parseInt(doorid));
            Log.w(prefs.TAG, "onReceive, doorID: " + doorid);
            String[] door = {map.get("name").toString(), map.get("secret").toString()};
            new HttpRequestTask(context, false).execute(door);
            connect_to_data(context, false);
        }
    }

}