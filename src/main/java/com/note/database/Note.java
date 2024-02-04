package com.note.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Note implements Serializable{//Serializable为空接口，表示该类可被序列化
    private long id;
    private String title;
    private String content;
    private String time;
    private String tag;
    private static final long serialVersionUID = -7060210544600464482L;
    public Note(){

    }
    public Note(String title, String content, String time, String tag) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.tag = tag;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public String getTag() {
        return tag;
    }


    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        Note note = (Note) o;
        return this.getTitle().equals(note.getTitle()) && ( this.getContent().equals(note.getContent())) && ( this.getTime().equals(note.getTime())) && ( this.getTag().equals(note.getTag()));
    }

    @Override
    public int hashCode() {
        String result = title+content+time+tag;
        return result.hashCode();
    }
}
