package com.tomer.ppc;

import java.util.List;

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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_TO_DELETE = "to_delete";
    private static final int TO_DELETE_DEFAULT_VALUE = -1;

    private EditText editText;
    private Button submitButton;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<TodoItem> data;
    private TodoRepo todoRepo;
    private int toDelete = TO_DELETE_DEFAULT_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        todoRepo = ((PpcApplication) getApplication()).getTodoRepo();
        data = todoRepo.getAllItems();

        editText = findViewById(R.id.edit_text);
        submitButton = findViewById(R.id.submit_button);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(data);
        recyclerView.setAdapter(adapter);
        submitButton.setOnClickListener(view -> onSubmitButtonClicked());

        if (savedInstanceState != null) {
            toDelete = savedInstanceState.getInt(KEY_TO_DELETE, TO_DELETE_DEFAULT_VALUE);
            if (toDelete != TO_DELETE_DEFAULT_VALUE) {
                showDeleteDialog(toDelete);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TO_DELETE, toDelete);
    }

    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteItem(position))
                .setNegativeButton(android.R.string.no, null)
                .setOnDismissListener(view -> toDelete = TO_DELETE_DEFAULT_VALUE)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void onTodoItemClicked(int position, TodoItem todoItem) {
        if (todoItem.isDone()) {
            return;
        }
        todoItem.toggleIsDone();
        adapter.notifyItemChanged(position);
        todoRepo.notifyDataSetChanged(data);
        Log.d("TAMAR", "after click: " + todoRepo.getItemsCount());
    }

    private void onSubmitButtonClicked() {
        String userInput = editText.getText().toString().trim();
        if (userInput.length() == 0) {
            Toast.makeText(this, "you can't create an empty TODO item, oh silly!", Toast.LENGTH_SHORT).show();
            return;
        }
        editText.setText("");
        TodoItem newItem = new TodoItem(userInput);
        addItem(newItem);
    }

    private void addItem(TodoItem newItem) {
        data.add(newItem);
        todoRepo.addItem(newItem);
        adapter.notifyItemInserted(data.size() - 1);
        Log.d("TAMAR", "after add: " + todoRepo.getItemsCount());
    }

    private void deleteItem(int position) {
        data.remove(position);
        todoRepo.deleteItem(position);
        adapter.notifyItemRemoved(position);
        Log.d("TAMAR", "after del: " + todoRepo.getItemsCount());
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<TodoItem> data;

        public MyAdapter(List<TodoItem> data) {
            this.data = data;
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
                textView.setOnLongClickListener(view -> {
                    toDelete = curPos;
                    showDeleteDialog(toDelete);
                    return true;
                });
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
