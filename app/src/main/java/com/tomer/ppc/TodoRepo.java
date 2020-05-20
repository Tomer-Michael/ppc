package com.tomer.ppc;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TodoRepo {
    private static final String KEY_ITEMS_COUNT = "items_count";
    private static final String KEY_ALL_ITEMS = "all_items";
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public TodoRepo(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        gson = new Gson();
    }

    public int getItemsCount() {
        return sharedPreferences.getInt(KEY_ITEMS_COUNT, 0);
    }

    public List<TodoItem> getAllItems() {
        String json = sharedPreferences.getString(KEY_ALL_ITEMS, null);
        return json != null ? gson.fromJson(json, new TypeToken<ArrayList<TodoItem>>(){}.getType()) : new ArrayList<>();
    }

    public void addItem(TodoItem todoItem) {
        int itemCount = getItemsCount();
        List<TodoItem> allItems = getAllItems();
        allItems.add(todoItem);
        sharedPreferences.edit()
                .putInt(KEY_ITEMS_COUNT, itemCount + 1)
                .putString(KEY_ALL_ITEMS, gson.toJson(allItems))
                .apply();
    }

    public void deleteItem(int index) {
        int itemCount = getItemsCount();
        List<TodoItem> allItems = getAllItems();
        allItems.remove(index);
        sharedPreferences.edit()
                .putInt(KEY_ITEMS_COUNT, itemCount - 1)
                .putString(KEY_ALL_ITEMS, gson.toJson(allItems))
                .apply();
    }

    public void notifyDataSetChanged(List<TodoItem> newDataSet) {
        sharedPreferences.edit()
                .putInt(KEY_ITEMS_COUNT, newDataSet.size())
                .putString(KEY_ALL_ITEMS, gson.toJson(newDataSet))
                .apply();
    }
}
