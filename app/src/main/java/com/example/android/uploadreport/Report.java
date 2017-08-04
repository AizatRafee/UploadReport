package com.example.android.uploadreport;

/**
 * Created by user on 7/24/2017.
 */

public class Report {
    private String text;
    private String name;
    private String photoUrl;
    private String category;
    private String title;
    private String location;
    private String date;

    public Report(){
    }

    public Report (String name, String photoUrl,String categ, String title,String des, String loc, String date){
        this.text = des;
        this.name = name;
        this.photoUrl = photoUrl;
        this.category = categ;
        this.title = title;
        this.location = loc;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) { this.title = title; }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) { this.location = location; }

    public String getDate() {
        return date;
    }

    public void setDate(String date) { this.date = date; }
}
