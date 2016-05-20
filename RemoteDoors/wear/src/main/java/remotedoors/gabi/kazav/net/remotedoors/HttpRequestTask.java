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

    public HttpRequestTask(Context cont) {c=cont;}
    protected String doInBackground(String... doorId) {
        String name = doorId[0];
        String secret  = doorId[1];
        String restxt;
        try{
            URL url = new URL("https://agent.electricimp.com/4J1Ll7fzdPRn?operation=open&code=" + secret + name + secret);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            Log.w(dataLayer.TAG, "Sending request to URL: " + url.toString());
            Log.w(dataLayer.TAG, "Response Code: " + Integer.toString(responseCode));
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            restxt = response.toString();
            Log.w(dataLayer.TAG, "Response: " + restxt);

        } catch (IOException e) {
            Log.e(dataLayer.TAG, "Cannot do anything.. " + e.toString());
            return "";
        }
        return restxt;
    }

    protected void onPostExecute(String retxt) {
        Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
        if (retxt.equals("YES")) {
            long[] ptrn = {0, 50,100,60,100,80};
            v.vibrate(ptrn, -1);
        } else if (retxt.equals("NO")) {
            Log.w(dataLayer.TAG, "Door cannot be found: " + retxt);
            Toast.makeText(c.getApplicationContext(), "הדלת אינה מחוברת", Toast.LENGTH_LONG).show();
            v.vibrate(1000);
        } else {
            v.vibrate(1000);
        }
    }
}