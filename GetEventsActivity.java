package com.appymitchpenney.hiremitch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetEventsActivity extends AppCompatActivity {
    ListView lstEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_events);

        lstEvents = (ListView) findViewById(R.id.lstEvents);

        lstEvents.setAdapter(getList());

        lstEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int position, long id) {
                new AlertDialog.Builder(GetEventsActivity.this)
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            JSONObject obj = (JSONObject) adapterView.getAdapter().getItem(position);
                            try {
                                //Log.i("DATA",obj.get("id").toString());
                                for(int i = 0; i < APITask.events.size(); i++) {
                                    if(APITask.events.get(i).get("id").toString().equalsIgnoreCase(obj.get("id").toString()))
                                    {
                                        APITask.doDelete(APITask.events.get(i));
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }})
                    .setNegativeButton("No", null).show();
            }
        });
    }

    private ArrayAdapter getList() {
        List<JSONObject> list = new ArrayList<>();
        try {
            for(JSONObject obj : APITask.events) {
                JSONObject reduced = new JSONObject();
                reduced.put("id",obj.get("id").toString());
                reduced.put("name",obj.get("name").toString());
                list.add(reduced);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        return adapter;
    }
}
