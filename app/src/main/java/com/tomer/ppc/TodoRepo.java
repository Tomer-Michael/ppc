package com.tomer.ppc;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class TodoRepo {
    public interface Listener {
        public void notifyMe(List<TodoItem> list);
    }

    private List<TodoItem> list = new ArrayList<>();
    private Listener listener;

    public TodoRepo(Context context) {
        init();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public int getItemsCount() {
        return list.size();
    }

    public List<TodoItem> getAllItems() {
        return list;
    }

    public void notifyDataSetChanged(List<TodoItem> newDataSet) {
        list = newDataSet;
    }

    void deleteItem(int position, TodoItem todo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ppc").document(todo.getId()).delete();
        list.remove(position);
    }

    void addTodo(TodoItem todo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // New document
        DocumentReference doc = db.collection("ppc").document();
        doc.set(todo);
        list.add(todo);
    }

    void editItem(int position, TodoItem newTodo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ppc").document(newTodo.getId()).set(newTodo);
        list.set(position, newTodo);
    }

    public TodoItem getTodo(String id){
        return list.stream()
                .filter(todoItem -> todoItem.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void init() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference tdLstCollectionRef = db.collection("ppc");
        tdLstCollectionRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e);
                return;
            }
            if (queryDocumentSnapshots == null) {
                Log.d("Firestore", "Current data: null");
            } else {
                list.clear();
                queryDocumentSnapshots.forEach(doc -> list.add(doc.toObject(TodoItem.class)));
            }
        });

        tdLstCollectionRef.get().addOnCompleteListener(task -> {
            if (listener != null) {
                listener.notifyMe(list);
            }
        });
        tdLstCollectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (listener != null) {
                listener.notifyMe(list);
            }
        });
    }
}
