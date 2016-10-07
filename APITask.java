package com.appymitchpenney.hiremitch;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class APITask extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] objects) {
        HttpURLConnection con = null;
        StringBuilder result = new StringBuilder();

        try {
            int current;
            URL url = (URL) objects[0];
            con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(true);
            InputStream in = con.getInputStream();

            current = in.read();

            while(current != -1) {
                char letter = (char) current;
                result.append(letter);
                current = in.read();
            }

            return result.toString();

        } catch (Exception e) {
            Log.e("ERROR:APITask",e.getMessage());
            e.printStackTrace();
        } finally {
            if(con != null) {
                con.disconnect();
            }
        }

        return null;
    }
}
