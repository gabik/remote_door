package remotedoors.gabi.kazav.net.remotedoors;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequestTask extends AsyncTask<String, Void, String> {
    Context c;
    boolean is_watch = false;

    public HttpRequestTask(Context cont, boolean is_from_watch) {
        c=cont;
        is_watch = is_from_watch;
    }

    protected String doInBackground(String... doorId) {
        String name = doorId[0];
        String secret  = doorId[1];
        String restxt;
        try{
            URL url = new URL("https://agent.electricimp.com/4J1Ll7fzdPRn?operation=open&code=" + secret + name + secret);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            Log.w(prefs.TAG, "Sending request to URL: " + url.toString());
            Log.w(prefs.TAG, "Response Code: " + Integer.toString(responseCode));
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            restxt = response.toString();
            Log.w(prefs.TAG, "Response: " + restxt);

        } catch (IOException e) {
            Log.e(prefs.TAG, "Cannot do anything.. " + e.toString());
            return "";
        }
        return restxt;
    }

    protected void onPostExecute(String retxt) {
        Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
        if (retxt.equals("YES")) {
            long[] ptrn = {0, 50,100,60,100,80};
            v.vibrate(ptrn, -1);
            if (is_watch) { upDoor.ack_ok = true; upDoor.connect_to_data(c, true); }
        } else if (retxt.equals("NO")) {
            Log.w(prefs.TAG, "Door cannot be found: " + retxt);
            Toast.makeText(c.getApplicationContext(), "הדלת אינה מחוברת", Toast.LENGTH_LONG).show();
            v.vibrate(1000);
            if (is_watch) { upDoor.ack_ok = false; upDoor.connect_to_data(c, true); }
        } else {
            v.vibrate(1000);
            if (is_watch) { upDoor.ack_ok = false; upDoor.connect_to_data(c, true); }
        }
    }
}