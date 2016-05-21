package net.kazav.gabi.remotedoor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gabi on 12/05/2016.
 *  * General shared preferences
 */
public class prefs extends PreferenceActivity{
    final static String FILEN = "DoOrSpReFs";

    public static void save_door(Context c, int doorid, DataMap data) {
        SharedPreferences sp = c.getSharedPreferences(FILEN, MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        String val = data.getString("caption") + "&" + data.getString("name") + "&" + data.getString("secret") + "&" + Long.toString(data.getLong("time"));
        Log.w(dataLayer.TAG, "Saving: " + val);
        edit.putString(Integer.toString(doorid), val);
        edit.apply();
    }

    public static DataMap get_door(Context c, int doorid){
        SharedPreferences sp = c.getSharedPreferences(FILEN, MODE_PRIVATE);
        DataMap map = new DataMap();
        String[] door ;
        String spdoor = sp.getString(Integer.toString(doorid), "&&&");
        Log.w(dataLayer.TAG, "Door sp str: " + spdoor);
        door = spdoor.split("&");
        if (door.length == 4){
            map.putString("caption", door[0]);
            map.putString("name", door[1]);
            map.putString("secret", door[2]);
            map.putLong("time", Long.parseLong(door[3]));
        }
        return map;
    }

    public static List<Integer> get_doors(Context c) {
        Long currtime = System.currentTimeMillis();
        List<Integer> keys = new ArrayList<>();
        SharedPreferences sp = c.getSharedPreferences(FILEN, MODE_PRIVATE);
        Map<String, ?> sps = sp.getAll();
        for (Map.Entry<String, ?> e : sps.entrySet()) {
            int doorid = Integer.parseInt(e.getKey());
            long timediff = currtime - get_door(c, doorid).getLong("time");
            Log.w(dataLayer.TAG, "Times: " + Long.toString(timediff));
            if (timediff <= 86000000) {
                keys.add(doorid);
            }
        }
        return keys;
    }

    public static void clear_all(Context c) {
        SharedPreferences sp = c.getSharedPreferences(FILEN, MODE_PRIVATE);
        sp.edit().clear().commit();
    }
}
