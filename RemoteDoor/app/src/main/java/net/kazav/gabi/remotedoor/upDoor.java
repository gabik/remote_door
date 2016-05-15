package net.kazav.gabi.remotedoor;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Implementation of App Widget functionality.
 */
public class upDoor extends AppWidgetProvider {
    public static String CLICK_DOOR = "ClickDoorOpen";
    public static String DOOR_ID = "DoorID";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

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
        }
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
        }
    }
}