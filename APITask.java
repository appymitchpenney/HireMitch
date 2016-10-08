package com.appymitchpenney.hiremitch;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class APITask extends AsyncTask {
    private HashSet jsons = new HashSet();
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");

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

            return result;

        } catch (IOException e) {
            Log.e("ERROR:APITask",e.getMessage());
            e.printStackTrace();
        } finally {
            if(con != null) {
                con.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        try {
            JSONArray arr = new JSONArray(o.toString());

            for(int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                HashMap<String,String> map = new HashMap<>();

                Date created = fmt.parse(obj.getString("created_at"));
                Date updated = fmt.parse(obj.getString("updated_at"));

                map.put("id",obj.getString("id"));
                map.put("name",obj.getString("name"));
                map.put("start",obj.getString("start"));
                map.put("end",obj.getString("end"));
                map.put("created_at",new Timestamp(created.getTime()).toString());
                map.put("updated_at",new Timestamp(updated.getTime()).toString());

                jsons.add(map);
                Log.i("DATA",obj.getString("id"));
            }
        } catch (JSONException|ParseException e) {
            e.printStackTrace();
            Log.e("ERROR","JSON conversion failed!");
        }

        MainActivity.txtWorld.setText(jsons.toString());
    }
}
