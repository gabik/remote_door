package net.kazav.gabi.remotedoor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gabi on 12/05/2016.
 * General shared preferences
 */
public class prefs extends PreferenceActivity{
    final public static String TAG = "RemoteDoors";
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

    public static List<Integer> get_doors(Context c) {
        List<Integer> keys = new ArrayList<>();
        SharedPreferences sp = c.getSharedPreferences(FILEN, MODE_PRIVATE);
        Map<String, ?> sps = sp.getAll();
        for (Map.Entry<String, ?> e : sps.entrySet()) {
            keys.add(Integer.parseInt(e.getKey()));
        }
        return keys;
    }

    public static void del_door(Context c, int i) {
        SharedPreferences sp = c.getSharedPreferences(FILEN, MODE_PRIVATE);
        sp.edit().remove(Integer.toString(i)).commit();
    }

    public static void clear_all(Context c) {
        SharedPreferences sp = c.getSharedPreferences(FILEN, MODE_PRIVATE);
        sp.edit().clear().commit();
    }
}
