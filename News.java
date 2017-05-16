package com.example.shrad.newsgateway;

/**
 * Created by shrad on 4/26/2017.
 */

public class News {

    String id;
    String name;
    String url;
    String category;
    News(String id,String name,String url,String category)
    {
        this.id=id;
        this.name=name;
        this.url=url;
        this.category=category;
    }

    public String getUrl() {
        return url;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setUrl(String url) {
        this.url = url;
    }


}
