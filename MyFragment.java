package com.example.shrad.newsgateway;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;

/**
 * Created by shrad on 4/26/2017.
 */

public class MyFragment extends Fragment implements View.OnClickListener {
private TextView title,desc;
    private String url;
    private ImageView article_img;
    public static final MyFragment newInstance(String src,String author,String title,String desc,String urlToimg,String url,String date,String pgno)
    {
        if(author.equals(null)||author.isEmpty()||author.equals("null"))
        {
            author="";
        }
        if(title.equals(null)||title.isEmpty()||title.equals("null"))
        {
           title="";
        }


        if(desc.equals(null)||desc.isEmpty()||desc.equals("null"))
        {
            desc="";
        }
        MyFragment f = new MyFragment();
        Bundle bdl = new Bundle(1);
       bdl.putString("Source",src);
        bdl.putString("Author",author);
        bdl.putString("title",title);
        bdl.putString("desc",desc);
        bdl.putString("urltoimg",urlToimg);
        bdl.putString("url",url);
        bdl.putString("pgno",pgno);
        bdl.putString("date",date);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String Source = getArguments().getString("Source");
        String Author = getArguments().getString("Author");
        String Title = getArguments().getString("title");
        String description = getArguments().getString("desc");
        final String urltoimg = getArguments().getString("urltoimg");
        String pgno=getArguments().getString("pgno");
        String Publisheddate=getArguments().getString("date");
        SimpleDateFormat sdf = null;
        String pubdate = "";
        url=getArguments().getString("url");
        if(Publisheddate!=null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                //sdf=new SimpleDateFormat("EEE MMM d, HH:mm aaa");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    pubdate = new SimpleDateFormat("MMM d, YYYY HH:mm ").format(sdf.parse(Publisheddate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }


        View v = inflater.inflate(R.layout.myfragment, container, false);
         title = (TextView)v.findViewById(R.id.article_title);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickUrl(v);
            }
        });
          desc=(TextView)v.findViewById(R.id.article_desc1);
        desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickUrl(v);
            }
        });
        TextView author=(TextView)v.findViewById(R.id.author);
       article_img=(ImageView)v.findViewById(R.id.article_img);
        article_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickUrl(v);
            }
        });
        TextView date=(TextView)v.findViewById(R.id.date);
        TextView pageno=(TextView)v.findViewById(R.id.pgno);

        title.setText(Title);
        desc.setText(description);
        if(Author.equals(""))
        {
            author.setText(Source);
        }
       else{ author.setText(Author+", "+Source);}
        date.setText(pubdate);
        pageno.setText(pgno);

        try {
            if (!urltoimg.equals(null)) {


                Picasso picasso = new Picasso.Builder(v.getContext()).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        // Here we try https if the image attempt failed
                        String changedUrl = urltoimg.replace("http:", "https:");
                        picasso.load(changedUrl)
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.nothumb)
                                .into(article_img);
                    }
                }).build();
                picasso.load(urltoimg)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.nothumb)
                        .into(article_img);
            } else {
                Picasso.with(v.getContext()).load(urltoimg)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.nothumb)
                        .into(article_img);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }



        return v;
    }

    public void clickUrl(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onClick(View v) {

    }
}