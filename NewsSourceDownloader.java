package com.example.shrad.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shrad on 4/26/2017.
 */

public class NewsSourceDownloader extends AsyncTask<String,Void,String> {
    private static final String TAG = "NewsSourceDownloader";
    private MainActivity mainActivity;
    private final String newsURL = "https://newsapi.org/v1/sources?language=en&country=us&apiKey=eea1b07ce9cb489fb8762ba09fbe0b53";
    private final String categoryUrl = "https://newsapi.org/v1/sources?language=en&country=us";
    private final String NODATA = "No Data Provided";
    private String newsCategory;

    public NewsSourceDownloader(MainActivity ma ) {
        mainActivity = ma;

    }

    @Override
    protected void onPostExecute(String s) {
        System.out.println("OnPostexecute Result: " + s);
        HashMap<String,News> List=parseJSON(s);
        mainActivity.setSources(List);

    }

    @Override
    protected String doInBackground(String... params) {
        Uri.Builder buildURL;
if(params[0].equals("all")|params[0].equals(""))
{  buildURL = Uri.parse(newsURL).buildUpon();}
        else {
    buildURL = Uri.parse(categoryUrl).buildUpon();
    buildURL.appendQueryParameter("category", params[0]);
    buildURL.appendQueryParameter("apiKey", "eea1b07ce9cb489fb8762ba09fbe0b53");
}
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        return sb.toString();
    }

    private HashMap<String,News> parseJSON(String s) {
        HashMap<String,News> categoryList = new HashMap<>();
        try {

            JSONObject mJsonObject = null, news = null;
            JSONArray newsArry = null;
            String id, name, category, url;
            mJsonObject = new JSONObject(s);
            newsArry = mJsonObject.getJSONArray("sources");
            for (int i = 0; i < newsArry.length(); i++) {

                    news = newsArry.getJSONObject(i);
                    id = news.getString("id");
                    System.out.println("id :" + id);
                    name = news.getString("name");
                    url = news.getString("url");
                    category = news.getString("category");
                categoryList.put(name,new News(id,name,url,category));
            }
        }
        catch(Exception e)
        {
            System.out.print(e);
        }
        return categoryList;
    }
}