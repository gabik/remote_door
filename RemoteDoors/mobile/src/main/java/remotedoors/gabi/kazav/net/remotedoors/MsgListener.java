package remotedoors.gabi.kazav.net.remotedoors;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.HashMap;

/**
 * Created by gabik on 5/20/16.
 * Rcv Msg
 */
public class MsgListener extends WearableListenerService {

    private static final String OPEN_DOOR = "/open/";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().startsWith(OPEN_DOOR)) {
            String doorid = messageEvent.getPath().split("/")[2];
            HashMap map = prefs.get_widget(this, Integer.parseInt(doorid));
            Log.w(prefs.TAG, "onReceive, doorID: " + doorid);
            String[] door = {map.get("name").toString(), map.get("secret").toString()};
            new HttpRequestTask(this, true).execute(door);
        }
    }
}
