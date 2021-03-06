package net.kazav.gabi.remotedoor;

import android.content.SharedPreferences;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.HashMap;

/**
 * Created by gabi on 12/05/2016.
 * General shared preferences
 */
public class prefs extends PreferenceActivity{
    final static String FILEN = "DoOrSpReFs";

    public static void save_widget(Context c, int doorid, String caption, String doorname, String secret) {
        SharedPreferences sp = c.getSharedPreferences(FILEN, MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        String val = caption + "&" + doorname + "&" + secret;
        edit.putString(Integer.toString(doorid), val);
        edit.apply();
    }

    public static HashMap<String, String> get_widget(Context c, int doorid){
        SharedPreferences sp = c.getSharedPreferences(FILEN, MODE_PRIVATE);
        HashMap<String, String> map = new HashMap<>();
        String[] door = sp.getString(Integer.toString(doorid), "&&").split("&");
        if (door.length == 3){
            map.put("caption", door[0]);
            map.put("name", door[1]);
            map.put("secret", door[2]);
        }
        return map;
    }
}
