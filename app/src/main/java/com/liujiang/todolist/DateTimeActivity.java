package com.liujiang.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class DateTimeActivity extends Activity {

    public static String dateFormat = "%Y/%m/%d";
    public static String timeFormat ="%H时%M分";

    public static String dateTimeFormat(long millis) {
        Time time = new Time();
        time.set(millis);
        return  new String(time.format(dateFormat)+"\n"+time.format(timeFormat));
    }


    public static final String starttime_action = "todolist.addtask.notifystarttime";
    public static final String endtime_action = "todolist.addtask.notifyendtime";
    public static String widgetUdateStartTime = "todolist.appwidget.updatestarttime";
    public static String widgetUpdateEndTime = "todolist.appwidget.updateendtime";

    // for alarm mode
    public static String alarmModes[] = {"不提醒", "准时提醒", "提前五分钟",
            "提前半小时","提前一小时"};

    private static int getAlarmModeIndex(int alarmTime) {
        switch (alarmTime) {
            case -1:
                return 0;
            case 0:
                return 1;
            case 5:
                return 2;
            case 30:
                return 3;
            case 60:
                return 4;
        }
        return 0;
    }

    public static int getAlarmTime(int index) {
        switch (index) {
            case 0:
                return -1;
            case 1:
                return 0;
            case 2:
                return 5;
            case 3:
                return 30;
            case 4:
                return 60;
        }
        return -1;
    }

    public static String getAlarmMode(int alarmTime) {
        return alarmModes[getAlarmModeIndex(alarmTime)];
    }

    //public String endTimeDate = "";
    //public String endTimeTime = "";

    int date_year,date_month,date_day,date_hour,date_min;
    boolean isFromWidget = true;
    boolean isStartTime = true;
    int position = -1;      //only for date time from AppWidget.
    int alarmTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time);

        Intent intent = getIntent();
        isFromWidget = intent.getExtras().getBoolean("isWidget");
        isStartTime  = intent.getExtras().getBoolean("isStartTime");
        alarmTime = intent.getExtras().getInt("alarmTime");


        Time time = new Time();
        time.set(intent.getExtras().getLong("date_time") );
        date_year = time.year;
        date_month = time.month;
        date_day = time.monthDay;
        date_hour = time.hour;
        date_min = time.minute;


        if(isFromWidget == true) {
            position = intent.getExtras().getInt("pos");
        }


        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,alarmModes);
        Spinner mSpinner = (Spinner) findViewById(R.id.endTimeAlarmSpinner);
        mSpinner.setAdapter(mArrayAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String mode = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),"提醒模式 ："+mode,2000).show();
                alarmTime = getAlarmTime(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Log.e("DTA==>alarmTime是：--------------》", "" + alarmTime);
        mSpinner.setSelection(getAlarmModeIndex(alarmTime));


        // for Calendar View
        CalendarView mCalendarView = (CalendarView) findViewById(R.id.endTimeCalendarView);
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(date_year,date_month,date_day);
        mCalendarView.setDate(mCalendar.getTimeInMillis());
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String date = year + "年" + month + "月" + dayOfMonth + "日";
                Toast.makeText(getApplicationContext(), date, 0).show();
                date_year = year;
                date_month = month;
                date_day = dayOfMonth;
            }
        });


        // for TimePicker
        TimePicker timePicker = (TimePicker) findViewById(R.id.endTimePicker);
        timePicker.setCurrentHour(date_hour);
        timePicker.setCurrentMinute(date_min);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String date = hourOfDay + "时" + minute + "分";
                Toast.makeText(getApplicationContext(), date, 0).show();

                date_hour = hourOfDay;
                date_min = minute;
            }
        });


        // for End button OK
        Button endOK = (Button) findViewById(R.id.endOk);
        endOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Time time = new Time();
                time.set(0,date_min,date_hour,date_day,date_month,date_year);

                Intent intent = new Intent();

                if (isFromWidget == false) {
                    if(isStartTime == true) {
                        intent.setAction(starttime_action);
                    }
                    else {
                        intent.setAction(endtime_action);
                    }
                }
                else {
                    if(isStartTime == true) {
                        intent.setAction(widgetUdateStartTime);
                        intent.putExtra("pos", position);
                    }
                    else {
                        intent.setAction(widgetUpdateEndTime);
                        intent.putExtra("pos", position);
                    }
                }
                intent.putExtra("alarmTime", alarmTime);
                intent.putExtra("date_time",time.toMillis(false));
                sendBroadcast(intent);

                finish();
            }
        });

        Button endCancel = (Button) findViewById(R.id.endCancel);
        endCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_end_time, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
