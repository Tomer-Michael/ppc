package com.tomer.ppc;

import android.os.Parcel;
import android.os.Parcelable;

public class TodoItem implements Parcelable {
    private String text;
    private boolean isDone;

    public TodoItem(String text, boolean isDone) {
        this.text = text;
        this.isDone = isDone;
    }

    public TodoItem(String text) {
        this(text, false);
    }

    protected TodoItem(Parcel in) {
        text = in.readString();
        isDone = in.readByte() != 0;
    }

    public String getText() {
        return text;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    public void toggleIsDone() {
        this.isDone = !this.isDone;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeByte((byte) (isDone ? 1 : 0));
    }
}
