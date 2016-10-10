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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APITask extends AsyncTask {
    public static HashSet events = new HashSet();
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
                    HashMap<String, String> map = new HashMap<>();

                    Date created = FORMAT.parse(obj.getString("created_at"));
                    Date updated = FORMAT.parse(obj.getString("updated_at"));

                    map.put("id", obj.getString("id"));
                    map.put("name", obj.getString("name"));
                    map.put("start", obj.getString("start"));
                    map.put("end", obj.getString("end"));
                    map.put("created_at", new Timestamp(created.getTime()).toString());
                    map.put("updated_at", new Timestamp(updated.getTime()).toString());

                    events.add(map);
                }
            } catch (JSONException | ParseException e) {
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

    public static void doDelete(String id) {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");
            con.setRequestProperty("id",id);
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            Log.i("INF",String.valueOf(con.getResponseCode()));
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void doAdd(JSONObject obj) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, obj.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Log.i("INF",response.message());
        } catch (IOException e) {
            e.printStackTrace();
        }



            /*HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("name",obj.getString("name"));
            con.setRequestProperty("start","2010-10-12 10:45:00");
            con.setRequestProperty("end","2010-10-13 10:46:00");
            con.setUseCaches(false);
            //con.setDoInput(true);
            //con.setDoOutput(true);
            Log.i("INF",String.valueOf(con.getResponseCode()));
            InputStream is = con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            Log.i("RESP",line);

            is = con.getErrorStream();
            rd = new BufferedReader(new InputStreamReader(is));
            line = null;
            response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            Log.e("ERR",line);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/



        /*con.connect();
        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(data); // data is the post data to send
        wr.flush();*/


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

/* if (con.getResponseCode() == UNPROCESSABLE_ENTITY) {
throw new IOException("HTTP_UNPROCESSABLE_ENTITY");*/


/*
HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");

        JSONObject cred   = new JSONObject();
        JSONObject auth   = new JSONObject();
        JSONObject parent = new JSONObject();

        cred.put("username","adm");
        cred.put("password", "pwd");

        auth.put("tenantName", "adm");
        auth.put("passwordCredentials", cred.toString());

        parent.put("auth", auth.toString());

        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(parent.toString());
        wr.flush();

//display what returns the POST request

        StringBuilder sb = new StringBuilder();
        int HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            System.out.println("" + sb.toString());
        } else {
            System.out.println(con.getResponseMessage());
        }

*/
