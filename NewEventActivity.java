package com.appymitchpenney.hiremitch;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
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

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

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
                String buffer = "";
                String hourBuffer = "";
                if(selectedMinute < 10) {
                    buffer = "0";
                }
                if(selectedHour < 10) {
                    hourBuffer = "0";
                }
                viewStartTime.setText(hourBuffer + selectedHour + ":" + buffer + selectedMinute);
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);

        toTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String buffer = "";
                String hourBuffer = "";
                if(selectedMinute < 10) {
                    buffer = "0";
                }
                if(selectedHour < 10) {
                    hourBuffer = "0";
                }
                viewEndTime.setText(hourBuffer + selectedHour + ":" + buffer + selectedMinute);
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);
    }

    public void addNew(View view) {
        JSONObject obj = new JSONObject();
        boolean valid = true;
        StringBuilder message = new StringBuilder();
        if(viewStartDate.getText().toString().equalsIgnoreCase("")) {
            message.append("> Start date not valid.\n");
            valid = false;
        }
        if(viewStartTime.getText().toString().equalsIgnoreCase("")) {
            message.append("> Start time not valid.\n");
            valid = false;
        }
        if(viewEndDate.getText().toString().equalsIgnoreCase("")) {
            message.append("> End date not valid.\n");
            valid = false;
        }
        if(viewEndTime.getText().toString().equalsIgnoreCase("")) {
            message.append("> End time not valid.\n");
            valid = false;
        }
        if(txtName.getText().toString().equalsIgnoreCase("")) {
            message.append("> Event name not valid.\n");
            valid = false;
        }

        if(valid) {
            try {
                obj.put("name", txtName.getText().toString());
                obj.put("start", viewStartDate.getText().toString() + " " + viewStartTime.getText().toString() + ":00");
                obj.put("end", viewEndDate.getText().toString() + " " + viewEndTime.getText().toString() + ":00");
                int result = APITask.doAdd(obj);
                if(result == 0) {
                    Toast.makeText(this, "Event added successfully!", Toast.LENGTH_SHORT).show();
                } else if (result == 1) {
                    Toast.makeText(this, "The start of the event cannot be after the end of the event!", Toast.LENGTH_SHORT).show();
                } else if (result == 2) {
                    Toast.makeText(this, "The server cannot be reached. Please try again later...", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            message.delete(message.length() - 1, message.length());
            Toast.makeText(this, "Event not valid: \n" + message.toString(),Toast.LENGTH_SHORT).show();
        }
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
