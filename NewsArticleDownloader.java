package com.example.shrad.newsgateway;

import android.content.Context;
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
 * Created by shrad on 4/27/2017.
 */

public class NewsArticleDownloader extends AsyncTask<String,Void,String> {
private static final String TAG = "NewsSourceDownloader";
private NewsService newsService;
private final String newsURL = "https://newsapi.org/v1/articles?source=";

private final String NODATA = "No Data Provided";


public NewsArticleDownloader(NewsService ma ) {
        newsService = ma;
        }

@Override
protected void onPostExecute(String s) {
        System.out.println("OnPostexecute Result: " + s);
        List<Article> List=parseJSON(s);
        newsService.setArticle(List);

        }

@Override
protected String doInBackground(String... params) {
        Uri.Builder buildURL;
         String newURL=newsURL+""+params[0];
        buildURL = Uri.parse(newURL).buildUpon();
        buildURL.appendQueryParameter("apiKey", "eea1b07ce9cb489fb8762ba09fbe0b53");
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

private List<Article> parseJSON(String s) {
        List<Article> List = new ArrayList<>();
        try {

        JSONObject mJsonObject = null, news = null;
        JSONArray newsArry = null;
        String author, title, desc, urlToImage,source,url,date;
        mJsonObject = new JSONObject(s);
                source=mJsonObject.getString("source");
        newsArry = mJsonObject.getJSONArray("articles");
        for (int i = 0; i < newsArry.length(); i++) {

        news = newsArry.getJSONObject(i);
        author = news.getString("author");
        title = news.getString("title");
        desc = news.getString("description");
        urlToImage = news.getString("urlToImage");
                url=news.getString("url");
                date=news.getString("publishedAt");
        List.add(new Article(source,author,title,desc,urlToImage,url,date));
        }
        }
        catch(Exception e)
        {
        System.out.print(e);
        }
        return List;
        }}
