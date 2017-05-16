package com.example.shrad.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private ViewPager pager;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<String> items = new ArrayList<>();
    HashMap<String, News> sourceList = new HashMap<>();
    private ArrayAdapter dAdapter;
    private NewsReceiver yourReceiver;
    private final String TAG = "Mainactivity";
    private final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY", ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Start Service
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);
// Registers the receiver so that your service will listen for
        // broadcasts
        yourReceiver = new NewsReceiver();
        IntentFilter filter1 = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(yourReceiver, filter1);
//Set drawer and adapter
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        dAdapter = new ArrayAdapter<>(this, R.layout.drawerlist, items);
        mDrawerList.setAdapter(dAdapter);
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                          pager.setBackground(null);
                       // Toast.makeText(MainActivity.this, "item Clicked", Toast.LENGTH_SHORT).show();
                        selectItem(position);
                    }
                }
        );
//Setup for support Action Bar
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Pageviewer and adapter

        fragments = getFragments();

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        //Async News Source Downloader Task
        new NewsSourceDownloader(this).execute("");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Do not forget to unregister the receiver!!!
        this.unregisterReceiver(this.yourReceiver);
    }

    void setSources(HashMap<String, News> List) {
        sourceList.clear();
        items.clear();
        if (List.isEmpty()) {
            items.add(0, "all");
        } else {
            sourceList = List;
            for (String key : sourceList.keySet()) {
                items.add(key);

            }
        }
        dAdapter.notifyDataSetChanged();
    }


    public boolean checkConnection(Context Context) {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Check Network Connection");
            builder.setTitle("No Network Connection");

            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }

    }

    private void selectItem(int position) {
        Toast.makeText(this, items.get(position), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.setAction(ACTION_MSG_TO_SERVICE);
        String key = items.get(position);
        News news = sourceList.get(key);
        setTitle(news.getName());
        Bundle b = new Bundle();
        b.putString("name", news.getName());
        b.putString("category", news.getCategory());
        b.putString("Id", news.getId());
        b.putString("url", news.getUrl());
        intent.putExtra("Bundle", b);
        sendBroadcast(intent);
        //reDoFragments(position);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void reDoFragments(List<Article> idx) {
        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);
        fragments.clear();

        int count = (int) (Math.random() * 8 + 2);

        for (int i = 0; i < count; i++) {

            fragments.add(MyFragment.newInstance(idx.get(i).getSource(), idx.get(i).getAuthor(), idx.get(i).getTitle(), idx.get(i).getDescription(), idx.get(i).getUrlToImage(), idx.get(i).getUrl(), idx.get(i).getDate(), (i + 1) + " of " + count));

            pageAdapter.notifyChangeInPosition(i);
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        switch (item.getItemId()) {
            case R.id.all:
                if (checkConnection(getApplicationContext())) {
                    Toast.makeText(this, "You pressed all", Toast.LENGTH_SHORT).show();
                    new NewsSourceDownloader(this).execute("all");
                }
                return true;
            case R.id.business:
                if (checkConnection(getApplicationContext())) {
                    new NewsSourceDownloader(this).execute("business");
                }
                return true;
            case R.id.scinnature:
                if (checkConnection(getApplicationContext())) {
                    new NewsSourceDownloader(this).execute("science-and-nature");
                }
                return true;
            case R.id.gaming:
                if (checkConnection(getApplicationContext())) {
                    new NewsSourceDownloader(this).execute("gaming");
                }
                return true;
            case R.id.music:
                if (checkConnection(getApplicationContext())) {
                    new NewsSourceDownloader(this).execute("music");
                }
                return true;
            case R.id.general:
                if (checkConnection(getApplicationContext())) {
                    new NewsSourceDownloader(this).execute("general");
                }
                return true;
            case R.id.politics:
                if (checkConnection(getApplicationContext())) {
                    new NewsSourceDownloader(this).execute("politics");
                }
                return true;
            case R.id.technology:
                if (checkConnection(getApplicationContext())) {
                    new NewsSourceDownloader(this).execute("technology");
                }
                return true;
            case R.id.sport:
                if (checkConnection(getApplicationContext())) {
                    new NewsSourceDownloader(this).execute("sport");
                }
                return true;
            case R.id.entertainment:
                if (checkConnection(getApplicationContext())) {
                    new NewsSourceDownloader(this).execute("entertainment");
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }



    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        return fList;
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         *
         * @param n number of items which have been changed
         */
        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;

        }

    }

    class NewsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (action.equals("ACTION_NEWS_STORY")) {
                    List<Article> list = intent.getParcelableArrayListExtra("ArticleList");
                    reDoFragments(list);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
