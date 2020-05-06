package com.tomer.ppc;

public class TodoItem {
    private String text;
    private boolean isDone;

    public TodoItem(String text, boolean isDone) {
        this.text = text;
        this.isDone = isDone;
    }

    public TodoItem(String text) {
        this(text, false);
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
}
