package com.liujiang.todolist;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

public class WidgetService extends RemoteViewsService {
    public WidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("WidgetService", "onBind=============================>");
        return super.onBind(intent);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.e("WidgetService", "onGetViewFactory===========================>");
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{

        private final Context mContext;
        //private final List<String> mList;
        private final ArrayList<Agenda> mList;

        public ListRemoteViewsFactory(Context context, Intent intent){
            mContext = context;
            //mList = TodolistWidget.getList();
            mList = TodolistWidget.getToDoList();

            if(Looper.myLooper() == null){
                Looper.prepare();
            }
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return mList.size();
        }


        @Override
        public RemoteViews getViewAt(int position) {
            if(position<0 || position>=mList.size())
                return null;

            Log.e("getViewAt", "position is ===========================>" + position);

            String topic = mList.get(position).getTopic();
            /*Time start_time = new Time();
            start_time.set(mList.get(position).start_time);
            Time end_time = new Time();
            end_time.set(mList.get(position).end_time);*/

            final RemoteViews rv = new RemoteViews(mContext.getPackageName(),
                    R.layout.widget_list_item);

            Intent start_time_button = new Intent();
            start_time_button.putExtra("pos",position);
            start_time_button.putExtra("label","start");

            Intent end_time_button = new Intent();
            end_time_button.putExtra("pos",position);
            end_time_button.putExtra("label","end");

            Intent finish_task_intent = new Intent();
            finish_task_intent.putExtra("pos",position);
            finish_task_intent.putExtra("label","finish");

            Intent edit_task = new Intent();
            edit_task.putExtra("pos",position);
            edit_task.putExtra("label","edit");

            rv.setOnClickFillInIntent(R.id.starttime,start_time_button);
            rv.setOnClickFillInIntent(R.id.endtime,end_time_button);
            rv.setOnClickFillInIntent(R.id.finishtask,finish_task_intent);
            rv.setOnClickFillInIntent(R.id.taskname,edit_task);



            rv.setTextViewText(R.id.taskname, topic);

            /*rv.setTextViewText(R.id.starttime,start_time.format(DateTimeActivity.dateFormat)+"\n"+
                    start_time.format(DateTimeActivity.timeFormat));
            rv.setTextViewText(R.id.endtime,end_time.format(DateTimeActivity.dateFormat)+"\n"+
                    end_time.format(DateTimeActivity.timeFormat));*/
            rv.setTextViewText(R.id.starttime,DateTimeActivity.dateTimeFormat(mList.get(position).getStart_time()));
            rv.setTextViewText(R.id.endtime,DateTimeActivity.dateTimeFormat(mList.get(position).getEnd_time()));
            //rv.setTextViewText(R.id.endtime,".\n.");

            // set Text Color
            if(mList.get(position).getImportance() == AddTaskActivity.NotImportantAndNotUrgent) {
                rv.setTextColor(R.id.taskname,getResources().getColor(android.R.color.holo_green_light) );
            }else if(mList.get(position).getImportance() == AddTaskActivity.NotImportantAndUrgent) {
                rv.setTextColor(R.id.taskname,getResources().getColor(android.R.color.holo_blue_light) );
            }else if(mList.get(position).getImportance() == AddTaskActivity.ImportantAndNotUrgent) {
                rv.setTextColor(R.id.taskname,getResources().getColor(android.R.color.holo_orange_light) );
            }else {
                rv.setTextColor(R.id.taskname,getResources().getColor(android.R.color.holo_red_light) );
            }

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
