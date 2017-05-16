package com.example.shrad.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

/**
 * Created by shrad on 4/26/2017.
 */

public class NewsService extends Service {
    private static final String TAG = "NewsService";
    private boolean isRunning = true;
    private ServiceReceiver yourReceiver;
    public static boolean doRight = true;
    private List<Article> articleList=new ArrayList<>();

    private final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            yourReceiver = new ServiceReceiver();
        IntentFilter filter1 = new IntentFilter(ACTION_MSG_TO_SERVICE);
        registerReceiver(yourReceiver, filter1);

        new Thread(new Runnable() {
            @Override
            public void run() {

                //In this example we are just displaying a log message every 1000ms
                while (isRunning) {
                    try {
                        while (articleList.isEmpty())
                        { Thread.sleep(250);}
                        if(!articleList.isEmpty())
                        {
                            Intent intent=new Intent();
                        intent.setAction("ACTION_NEWS_STORY");
                            intent.putParcelableArrayListExtra("ArticleList",(ArrayList<Article>) articleList);
                        sendBroadcast(intent);
                        articleList.clear();
                            Thread.interrupted();

                       //isRunning=false;
                            }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (isRunning) {
                        Log.i(TAG, "Hello - I'm running properly in my own thread!");

                    }
                }
                Log.i(TAG, "NewsService was properly stopped");
            }
        }).start();

        return Service.START_STICKY;
    }

public void setArticle(List<Article> List)
        {
                articleList.clear();
                articleList.addAll(List);

        }

    @Override
    public void onDestroy() {
        isRunning = false;
        this.unregisterReceiver(yourReceiver);
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
    class ServiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals("ACTION_MSG_TO_SERVICE")) {

                    Bundle b = intent.getBundleExtra("Bundle");
                    String id = b.getString("Id");
                    new NewsArticleDownloader(NewsService.this).execute(id);

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


}
