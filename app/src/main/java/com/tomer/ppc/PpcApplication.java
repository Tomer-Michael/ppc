package com.tomer.ppc;

import android.app.Application;
import android.util.Log;

public class PpcApplication extends Application {
    private static final String TAG = PpcApplication.class.getSimpleName();
    private TodoRepo todoRepo;

    @Override
    public void onCreate() {
        super.onCreate();
        todoRepo = new TodoRepo(this);
        int numTodos = todoRepo.getItemsCount();
        Log.d(TAG, "Num todos: " + numTodos);
    }

    public TodoRepo getTodoRepo() {
        return todoRepo;
    }
}
