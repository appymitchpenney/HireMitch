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
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APITask extends AsyncTask {
    public static List<JSONObject> events = new ArrayList<JSONObject>();
    private static URL url;
    private static URI uri;
    private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");

    @Override
    protected Object doInBackground(Object[] objects) {
        url = (URL) objects[0];
        uri = null;
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
                cache = HttpResponseCache.getInstalled().get(uri, "GET", con.getRequestProperties());
                con.connect();

                if (con.getHeaderField("X-Android-Response-Source").equalsIgnoreCase("CONDITIONAL_CACHE 304")) {
                    if(HttpResponseCache.getInstalled() != null) {
                        if (cache != null) {
                            Log.i("INF", "Reading from cache!");
                            return readData(cache.getBody());
                        }
                    }
                } else {
                    Log.i("INF","Reading from HttpConnection!");
                    return readData(con.getInputStream());
                }
            } catch(IOException e) {
                Log.e("DATA_READ",e.getMessage());
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

        if(o != null) {
            try {
                JSONArray arr = new JSONArray(o.toString());

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    /*Date created = FORMAT.parse(obj.getString("created_at"));
                    Date updated = FORMAT.parse(obj.getString("updated_at"));

                    obj.put("created_at", new Timestamp(created.getTime()).toString());
                    obj.put("updated_at", new Timestamp(updated.getTime()).toString());*/

                    events.add(obj);
                }
            } catch (JSONException e) {
                Log.e("JSON", "JSON conversion failed!");
            }
        }
    }

    private String readData(InputStream in) {
        StringBuilder result = new StringBuilder();
        int current;

        try {
            current = in.read();

            while (current != -1) {
                char letter = (char) current;
                result.append(letter);
                current = in.read();
            }
            return result.toString();
        } catch (IOException e) {
            Log.e("ERR","Data read failed!");
        }

        return null;
    }

    public static boolean doDelete(JSONObject obj) {
        MediaType JSON = MediaType.parse("application/json");
        OkHttpClient client = new OkHttpClient();

        Log.i("DATA",obj.toString());
        Response response = null;

        try {
            RequestBody body = RequestBody.create(JSON, obj.toString());
            Request request = new Request.Builder()
                    .url(new URL(url + "/" + obj.get("id").toString()))
                    .delete()
                    .build();
            response = client.newCall(request).execute();
            Log.i("INF",response.message());
            Log.i("INF",String.valueOf(response.code()));
            response.close();
            return true;
        } catch (IOException|JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean doAdd(JSONObject obj) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, obj.toString());
        Response response = null;
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            response = client.newCall(request).execute();
            Log.i("INF",response.message());
            response.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}