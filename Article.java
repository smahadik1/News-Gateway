package com.example.shrad.newsgateway;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shrad on 4/27/2017.
 */

public class Article implements Parcelable {
    String author;
    String title;
    String description;
    String urlToImage;
    String source;
    String url;
    String date;

  public  Article (String source,String author,String title,String description,String urlToImage,String url,String date)
  {
      this.source=source;
   this.author=author;
      this.title=title;
      this.description=description;
      this.urlToImage=urlToImage;
      this.url=url;
      this.date=date;
  }

    protected Article(Parcel in) {
        author = in.readString();
        title = in.readString();
        description = in.readString();
        urlToImage = in.readString();
        source = in.readString();
        url=in.readString();
        date=in.readString();

    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public String getSource() {
        return source;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(urlToImage);
        dest.writeString(source);
        dest.writeString(url);
        dest.writeString(date);
    }
}
