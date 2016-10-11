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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetEventsActivity extends AppCompatActivity {
    private ListView lstEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_events);

        lstEvents = (ListView) findViewById(R.id.lstEvents);

        APITask.adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,APITask.list);
        lstEvents.setAdapter(APITask.adapter);

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
                                for(int i = 0; i < APITask.events.size(); i++) {
                                    if(APITask.events.get(i).getString("id").equalsIgnoreCase(obj.get("id").toString()))
                                    {
                                        int result = APITask.doDelete(APITask.events.get(i).getString("id"));
                                        if (result == 0) {
                                            APITask.events.remove(i);
                                            APITask.list.remove(i);
                                            GetEventsActivity.updateViews();
                                            Toast.makeText(getApplicationContext(),"Event Deleted!",Toast.LENGTH_SHORT).show();
                                        } else if (result ==1) {
                                            Toast.makeText(getApplicationContext(),"The server cannot be reached. Please try again later...",Toast.LENGTH_SHORT).show();
                                        }
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

    public static void updateViews() {
        APITask.adapter.notifyDataSetChanged();
    }
}
