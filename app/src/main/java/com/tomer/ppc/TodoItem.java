package com.tomer.ppc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import android.os.Parcel;
import android.os.Parcelable;

public class TodoItem implements Parcelable {
    private final String id;
    private String text;
    private boolean isDone;
    private final String creationTimestamp;
    private String editTimestamp;

    public TodoItem(String text, boolean isDone) {
        this.text = text;
        this.isDone = isDone;
        creationTimestamp = getCurrentTime();
        editTimestamp = creationTimestamp;
        id = creationTimestamp;
    }

    public TodoItem(String text) {
        this(text, false);
    }

    protected TodoItem(Parcel in) {
        id = in.readString();
        text = in.readString();
        isDone = in.readByte() != 0;
        creationTimestamp = in.readString();
        editTimestamp = in.readString();
    }

    public static final Creator<TodoItem> CREATOR = new Creator<TodoItem>() {
        @Override
        public TodoItem createFromParcel(Parcel in) {
            return new TodoItem(in);
        }

        @Override
        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };

    public String getText() {
        return text;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setText(String text) {
        this.text = text;
        setEditTimestamp(getCurrentTime());
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
        setEditTimestamp(getCurrentTime());
    }

    public void setEditTimestamp(String editTimestamp) {
        this.editTimestamp = editTimestamp;
    }

    public String getId() {
        return id;
    }

    public String getCreationTimestamp() {
        return creationTimestamp;
    }

    public String getEditTimestamp() {
        return editTimestamp;
    }

    private String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public void toggleIsDone() {
        setIsDone(!isDone());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(text);
        dest.writeByte((byte) (isDone ? 1 : 0));
        dest.writeString(creationTimestamp);
        dest.writeString(editTimestamp);
    }
}
