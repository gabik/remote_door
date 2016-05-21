package net.kazav.gabi.remotedoor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;

import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.HashMap;
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
        String val = data.getString("caption") + "&" + data.getString("doorname") + "&" + data.getString("secret");
        edit.putString(Integer.toString(doorid), val);
        edit.apply();
    }

    public static DataMap get_door(Context c, int doorid){
        SharedPreferences sp = c.getSharedPreferences(FILEN, MODE_PRIVATE);
        DataMap map = new DataMap();
        String[] door ;
        if (doorid == 999) { door = "Test&UP&1234".split("&"); }
        else { door = sp.getString(Integer.toString(doorid), "&&").split("&"); }
        if (door.length == 3){
            map.putString("caption", door[0]);
            map.putString("name", door[1]);
            map.putString("secret", door[2]);
        }
        return map;
    }

    public static List<Integer> get_doors(Context c) {
        List<Integer> keys = new ArrayList<>();
        SharedPreferences sp = c.getSharedPreferences(FILEN, MODE_PRIVATE);
        Map<String, ?> sps = sp.getAll();
        for (Map.Entry<String, ?> e : sps.entrySet()) {
            keys.add(Integer.parseInt(e.getKey()));
        }
        keys.add(999);
        return keys;
    }
}
