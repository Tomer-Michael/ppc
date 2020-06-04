package com.tomer.ppc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentSnapshot;

public class TodoItem implements Parcelable {
    public String id;
    public String text;
    public boolean isDone;
    public String creationTimestamp;
    public String editTimestamp;

    public TodoItem() {
    }

    public TodoItem(String id, String text, boolean isDone) {
        this.text = text;
        this.isDone = isDone;
        this.id = id;
        creationTimestamp = getCurrentTime();
        editTimestamp = creationTimestamp;
    }
    public TodoItem(DocumentSnapshot document) {
        this.text = (String) document.get("text");;
        this.isDone = document.getBoolean("isDone");;
        this.id = (String) document.get("id");;
        creationTimestamp = (String) document.get("creationTimestamp");;
        editTimestamp = (String) document.get("editTimestamp");;
    }

    public TodoItem(String id, String text) {
        this(id, text, false);
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

    public void setId(String id) {
        this.id = id;
        setEditTimestamp(getCurrentTime());
    }

    public void setCreationTimestamp(String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
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

    public Map<String, Object> describe() {
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("id", id);
        taskData.put("text", text);
        taskData.put("isDone", isDone);
        taskData.put("creationTimestamp", creationTimestamp);
        taskData.put("editTimestamp", editTimestamp);
        return taskData;
    }

}
