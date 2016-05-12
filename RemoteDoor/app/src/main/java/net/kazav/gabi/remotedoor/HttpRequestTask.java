package net.kazav.gabi.remotedoor;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gabi on 12/05/2016.
 */
public  class HttpRequestTask extends AsyncTask<String, Void, Boolean> {
    protected Boolean doInBackground(String... doorId) {
        String name = doorId[0];
        String secret  = doorId[1];
        try{
            URL url = new URL("http://c.gabi.ninja:8080/test/" + secret + name + secret);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            Log.w("Sending request to URL", url.toString());
            Log.w("Response Code", Integer.toString(responseCode));
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.w("Response", response.toString());
        } catch (IOException e) {
            Log.e("Cannot do anything..", e.toString());
            return false;
        }
        return true;
    }

    protected void onPostExecute(Boolean state) {

    }
}