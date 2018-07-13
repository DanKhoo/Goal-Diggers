package com.example.danielk.goalchamp;

import android.os.Parcel;
import android.os.Parcelable;

public class Goal implements Parcelable{

    private String title;
    private String id;
    private String message;
    private String date;
    private String time;

    public Goal(){}

    public Goal(String id, String title, String message, String date, String time) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(message);
        dest.writeString(date);
        dest.writeString(time);
    }
    public Goal(Parcel source) {
        this.id = source.readString();
        this.title = source.readString();
        this.message = source.readString();
        this.date = source.readString();
        this.time = source.readString();
    }
    public static final Parcelable.Creator<Goal> CREATOR
            = new Parcelable.Creator<Goal>() {
        public Goal createFromParcel(Parcel in) {
            return new Goal(in);
        }
        public Goal[] newArray(int size) {
            return new Goal[size];
        }
    };
}
