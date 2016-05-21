package net.kazav.gabi.remotedoor;

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
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of App Widget functionality.
 */
public class upDoor extends AppWidgetProvider {
    public static String TAG = "Remote door GAPI";
    public static String CLICK_DOOR = "ClickDoorOpen";
    public static String DOOR_ID = "DoorID";
    private static GoogleApiClient mGoogleApiClient;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
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

            connect_to_data(context, map, appWidgetId);
        }
    }

    private static void connect_to_data(final Context context, final HashMap map, final int appWidgetId) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.w("Google API", Boolean.toString(mGoogleApiClient.isConnected()));
                        Log.d(TAG, "onConnected: " + connectionHint);
                        send_to_wear(map, appWidgetId);
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
        Log.w("Google API", "Connecting");
        mGoogleApiClient.connect();
    }

    private static void send_to_wear(HashMap map, int appWidgetId) {
        com.google.android.gms.common.api.ResultCallback<DataApi.DataItemResult> send_callback =
                new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult result) {
                        Log.w("Result", result.getStatus().toString());
                        Log.w("Reason", result.getStatus().getStatusMessage());
                        Log.w("Item", result.getDataItem().getUri().toString());
                        DataMap dataMap = DataMap.fromByteArray(result.getDataItem().getData());
                        Log.w("map", dataMap.toString());

                    }
                };
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/doors/" + Integer.toString(appWidgetId));
        putDataMapReq.getDataMap().putString("caption", map.get("caption").toString());
        putDataMapReq.getDataMap().putString("name", map.get("name").toString());
        putDataMapReq.getDataMap().putString("secret", map.get("secret").toString());
        putDataMapReq.getDataMap().putLong("Time", System.currentTimeMillis());
        putDataMapReq.setUrgent();
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        com.google.android.gms.common.api.PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
        pendingResult.setResultCallback(send_callback);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //super.onUpdate(context, appWidgetManager, appWidgetIds);
        // There may be multiple widgets active, so update all of them
        Log.w("Updating doors", "OK");
        for (int appWidgetId : appWidgetIds) {
            Log.w("Door Widget ID", Integer.toString(appWidgetId));
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.w("Enabled", "Door");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(CLICK_DOOR)) {
            String doorid = intent.getStringExtra(DOOR_ID);
            HashMap map = prefs.get_widget(context, Integer.parseInt(doorid));
            Log.w("doorID", doorid);
            String[] door = {map.get("name").toString(), map.get("secret").toString()};
            new HttpRequestTask(context).execute(door);
            connect_to_data(context, map, Integer.parseInt(doorid));
        }
    }
}