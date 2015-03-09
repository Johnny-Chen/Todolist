package com.liujiang.todolist;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.liujiang.todolist.json.WriteJson;
import com.liujiang.todolist.operation.Operaton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private static ArrayList<Agenda> allTaskArray = new ArrayList<Agenda>();

    int ID , pos = 0;
    SimpleCursorAdapter listAdapter;

    ListView alltaskView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        alltaskView = (ListView) findViewById(R.id.alltask);
        Cursor cursor = getCursor(db);
        listAdapter = new SimpleCursorAdapter(this,R.layout.activity_list_item,cursor,
                new String[]{DatabaseHelper.FIELD_TITLE,DatabaseHelper.FIELD_ID},
                new int[] {R.id.task_title, R.id.task_status},0);
        alltaskView.setAdapter(listAdapter);

        alltaskView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                Log.e("长按Listview", "setOnCreateContextMenuListener");

                menu.setHeaderTitle("ContextMenu");
                menu.add(0, 0, 0, "删除这个任务");
                menu.add(0, 1, 0, "标记为pendding");
                menu.add(0, 2, 0, "标记为onGoing");
                menu.add(0, 3, 0, "标记为Finish");
            }
        });

        alltaskView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("长按Listview", "setOnItemLongClickListener");


                ID = Integer.parseInt(((Button)view.findViewById(R.id.task_status) ).getText().toString());
                pos = position;
                Log.e("长按Listview获得的ID是",""+ID);
                alltaskView.showContextMenu();
                return true;
            }
        });

        alltaskView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("短按Listview", "setOnItemClickListener");
                Intent edit_task = new Intent(MainActivity.this,AddTaskActivity.class);
                edit_task.putExtra("isEdit", true);
                Bundle bundle = new Bundle();
                bundle.putSerializable("agenda",allTaskArray.get(position));
                edit_task.putExtras(bundle);
                MainActivity.this.startActivity(edit_task);
            }
        });

        alltaskView.post(new Runnable() {
            @Override
            public void run() {
                updateStatusColor();
            }
        });


        IntentFilter intentFilter = new IntentFilter(TodolistWidget.dataIsUpdate);
        registerReceiver(broadcastReceiver,intentFilter);
        //setContentView(R.layout.activity_main);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                Log.e("上下文菜单", "删除ID号码为"+ID+"的任务");
                deleteTask();
                break;
            case 1:
                Log.e("上下文菜单", "修改ID号码为"+ID+"的状态为pending");
                updateTaskStatus(AddTaskActivity.TASK_PENDDING);
                break;
            case 2:
                Log.e("上下文菜单", "修改ID号码为"+ID+"的状态为onGoing");
                updateTaskStatus(AddTaskActivity.TASK_ONGOING);
                break;
            case 3:
                Log.e("上下文菜单", "修改ID号码为"+ID+"的状态为finish");
                updateTaskStatus(AddTaskActivity.TASK_FINISH);
                break;
        }
        // send broadcast

        //listAdapter.notifyDataSetChanged();
        Intent notifyDatabaseIsChanged = new Intent(TodolistWidget.dataIsAdded);
        sendBroadcast(notifyDatabaseIsChanged);

        return super.onContextItemSelected(item);
    }

    public void deleteTask() {

        new Thread(new Runnable() {

            public void run() {

                Operaton operaton=new Operaton();

                Agenda mAgenda = allTaskArray.get(pos);

                List<Agenda> list = new ArrayList<Agenda>();
                list.add(mAgenda);

                WriteJson writeJson=new WriteJson();
                //将user对象写出json形式字符串
                String jsonString= writeJson.getJsonData(list);
                System.out.println(jsonString);
                String result= operaton.uploadAgenda("RemoveAgenda", jsonString);
                Log.e("deleteTask-----result---->",result);
            }
        }).start();


        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        String where = DatabaseHelper.FIELD_ID + "=?";
        String[] whereValue = { Integer.toString(ID) };
        sqLiteDatabase.delete(DatabaseHelper.TABLE_NAME,where,whereValue);

        listAdapter.changeCursor(getCursor(sqLiteDatabase));
        sqLiteDatabase.close();


    }

    public void updateTaskStatus( int status ) {

        new Thread(new Runnable() {

            public void run() {

                Operaton operaton=new Operaton();

                Agenda mAgenda = allTaskArray.get(pos);

                List<Agenda> list = new ArrayList<Agenda>();
                list.add(mAgenda);

                WriteJson writeJson=new WriteJson();
                //将user对象写出json形式字符串
                String jsonString= writeJson.getJsonData(list);
                System.out.println(jsonString);
                String result= operaton.uploadAgenda("UpdateAgenda", jsonString);
                Log.e("updateTaskStatus-----result---->",result);
            }
        }).start();

        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(databaseHelper.FIELD_STATUS,status);

        sqLiteDatabase.update(DatabaseHelper.TABLE_NAME,values,DatabaseHelper.FIELD_ID+"=?",
                new String[]{""+ID});

        getCursor(sqLiteDatabase);  //UPDATE the TaskArrayList.
        sqLiteDatabase.close();

        updateStatusColor();
    }

    public Cursor getCursor(SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_NAME,
                new String[]{DatabaseHelper.FIELD_ID,DatabaseHelper.FIELD_TITLE,
                        DatabaseHelper.FIELD_START_TIME,DatabaseHelper.FIELD_END_TIME,
                        DatabaseHelper.FIELD_START_ALARM,DatabaseHelper.FIELD_END_ALARM,
                        DatabaseHelper.FIELD_ADDRESS,DatabaseHelper.FIELD_PARTICIPATOR,
                        DatabaseHelper.FIELD_LABEL,DatabaseHelper.FIELD_PROJECT,
                        DatabaseHelper.FIELD_SUBTASK,DatabaseHelper.FIELD_PS,
                        DatabaseHelper.FIELD_REPEAT,
                        DatabaseHelper.FIELD_STATUS,DatabaseHelper.FIELD_IMPORTANCE},
                null,null,null,null,null);


        allTaskArray.clear();

        while (cursor.moveToNext()) {
            Agenda agenda = new Agenda();
            agenda.setID(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FIELD_ID)));
            agenda.setTopic(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_TITLE)));
            agenda.setStart_time(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.FIELD_START_TIME)));
            agenda.setEnd_time(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.FIELD_END_TIME)));
            agenda.setImportance(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FIELD_IMPORTANCE)));
            agenda.setStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FIELD_STATUS)));
            agenda.setStart_alarm(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FIELD_START_ALARM)));
            agenda.setEnd_alarm(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FIELD_END_ALARM)));
            agenda.setAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_ADDRESS)));
            agenda.setParticipator(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_PARTICIPATOR)));
            agenda.setRepeat(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FIELD_REPEAT)));
            agenda.setLabel(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_LABEL)));
            agenda.setProject(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_PROJECT)));
            agenda.setSubtask(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_SUBTASK)));
            agenda.setPS(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_PS)));
            Log.e("getCursor******************任务标题", "" + agenda.getTopic());
            allTaskArray.add(agenda);
        }

        return cursor;
    }

    public void updateStatusColor() {
        Log.e("更新Listview所有任务的状态==========>",""+alltaskView.getChildCount());
        for(int i = 0; i < alltaskView.getChildCount(); i++){
            Log.e("更新Listview所有任务的状态", "loop");
            View view = alltaskView.getChildAt(i);
            Button status = (Button)view.findViewById(R.id.task_status);
            TextView taskName = (TextView)view.findViewById(R.id.task_title);
            //status.setBackgroundColor(android.R.color.darker_gray);
            if(allTaskArray.get(i).getStatus() == AddTaskActivity.TASK_ONGOING) {
                status.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            }
            else if (allTaskArray.get(i).getStatus() == AddTaskActivity.TASK_PENDDING) {
                status.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            }
            else if (allTaskArray.get(i).getStatus() == AddTaskActivity.TASK_FINISH) {
                status.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            }

            if(allTaskArray.get(i).getImportance() == AddTaskActivity.NotImportantAndNotUrgent) {
                taskName.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            }else if(allTaskArray.get(i).getImportance() == AddTaskActivity.NotImportantAndUrgent) {
                taskName.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
            }else if(allTaskArray.get(i).getImportance() == AddTaskActivity.ImportantAndNotUrgent) {
                taskName.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
            }else {
                taskName.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            }
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(getApplicationContext(),
                    "收到广播了,更新main中的listview", 0).show();
            //startTimeTextView.setText("开始时间 : \n"+intent.getExtras().getString("endTimeAlarmMode"));
            /*if(intent.getAction().equals(TodolistWidget.dataIsUpdate)) {
                updateStatusColor();
            }*/
            DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            listAdapter.changeCursor(getCursor(sqLiteDatabase));
            updateStatusColor();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
