package com.appymitchpenney.hiremitch;

import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.CacheResponse;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class APITask extends AsyncTask {
    private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
    private final int UNPROCESSABLE_ENTITY = 422;
    private HashSet jsons = new HashSet();

    @Override
    protected Object doInBackground(Object[] objects) {
        URL url = (URL) objects[0];
        URI uri = null;
        HttpURLConnection con = null;
        CacheResponse cache;

        try {
            uri = new URI(url.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("ERR","URI could not be created from URL!");
        }

        if(uri != null) {
            try {
                con = (HttpURLConnection) url.openConnection();
                con.setUseCaches(true);

                if(HttpResponseCache.getInstalled() != null) {
                    cache = HttpResponseCache.getInstalled().get(uri, "GET", con.getRequestProperties());

                    if (cache != null) {
                        Log.i("INF","Reading from cache!");
                        return readData(cache.getBody());
                    }
                }

                if (con.getResponseCode() == UNPROCESSABLE_ENTITY) {
                    throw new IOException();
                } else if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new IOException();
                } else {
                    Log.i("INF","Reading from HttpConnection!");
                    return readData(con.getInputStream());
                }
            } catch(IOException e) {
                e.printStackTrace();
                Log.e("ERR","HTTP Response was NOT OK!");
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        if (o != null) {
            try {
                JSONArray arr = new JSONArray(o.toString());

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<>();

                    Date created = FORMAT.parse(obj.getString("created_at"));
                    Date updated = FORMAT.parse(obj.getString("updated_at"));

                    map.put("id", obj.getString("id"));
                    map.put("name", obj.getString("name"));
                    map.put("start", obj.getString("start"));
                    map.put("end", obj.getString("end"));
                    map.put("created_at", new Timestamp(created.getTime()).toString());
                    map.put("updated_at", new Timestamp(updated.getTime()).toString());

                    jsons.add(map);
                    Log.i("DATA", obj.getString("id"));
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
                Log.e("ERROR", "JSON conversion failed!");
            }

            MainActivity.txtWorld.setText(jsons.toString());
        }
    }

    private String readData(InputStream in) {
        StringBuilder result = new StringBuilder();
        int current = 0;

        try {
            current = in.read();

            while (current != -1) {
                char letter = (char) current;
                result.append(letter);
                current = in.read();
            }

            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERR","Data read failed!");
        }

        return null;
    }
}

//Long expires = con.getHeaderFieldDate("Expires",System.currentTimeMillis());
//con.setUseCaches(true);

/*if(HttpResponseCache.getInstalled() != null) {
        cache = HttpResponseCache.getInstalled().get(uri,"GET",con.getRequestProperties());
        if (cache != null) {
        Map<String,List<String>> headers = cache.getHeaders();
        if (headers.containsKey("Last-Modified")) {
        List<String> modifyList = headers.get("Last-Modified");
        if (modifyList != null && !modifyList.isEmpty()) {
        String modified = modifyList.get(0);
        if (modified != null) {
        con.addRequestProperty("If-Modified-Since",modified);
        }
        }
        }
        }
        }*/
