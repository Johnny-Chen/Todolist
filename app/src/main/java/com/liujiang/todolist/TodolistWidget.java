package com.liujiang.todolist;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.liujiang.todolist.json.WriteJson;
import com.liujiang.todolist.operation.Operaton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of App Widget functionality.
 */
public class TodolistWidget extends AppWidgetProvider {

    // the data source for AppWidget
    private static ArrayList<Agenda> toDoList = new ArrayList<Agenda>();
    public  static ArrayList<Agenda> getToDoList() {return toDoList;}



    private ComponentName thisWidget;
    private RemoteViews remoteViews;

    String listviewAction = "todolist.appwidget.listview";

    public static String dataIsUpdate = "todolist.appwidget.dataisupdate";
    public static String dataIsAdded = "todolist.appwidget.dataisadded";
    public static String updateStartTime = "todolist.appwidget.updatestarttime";
    public static String updateEndTime = "todolist.appwidget.updateendtime";

    Context mContext;



    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.e("onReceive----->action is",""+intent.getAction());


        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context,TodolistWidget.class);
        RemoteViews remoteViews1 = new RemoteViews(context.getPackageName(),R.layout.todolist_widget);
        int appWidgetIds[] = manager.getAppWidgetIds(componentName);


        if( intent.getAction().equals(listviewAction))
        {
            updateToDoList(context);
            int position = intent.getIntExtra("pos",-1);
            Log.e("onReceive----->来自listview的广播 ： pos is",""+position);
            Log.e("onReceive----------------->the label is",intent.getStringExtra("label"));

            if(intent.getStringExtra("label").equals("start"))
            {
                Intent start_dlg = new Intent(context,DateTimeActivity.class);
                start_dlg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                start_dlg.putExtra("isWidget", true);
                start_dlg.putExtra("isStartTime", true);
                start_dlg.putExtra("date_time", toDoList.get(position).getStart_time());
                start_dlg.putExtra("alarmTime", toDoList.get(position).getStart_alarm());
                start_dlg.putExtra("pos", position);
                context.startActivity(start_dlg);
            }
            else if(intent.getStringExtra("label").equals("end"))
            {
                Intent start_dlg = new Intent(context,DateTimeActivity.class);
                start_dlg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                start_dlg.putExtra("isWidget", true);
                start_dlg.putExtra("isStartTime", false);
                start_dlg.putExtra("date_time", toDoList.get(position).getEnd_time());
                start_dlg.putExtra("alarmTime", toDoList.get(position).getEnd_alarm());
                start_dlg.putExtra("pos", position);
                context.startActivity(start_dlg);
            }
            else if(intent.getStringExtra("label").equals("finish"))
            {
                Log.e("onReceive----------------->", "任务已经完成");
                finishTask(context,position);
                //updateToDoList(context);
                // do something
            }
            else if(intent.getStringExtra("label").equals("edit"))
            {
                Log.e("onReceive----------------->", "点击出发编辑任务");
                Intent edit_task = new Intent(context,AddTaskActivity.class);
                edit_task.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                edit_task.putExtra("isEdit", true);

                Bundle bundle = new Bundle();
                bundle.putSerializable("agenda",toDoList.get(position));
                edit_task.putExtras(bundle);
                context.startActivity(edit_task);
            }

        }
        if( intent.getAction().equals(dataIsUpdate)) {
            Log.e("onReceive----------------->", "收到数据已经更新的广播");
            updateToDoList(context);
        }
        if( intent.getAction().equals(dataIsAdded)) {
            Log.e("onReceive----------------->", "收到数据添加的广播");
            updateToDoList(context);
        }
        if( intent.getAction().equals(updateStartTime)) {
            Log.e("onReceive----------------->", "更新开始时间");
            int position = intent.getIntExtra("pos",-1);
            Log.e("onReceive----->The position is",""+position);
            /*updateStartTime(context,position,intent.getExtras().getLong("date_time"));*/
            updateDateTime(context,true,position,intent.getExtras().getLong("date_time"),
                    intent.getExtras().getInt("alarmTime"));
            //updateToDoList(context);
        }
        if( intent.getAction().equals(updateEndTime)) {
            Log.e("onReceive----------------->", "更新结束时间");
            int position = intent.getIntExtra("pos",-1);
            Log.e("onReceive----->The position is",""+position);
            /*updateEndTime(context, position, intent.getExtras().getLong("date_time"));*/
            updateDateTime(context,false,position,intent.getExtras().getLong("date_time"),
                    intent.getExtras().getInt("alarmTime"));
            //updateToDoList(context);
        }

        manager.updateAppWidget(componentName,remoteViews1);
        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListView);
    }

    //@Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        thisWidget = new ComponentName(context, TodolistWidget.class);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.todolist_widget);
        // There may be multiple widgets active, so update all of them

        mContext = context;
        //sList = getArrayList(context);
        //updateArrayList(context);
        updateToDoList(context);
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {

            Log.e("AppWidgetProvider", "onUpdate===========================>");



            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.todolist_widget);


            /* for addItem button on click */
            Intent additemIntent = new Intent(context,AddTaskActivity.class);
            additemIntent.putExtra("isEdit",false);
            PendingIntent addItemPendingIntent = PendingIntent.getActivity(context, 0, additemIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.addItem,addItemPendingIntent);

            //for listview ************************************************
            Intent startWidgetService = new Intent(context, WidgetService.class);
            //Intent startWidgetService = new Intent(context, UpdateService.class);
            startWidgetService.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            remoteViews.setRemoteAdapter(R.id.widgetListView,startWidgetService);

            //建立模板
            Intent intentMode = new Intent();
            intentMode.setAction(listviewAction);
            PendingIntent pendingIntentTemplate = PendingIntent.getBroadcast(context, 0, intentMode, 0);
            //PendingIntent pendingIntentTemplate = PendingIntent.getActivity(context, 1, intentMode,
                    //PendingIntent.FLAG_UPDATE_CURRENT);
            //拼接PendingIntent
            remoteViews.setPendingIntentTemplate(R.id.widgetListView, pendingIntentTemplate);

            //updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
            appWidgetManager.updateAppWidget(thisWidget,remoteViews);

            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            manager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.widgetListView);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.todolist_widget);
        ////////////////////////////views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public void updateToDoList(Context context){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        //Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_NAME,new String[]{DatabaseHelper.FIELD_START_TIME,DatabaseHelper.FIELD_TITLE},null,null,null,null,null);
//        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_NAME,
//                new String[]{DatabaseHelper.FIELD_START_TIME,DatabaseHelper.FIELD_TITLE,DatabaseHelper.FIELD_END_TIME},null,null,null,null,null);

        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_NAME,
                new String[]{DatabaseHelper.FIELD_ID,DatabaseHelper.FIELD_TITLE,
                        DatabaseHelper.FIELD_START_TIME,DatabaseHelper.FIELD_END_TIME,
                        DatabaseHelper.FIELD_START_ALARM,DatabaseHelper.FIELD_END_ALARM,
                        DatabaseHelper.FIELD_ADDRESS,DatabaseHelper.FIELD_PARTICIPATOR,
                        DatabaseHelper.FIELD_LABEL,DatabaseHelper.FIELD_PROJECT,
                        DatabaseHelper.FIELD_SUBTASK,DatabaseHelper.FIELD_PS,
                        DatabaseHelper.FIELD_REPEAT,
                        DatabaseHelper.FIELD_STATUS,DatabaseHelper.FIELD_IMPORTANCE},
                DatabaseHelper.FIELD_STATUS+"=?",new String[]{""+AddTaskActivity.TASK_ONGOING},
                null,null,null);


        toDoList.clear();

        while (cursor.moveToNext()){

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

            /*Log.e("查询测试******************任务ID",""+ID);
            Log.e("查询测试******************任务标题",""+title);
            Log.e("查询测试******************开始时间",""+start_time );
            Log.e("查询测试******************结束时间",""+end_time);*/
            toDoList.add(agenda);
        }
    }

    public void finishTask(Context context,final int pos) {

        new Thread(new Runnable() {

            public void run() {

                Operaton operaton=new Operaton();

                Agenda mAgenda = toDoList.get(pos);
                mAgenda.setStatus(AddTaskActivity.TASK_FINISH);

                List<Agenda> list = new ArrayList<Agenda>();
                list.add(mAgenda);

                WriteJson writeJson=new WriteJson();
                //将user对象写出json形式字符串
                String jsonString= writeJson.getJsonData(list);
                System.out.println(jsonString);
                String result= operaton.uploadAgenda("UpdateAgenda", jsonString);
                if(result != null && Integer.parseInt(result) > 0) {
                    Log.e("finishTask-----result---->",result);
                    Sync.notifyOK();
                } else {
                    Sync.notifyError();
                }
            }
        }).start();

        switch ( Sync.waitForResult(5000) ) {
            case Sync.TIME_OUT:
                Toast.makeText(context, "服务器无响应,等待超时", Toast.LENGTH_SHORT).show();
                break;
            case Sync.RESULT_ERROR:
                Toast.makeText(context, "服务器返回错误", Toast.LENGTH_SHORT).show();
                break;
            case Sync.RESULT_OK:
                int ID = toDoList.get(pos).getID();
                Toast.makeText(context, "任务ID: "+ID+" 已完成并更新状态", Toast.LENGTH_SHORT).show();
                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(databaseHelper.FIELD_STATUS,AddTaskActivity.TASK_FINISH);
                sqLiteDatabase.update(DatabaseHelper.TABLE_NAME,values,DatabaseHelper.FIELD_ID+"=?",
                        new String[]{""+ID});

                updateToDoList(context);
                break;
        }


    }


    public void updateDateTime(Context context,final boolean isStartTime,final int pos, final long millis, final int alarmTime) {

        new Thread(new Runnable() {

            public void run() {

                Operaton operaton=new Operaton();

                Agenda mAgenda = toDoList.get(pos);
                if (isStartTime == true) {
                    mAgenda.setStart_time(millis);
                    mAgenda.setStart_alarm(alarmTime);
                }
                else {
                    mAgenda.setEnd_time(millis);
                    mAgenda.setEnd_alarm(alarmTime);
                }

                List<Agenda> list = new ArrayList<Agenda>();
                list.add(mAgenda);

                WriteJson writeJson=new WriteJson();
                //将user对象写出json形式字符串
                String jsonString= writeJson.getJsonData(list);
                System.out.println(jsonString);
                String result = operaton.uploadAgenda("UpdateAgenda", jsonString);
                if(result != null && Integer.parseInt(result) > 0) {
                    Log.e("updateDateTime-----result---->",result);
                    Sync.notifyOK();
                } else {
                    Sync.notifyError();
                }
            }
        }).start();

        switch ( Sync.waitForResult(5000) ) {
            case Sync.TIME_OUT:
                Toast.makeText(context, "等待超时,不能更新时间", Toast.LENGTH_SHORT).show();
                break;
            case Sync.RESULT_ERROR:
                Toast.makeText(context, "服务器返回错误,不能更新时间", Toast.LENGTH_SHORT).show();
                break;
            case Sync.RESULT_OK:
                int ID = toDoList.get(pos).getID();
                Toast.makeText(context, "任务ID: "+ID+" 时间更新成功", Toast.LENGTH_SHORT).show();
                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                if (isStartTime == true) {
                    values.put(DatabaseHelper.FIELD_START_TIME, millis);
                    values.put(DatabaseHelper.FIELD_START_ALARM, alarmTime);
                    Log.e("需要更改开始时间的任务的ID是：--------------》", "" + ID);
                    Log.e("alarmTime是：--------------》", "" + alarmTime);
                } else {
                    values.put(databaseHelper.FIELD_END_TIME, millis);
                    values.put(DatabaseHelper.FIELD_END_ALARM, alarmTime);
                    Log.e("需要更改结束时间的任务的ID是：--------------》", "" + ID);
                    Log.e("alarmTime是：--------------》", "" + alarmTime);
                }
                sqLiteDatabase.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper.FIELD_ID + "=?",
                        new String[]{"" + ID});
                updateToDoList(context);
                break;
        }
    }

}




