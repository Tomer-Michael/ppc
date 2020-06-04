package com.tomer.ppc;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;

public class TodoRepo {
    public interface Listener {
        public void notifyMe(List<TodoItem> list);
    }

    private static final String COLLECTION = "tomer";
    private List<TodoItem> list = new ArrayList<>();
    public FirebaseFirestore db;
    private Listener listener;

    public TodoRepo(Context context) {
        db = FirebaseFirestore.getInstance();
        init();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public int getItemsCount() {
        return list.size();
    }

    public List<TodoItem> deleteItem(TodoItem item) {
        list.remove(find(item));
        db.collection(COLLECTION).document(item.getId()).delete();
        return list;
    }

    public int find(TodoItem item) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(item.getId())) {
                return i;
            }
        }
        return -1;
    }

    public List<TodoItem> addTodo(String text) {
        Log.d("TAMAR", "GREAT ADDING");
        DocumentReference doc = db.collection(COLLECTION).document();
        String id = doc.getId();
        TodoItem todoItem = new TodoItem(id, text);
        list.add(todoItem);
        Log.d("TAMAR", "GREAT ID " + id);

        doc.set(todoItem.describe())
                .addOnSuccessListener(unused -> Log.d("TAMAR", "GREAT SUCCESS!"))
                .addOnFailureListener(e -> Log.e("TAMAR", "GREAT FAIL!", e));
//        db.collection(COLLECTION).add(todoItem.describe())
//                .addOnSuccessListener(unused -> Log.d("TAMAR", "GREAT SUCCESS!"))
//                .addOnFailureListener(e -> Log.e("TAMAR", "GREAT FAIL!", e));
        return list;
    }

    public List<TodoItem> editItem(TodoItem item) {
        Log.d("TAMAR", "editing " + item.getText());
        list.set(find(item), item);
        db.collection(COLLECTION).document(item.getId()).set(item.describe());
        return list;
    }

    private CollectionReference init2() {
        CollectionReference collectionReference = db.collection(COLLECTION);
        collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w("TAMAR Firestore", "Listen failed.", e);
                return;
            }
            if (queryDocumentSnapshots == null) {
                Log.d("TAMAR Firestore", "Current data: null");
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

        collectionReference.get().addOnCompleteListener(task -> {
            if (listener != null) {
                Log.d("TAMAR", "About to notify, list is " + list.toString());
                listener.notifyMe(list);
            }
        });
        collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (listener != null) {
                Log.d("TAMAR", "About to notify, list is " + list.toString());
                listener.notifyMe(list);
            }
        });
        return collectionReference;
    }

    public List<TodoItem> init()
    {
        list.clear();
        db.collection(COLLECTION).get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot document : documents) {
                TodoItem task = new TodoItem(document);
                list.add(task);
            }
            if (listener != null) {
                Log.d("TAMAR", "About to notify, list is " + list.toString());
                listener.notifyMe(list);
            }
        });
        return list;
    }
}
