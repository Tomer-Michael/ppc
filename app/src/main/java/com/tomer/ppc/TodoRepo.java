package com.tomer.ppc;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class TodoRepo {
    public interface Listener {
        public void notifyMe(List<TodoItem> list);
    }

    private List<TodoItem> list = new ArrayList<>();
    public FirebaseDatabase dat = FirebaseDatabase.getInstance();
    DatabaseReference firestore = database.getReference("message");
    private Listener listener;
    private CollectionReference tdLstCollectionRef;

    public TodoRepo(Context context) {
        tdLstCollectionRef = init();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public int getItemsCount() {
        return list.size();
    }

    public List<TodoItem> deleteItem(TodoItem item) {
        list.remove(find(item));
        firestore.collection("ppc").document(item.getId()).delete();
        return list;
    }

    public int find(TodoItem item) {
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).getId().equals(item.getId())) {
                return i;
            }
        }
        return -1;
    }

    public List<TodoItem> addTodo(String text) {
        // New document
        DocumentReference doc = firestore.collection("ppc").document();
        String id = doc.getId();
        TodoItem todoItem = new TodoItem(id, text);
        list.add(todoItem);
        doc.set(todoItem);
        return list;
    }

    public List<TodoItem> editItem(TodoItem item) {
        Log.d("TAMAR", "editing " + item.getText());
        list.set(find(item), item);
        firestore.collection("ppc").document(item.getId()).set(item);
        return list;
    }

    private CollectionReference init() {
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
        return tdLstCollectionRef;
    }
}
