package com.tomer.ppc;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class TodoRepo {
    public interface Listener {
        public void notifyMe(List<TodoItem> list);
    }

    private List<TodoItem> list = new ArrayList<>();
    public FirebaseFirestore firestore = FirebaseFirestore.getInstance();
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

    void deleteItem(int position, TodoItem todo) {
        Log.d("TAMAR DELETING", "pos and todo: " + position + " " + todo.toString());
        Log.d("TAMAR DELETING", list.toString());
        firestore.collection("ppc").document(todo.getId()).delete();
    }

    void addTodo(TodoItem todo) {
        Log.d("TAMAR", "addTodo with " + todo.getId());
        // New document
        DocumentReference doc = firestore.collection("ppc").document();
        doc.set(todo);
        list.add(todo);
    }

    void editItem(int position, TodoItem newTodo) {
        Log.d("TAMAR", "editItem");
        firestore.collection("ppc").document(newTodo.getId()).set(newTodo);
        list.set(position, newTodo);
    }

    private void init() {
        CollectionReference tdLstCollectionRef = firestore.collection("ppc");
        tdLstCollectionRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e);
                return;
            }
            if (queryDocumentSnapshots == null) {
                Log.d("Firestore", "Current data: null");
            } else {
                list.clear();
                Log.d("TAMAR", "got2 " + queryDocumentSnapshots.toString());
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    TodoItem todo = doc.toObject(TodoItem.class);
                    Log.d("TAMAR", "got2 " + todo.getText());
                    list.add(todo);
                }
                Log.d("TAMAR", "got list is " + list.toString());
            }
        });

        tdLstCollectionRef.get().addOnCompleteListener(task -> {
            if (listener != null) {
                Log.d("TAMAR", "About to notify, list is " + list.toString());
                listener.notifyMe(list);
            }
        });
        tdLstCollectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (listener != null) {
                Log.d("TAMAR", "About to notify, list is " + list.toString());
                listener.notifyMe(list);
            }
        });
    }
}
