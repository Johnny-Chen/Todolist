package com.liujiang.todolist;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.liujiang.todolist.json.WriteJson;
import com.liujiang.todolist.operation.Operaton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class AddTaskActivity extends Activity {

    public static int TASK_ONGOING = 0;
    public static int TASK_PENDDING = 1;
    public static int TASK_FINISH = 2;

    public final static int ImportantAndUrgent =0;
    public final static int ImportantAndNotUrgent =1;
    public final static int NotImportantAndUrgent =2;
    public final static int NotImportantAndNotUrgent =3;


    TextView  startTimeTextView   = null;
    Button    startTimeButton     = null;
    Button    endTimeButton       = null;
    TextView  endTimeTextView     = null;
    RadioGroup radioGroup;
    RadioButton radio1,radio2,radio3,radio4;

    EditText taskEditText = null;

    boolean isEdit;
    Agenda agenda;
    int ID;
    String topic = "";
    Long starttime_l,endtime_l;
    int importance = ImportantAndNotUrgent;
    int startAlarmTime = -1;
    int endAlarmTime = -1;

    private void setAllTaskViews() {
        if (isEdit == false) {      //采用default策略,后期还有模板策略
            topic = "";
            Time time = new Time();
            time.setToNow();
            starttime_l = time.toMillis(false);
            endtime_l = starttime_l + 3600*1000;
            importance = ImportantAndNotUrgent;
            startAlarmTime = -1;
            endAlarmTime = -1;
        } else {
            topic = agenda.getTopic();
            starttime_l = agenda.getStart_time();
            endtime_l = agenda.getEnd_time();
            importance = agenda.getImportance();
            startAlarmTime = agenda.getStart_alarm();
            endAlarmTime = agenda.getEnd_alarm();
        }

        switch (importance) {
            case ImportantAndUrgent:
                radio1.setChecked(true);
                break;
            case ImportantAndNotUrgent:
                radio2.setChecked(true);
                break;
            case NotImportantAndUrgent:
                radio3.setChecked(true);
                break;
            case NotImportantAndNotUrgent:
                radio4.setChecked(true);
                break;
        }

        taskEditText.setText(topic);
        startTimeButton.setText(DateTimeActivity.dateTimeFormat(starttime_l));
        endTimeButton.setText(DateTimeActivity.dateTimeFormat(endtime_l));
        startTimeTextView.setText("开始时间 : \n"+DateTimeActivity.getAlarmMode(startAlarmTime));
        endTimeTextView.setText("结束时间 : \n"+DateTimeActivity.getAlarmMode(endAlarmTime));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Fetch the intend info
        Intent intent = getIntent();
        if (intent != null) {
            //isEdit = intent.getExtras().getBoolean("isEdit");
            isEdit = intent.getBooleanExtra("isEdit",false);
            if (isEdit == true) {
                /*agenda = (Agenda) intent.getSerializableExtra("agenda");*/
                agenda = (Agenda) intent.getExtras().getSerializable("agenda");
                ID = agenda.getID();
                topic = agenda.getTopic();
                starttime_l = agenda.getStart_time();
                endtime_l = agenda.getEnd_time();
            }
        }

        // for RadioGroup
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        radio1=(RadioButton)findViewById(R.id.radioButton1);
        radio2=(RadioButton)findViewById(R.id.radioButton2);
        radio3=(RadioButton)findViewById(R.id.radioButton3);
        radio4=(RadioButton)findViewById(R.id.radioButton4);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.radioButton1:
                        Toast.makeText(getApplicationContext(),
                                "我选择的是:"+radio1.getText() , 0).show();
                        importance = ImportantAndUrgent;
                        break;
                    case R.id.radioButton2:
                        Toast.makeText(getApplicationContext(),
                                "我选择的是:"+radio2.getText() , 0).show();
                        importance = ImportantAndNotUrgent;
                        break;
                    case R.id.radioButton3:
                        Toast.makeText(getApplicationContext(),
                                "我选择的是:"+radio3.getText() , 0).show();
                        importance = NotImportantAndUrgent;
                        break;
                    case R.id.radioButton4:
                        Toast.makeText(getApplicationContext(),
                                "我选择的是:"+radio4.getText() , 0).show();
                        importance = NotImportantAndNotUrgent;
                        break;
                }
            }
        });
        //注意数据来源不同的策略
        //radio1.setChecked(true);

        // For start time and end time buttons

        startTimeButton      = (Button) findViewById(R.id.starttime_button);
        endTimeButton        = (Button) findViewById(R.id.endtime_button);
        startTimeTextView  = (TextView) findViewById(R.id.starttime_textView);
        endTimeTextView    = (TextView) findViewById(R.id.endtime_textView);

        // init value
        /*startTimeTextView.setText("开始时间 : \n"+"foo");
        endTimeTextView.setText("结束时间 : \n"+"foo");*/


        /*if( isEdit == false) {
            Time time = new Time();
            time.setToNow();
            starttime_l = time.toMillis(false);
            endtime_l = starttime_l + 3600*1000;
        }
        startTimeButton.setText(DateTimeActivity.dateTimeFormat(starttime_l));
        endTimeButton.setText(DateTimeActivity.dateTimeFormat(endtime_l));*/

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(AddTaskActivity.this, StartTimeActivity.class);
                Intent intent = new Intent(AddTaskActivity.this, DateTimeActivity.class);
                intent.putExtra("isWidget", false);
                intent.putExtra("isStartTime",true);
                intent.putExtra("date_time",starttime_l);
                intent.putExtra("alarmTime",startAlarmTime);
                AddTaskActivity.this.startActivity(intent);
            }
        });

        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTaskActivity.this, DateTimeActivity.class);
                intent.putExtra("isWidget",false);
                intent.putExtra("isStartTime",false);
                intent.putExtra("date_time",endtime_l);
                intent.putExtra("alarmTime",endAlarmTime);
                AddTaskActivity.this.startActivity(intent);
            }
        });

        IntentFilter startFilter = new IntentFilter(DateTimeActivity.starttime_action);
        IntentFilter endFilter = new IntentFilter(DateTimeActivity.endtime_action);
        registerReceiver(broadcastReceiver,startFilter);
        registerReceiver(broadcastReceiver,endFilter);

        taskEditText = (EditText) findViewById(R.id.taskEditText);

        Button buttonOK = (Button) findViewById(R.id.button_OK);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // upload the agenda data to server
                new Thread(new Runnable() {

                    public void run() {

                        Operaton operaton=new Operaton();

                        Agenda mAgenda;

                        if( isEdit == false)
                            mAgenda = new Agenda(0,taskEditText.getText().toString(),
                                "公司","我",starttime_l,endtime_l,startAlarmTime,endAlarmTime,0,
                                "","","",importance,"",AddTaskActivity.TASK_ONGOING);
                        else
                            mAgenda = new Agenda(agenda.getID(),taskEditText.getText().toString(),
                                    "公司","我",starttime_l,endtime_l,startAlarmTime,endAlarmTime,0,
                                    "","","",importance,"",agenda.getStatus());
                        List<Agenda> list = new ArrayList<Agenda>();
                        list.add(mAgenda);

                        WriteJson writeJson=new WriteJson();
                        //将user对象写出json形式字符串
                        String jsonString= writeJson.getJsonData(list);
                        System.out.println(jsonString);

                        String result;
                        if(isEdit == false)
                            result= operaton.uploadAgenda("AgendaServlet", jsonString);
                        else
                            result= operaton.uploadAgenda("UpdateAgenda", jsonString);
                        Message msg=new Message();
                        Log.e("buttonOK.setOnClickListener----result---->",result);
                        msg.obj=result;
                        handler1.sendMessage(msg);
                    }
                }).start();



                finish();
            }
        });

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseHelper databaseHelper = new DatabaseHelper(AddTaskActivity.this);
                SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
                //Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_NAME,new String[]{DatabaseHelper.FIELD_START_TIME,DatabaseHelper.FIELD_TITLE},DatabaseHelper.FIELD_ID+"=?",new String[]{"*"},null,null,null);
                //Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_NAME,new String[]{DatabaseHelper.FIELD_START_TIME,DatabaseHelper.FIELD_TITLE},null,null,null,null,null);
                Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_NAME,
                        new String[]{DatabaseHelper.FIELD_TITLE,DatabaseHelper.FIELD_START_TIME,DatabaseHelper.FIELD_END_TIME,DatabaseHelper.FIELD_STATUS},
                        DatabaseHelper.FIELD_STATUS+"=?",new String[]{"0"},
                        null,null,null);

                while (cursor.moveToNext()){
                    Long millis = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.FIELD_START_TIME));
                    String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_TITLE));

                    Time starttime = new Time();
                    starttime.set(millis);

                    millis = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.FIELD_END_TIME));
                    Time endtime = new Time();
                    endtime.set(millis);

                    int status = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FIELD_STATUS));

                    Log.e("查询测试******************##############",""+millis);
                    Log.e("查询测试******************任务标题",""+title);
                    Log.e("查询测试******************开始时间",""+starttime.format(DateTimeActivity.dateFormat)+" "+starttime.format(DateTimeActivity.timeFormat) );
                    Log.e("查询测试******************结束时间",""+endtime.format(DateTimeActivity.dateFormat)+" "+endtime.format(DateTimeActivity.timeFormat) );
                    if( status == 0)
                    {
                        Log.e("查询测试******************任务状态","正在进行");
                    }
                }

            }
        });

        setAllTaskViews();

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(getApplicationContext(),
                    "收到广播了" , 0).show();
            //startTimeTextView.setText("开始时间 : \n"+intent.getExtras().getString("endTimeAlarmMode"));
            if(intent.getAction().equals(DateTimeActivity.starttime_action)) {
                starttime_l = intent.getExtras().getLong("date_time");
                startAlarmTime = intent.getExtras().getInt("alarmTime");

                startTimeTextView.setText("开始时间 : \n" + DateTimeActivity.getAlarmMode(startAlarmTime));
                startTimeButton.setText(DateTimeActivity.dateTimeFormat(starttime_l));
            }
            if(intent.getAction().equals(DateTimeActivity.endtime_action)) {
                endtime_l = intent.getExtras().getLong("date_time");
                endAlarmTime = intent.getExtras().getInt("alarmTime");

                endTimeTextView.setText("结束时间 : \n" + DateTimeActivity.getAlarmMode(endAlarmTime));
                endTimeButton.setText(DateTimeActivity.dateTimeFormat(endtime_l));
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_task, menu);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }


    public void insertIntoSQLite( int ID)
    {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.FIELD_ID, ID);
        values.put(DatabaseHelper.FIELD_TITLE, taskEditText.getText().toString());
        values.put(DatabaseHelper.FIELD_START_TIME, starttime_l);
        values.put(DatabaseHelper.FIELD_END_TIME, endtime_l);
        values.put(DatabaseHelper.FIELD_IMPORTANCE,importance);
        values.put(DatabaseHelper.FIELD_STATUS, AddTaskActivity.TASK_ONGOING);
        values.put(DatabaseHelper.FIELD_START_ALARM,startAlarmTime);
        values.put(DatabaseHelper.FIELD_END_ALARM,endAlarmTime);
        values.put(DatabaseHelper.FIELD_ADDRESS,"公司");
        values.put(DatabaseHelper.FIELD_PARTICIPATOR,"我");
        values.put(DatabaseHelper.FIELD_LABEL,"");
        values.put(DatabaseHelper.FIELD_PROJECT,"");
        values.put(DatabaseHelper.FIELD_SUBTASK,"");
        values.put(DatabaseHelper.FIELD_PS,"");
        values.put(DatabaseHelper.FIELD_REPEAT,0);


        Log.e("chenliujiang******************任务标题 : ", "" + taskEditText.getText().toString());
        Log.e("chenliujiang******************开始时间 : ", "" + DateTimeActivity.dateTimeFormat(starttime_l));
        Log.e("chenliujiang******************结束时间 : ", "" + DateTimeActivity.dateTimeFormat(endtime_l));
        Log.e("chenliujiang******************重要属性 : ", "" +importance);

        DatabaseHelper databaseHelper = new DatabaseHelper(AddTaskActivity.this);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        if (isEdit == false) {
            sqLiteDatabase.insert(DatabaseHelper.TABLE_NAME, null, values);
        }
        else {
            Log.e("=============================>","更新数据库");
            values.put(DatabaseHelper.FIELD_STATUS, agenda.getStatus());
            sqLiteDatabase.update(DatabaseHelper.TABLE_NAME,values,DatabaseHelper.FIELD_ID+"=?",
                    new String[]{""+agenda.getID()});
        }
        // send broadcast
        Intent notifyDatabaseIsChanged = new Intent(TodolistWidget.dataIsUpdate);
        sendBroadcast(notifyDatabaseIsChanged);
    }


    Handler handler1=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            //dialog.dismiss();
            /*String msgobj=msg.obj.toString();
            if(msgobj.equals("t"))
            {
                Toast.makeText(AddTaskActivity.this, "注册成功", 0).show();
            }
            else {
                Toast.makeText(AddTaskActivity.this, "注册失败", 0).show();
            }*/

            int ID = Integer.parseInt(msg.obj.toString());

            Log.e("handleMessage--- the return ID is -->",""+ID);

            if( ID > 0 ) {
                insertIntoSQLite(ID);
                Toast.makeText(AddTaskActivity.this, "插入任务成功", 0).show();
            }
            else {
                Toast.makeText(AddTaskActivity.this, "插入失败", 0).show();
            }

            super.handleMessage(msg);
        }
    };
}
