package com.tomer.ppc;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements TodoRepo.Listener {
    public static final String TODO_ITEM_KEY = "todo_item_key";
    public static final String SHOULD_DELETE_KEY = "should_delete_key";
    public static final String UPDATED_ITEM_KEY = "updated_item_key";
    public static final int REQUEST_CODE_1 = 1;
    private static final int IN_PREVIEW_DEFAULT_VALUE = -1;

    private EditText editText;
    private Button submitButton;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<TodoItem> data = new ArrayList<>();
    private TodoRepo todoRepo;
    private TodoItem inPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        todoRepo = ((PpcApplication) getApplication()).getTodoRepo();
        todoRepo.setListener(this);

        editText = findViewById(R.id.edit_text);
        submitButton = findViewById(R.id.submit_button);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(data);
        recyclerView.setAdapter(adapter);
        submitButton.setOnClickListener(view -> onSubmitButtonClicked());
    }

    private void onTodoItemClicked(int position, TodoItem todoItem) {
        if (todoItem.isDone()) {
            return;
        }
        todoItem.toggleIsDone();
        updateItem(todoItem);
    }

    private boolean onTodoItemLongClicked(int position, TodoItem todoItem) {
        inPreview = todoItem;
        Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
        intent.putExtra(TODO_ITEM_KEY, todoItem);
        startActivityForResult(intent, REQUEST_CODE_1);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
        if (requestCode == REQUEST_CODE_1 && resultCode == RESULT_OK) {
            boolean shouldDelete = dataIntent.getBooleanExtra(SHOULD_DELETE_KEY, false);
            if (shouldDelete) {
                Log.d("TAMAR", "about to delete, my data is " + data.toString());
                deleteItem(inPreview);
            } else {
                TodoItem updatedItem = dataIntent.getParcelableExtra(UPDATED_ITEM_KEY);
                if (updatedItem != null) {
                    updateItem(updatedItem);
                }
            }
        }
        inPreview = null;
    }

    private void onSubmitButtonClicked() {
        String userInput = editText.getText().toString().trim();
        if (userInput.length() == 0) {
            Toast.makeText(this, "you can't create an empty TODO item, oh silly!", Toast.LENGTH_SHORT).show();
            return;
        }
        editText.setText("");
        addItem(userInput);
    }

    private void addItem(String text) {
        data = todoRepo.addTodo(text);
        adapter.setData(data);
    }

    private void updateItem(TodoItem item) {
        data = todoRepo.editItem(item);
        adapter.setData(data);
    }

    private void deleteItem(TodoItem item) {
        data = todoRepo.deleteItem(item);
        adapter.setData(data);
    }

    @Override
    public void notifyMe(List<TodoItem> list) {
        Log.d("TAMAR", "got notified");
        this.data = list;
        adapter.setData(data);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<TodoItem> data;

        public MyAdapter(List<TodoItem> data) {
            this.data = data;
        }

        public void setData(List<TodoItem> data) {
            this.data = data;
            adapter.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View todoItemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.todo_item, parent, false);
            return new ViewHolder(todoItemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(position, data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private int curPos;
            private TodoItem todoItem;
            private TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.todo_item_tv);
            }

            public void bind(int position, TodoItem todoItem) {
                curPos = position;
                this.todoItem = todoItem;
                textView.setText(todoItem.getText());
                if (todoItem.isDone()) {
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                textView.setOnClickListener(view -> onTodoItemClicked(position, todoItem));
                textView.setOnLongClickListener(view -> onTodoItemLongClicked(position, todoItem));
            }

            public int getCurPos() {
                return curPos;
            }

            public TodoItem getTodoItem() {
                return todoItem;
            }
        }
    }
}
