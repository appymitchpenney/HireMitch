package com.appymitchpenney.hiremitch;

import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

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
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APITask extends AsyncTask {
    public static List<JSONObject> events = new ArrayList<>();
    public static List<JSONObject> list = new ArrayList<>();
    public static ArrayAdapter adapter = null;

    private static URL url;
    private static URI uri;

    @Override
    protected Object doInBackground(Object[] objects) {
        url = (URL) objects[0];
        uri = null;
        HttpURLConnection con = null;
        CacheResponse cache = null;

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
                Log.e("INTERNET","No internet connection available. Using cached data.");
                try {
                    if (HttpResponseCache.getInstalled() != null) {
                        if (cache != null) {
                            Log.i("INF", "Reading from cache!");
                            return readData(cache.getBody());
                        }
                    }
                } catch (IOException ioe) {
                    Log.e("CACHE","Cached data couldn't be read!");
                }
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
            assignData(o.toString());
        }
    }

    protected static String readData(InputStream in) {
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

    protected static void assignData(String o) {
        try {
            events.clear();
            list.clear();
            JSONArray arr = new JSONArray(o);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                events.add(obj);

                JSONObject reduced = new JSONObject();
                reduced.put("id",obj.get("id").toString());
                reduced.put("name",obj.get("name").toString());
                list.add(reduced);
            }
        } catch (JSONException e) {
            Log.e("JSON", "JSON conversion failed!");
        }
    }

    public static int doDelete(String id) {
        OkHttpClient client = new OkHttpClient();

        try {
            Request request = new Request.Builder()
                    .url(new URL(url + "/" + id))
                    .delete()
                    .build();
            Response response = client.newCall(request).execute();
            response.close();
            return 0;
        } catch (IOException e) {
            return 1;
        }
    }

    public static int doAdd(final JSONObject obj) {

        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, obj.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if(response.code() != 422) {
                HttpURLConnection con = null;
                try {
                    con = (HttpURLConnection) url.openConnection();
                    con.connect();

                    assignData(readData(con.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
                response.close();
                return 0;
            } else {
                Log.i("INF",String.valueOf(response.code()));
                response.close();
                return 1;
            }
        } catch (IOException e) {
            return 2;
        }
    }
}