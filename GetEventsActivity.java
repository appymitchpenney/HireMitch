package com.appymitchpenney.hiremitch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

        lstEvents.setAdapter(getList());

        lstEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                new AlertDialog.Builder(GetEventsActivity.this)
                        .setTitle("Delete Event")
                        .setMessage("Are you sure you want to delete this event?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }})
                        .setNegativeButton("No", null).show();
            }
        });
    }

    private ArrayAdapter getList() {
        List<String> list = new ArrayList<String>(APITask.events);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        return adapter;
    }
}
