package com.appymitchpenney.hiremitch;

import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            File httpCacheDir = new File(this.getCacheDir(), "http");
            long httpCacheSize = 1024 * 1024;
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERROR", "Cache could not be created!");
        }

        try {
            URL url = new URL("http://shrouded-woodland-9458.herokuapp.com/events");
            APITask task = new APITask();
            task.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("ERROR","URL was invalid!");
            this.finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        Log.i("INF","Cache flush?");
        if (cache != null) {
            cache.flush();
            Log.i("INF","Cache flushed!");
        }
    }

    public void getEvents(View view) {
        Intent i = new Intent(this,GetEventsActivity.class);
        startActivity(i);
    }
    public void postEvent(View view) {
        Intent i = new Intent(this,NewEventActivity.class);
        startActivity(i);
    }
}
