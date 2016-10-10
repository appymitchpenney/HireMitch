package com.appymitchpenney.hiremitch;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewEventActivity extends AppCompatActivity {
    private EditText txtName;
    private TextView viewStartDate,viewStartTime, viewEndDate, viewEndTime;
    private Button btnStartDate, btnEndDate, btnStartTime, btnEndTime;

    private DatePickerDialog fromDatePickerDialog, toDatePickerDialog;
    private TimePickerDialog fromTimePickerDialog, toTimePickerDialog;
    private SimpleDateFormat dateFormatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        txtName = (EditText) findViewById(R.id.txtEnterName);
        viewStartDate = (TextView) findViewById(R.id.viewStartDate);
        viewStartTime = (TextView) findViewById(R.id.viewStartTime);
        viewEndDate = (TextView) findViewById(R.id.viewEndDate);
        viewEndTime = (TextView) findViewById(R.id.viewEndTime);
        btnStartDate = (Button) findViewById(R.id.btnStartDate);
        btnEndDate = (Button) findViewById(R.id.btnEndDate);
        btnStartTime = (Button) findViewById(R.id.btnStartTime);
        btnEndTime = (Button) findViewById(R.id.btnEndTime);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);

        setDateTimePickers();
    }

    private void setDateTimePickers() {
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                viewStartDate.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                viewEndDate.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        fromTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                viewStartTime.setText(selectedHour + ":" + selectedMinute);
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);

        toTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                viewEndTime.setText(selectedHour + ":" + selectedMinute);
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);
    }

    public void addNew(View view) {
        Log.i("DATA:name",txtName.getText().toString());
        Log.i("DATA:strtD",viewStartDate.getText().toString());
        Log.i("DATA:strtT",viewStartTime.getText().toString());
        Log.i("DATA:endD",viewEndDate.getText().toString());
        Log.i("DATA:endT",viewEndTime.getText().toString());
        /*JSONObject obj = new JSONObject();
        try {
            obj.put("name","TEST_NAME");
            obj.put("start","2010-10-11 10:45:00");
            obj.put("end","2010-10-12 10:46:00");
            APITask.doAdd(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    public void changeDate(View view) {
        if(view == btnStartDate) {
            fromDatePickerDialog.show();
        } else if (view == btnEndDate) {
            toDatePickerDialog.show();
        }
    }

    public void changeTime(View view) {
        if(view == btnStartTime) {
            fromTimePickerDialog.show();
        } else if (view == btnEndTime) {
            toTimePickerDialog.show();
        }
    }
}
