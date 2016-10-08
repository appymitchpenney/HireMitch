package com.appymitchpenney.hiremitch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class GetEventsActivity extends AppCompatActivity {
    ListView lstEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_events);

        lstEvents = (ListView) findViewById(R.id.lstEvents);

        List<String> list = new ArrayList<String>(APITask.events);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        adapter.notifyDataSetChanged();
        lstEvents.setAdapter(adapter);
    }
}
