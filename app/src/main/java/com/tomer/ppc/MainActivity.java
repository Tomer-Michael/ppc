package com.tomer.ppc;

import java.util.ArrayList;

import android.graphics.Paint;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {
    private static String DATA_KEY = "data";

    private EditText editText;
    private Button submitButton;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<TodoItem> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(DATA_KEY) != null) {
            data = savedInstanceState.getParcelableArrayList(DATA_KEY);
        } else {
            data = new ArrayList<>();
        }

        editText = findViewById(R.id.edit_text);
        submitButton = findViewById(R.id.submit_button);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(data);
        recyclerView.setAdapter(adapter);
        submitButton.setOnClickListener(view -> onSubmitButtonClicked());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(DATA_KEY, data);
    }


    private void onTodoItemClicked(int position, TodoItem todoItem) {
        if (todoItem.isDone()) {
            return;
        }
        todoItem.toggleIsDone();
        adapter.notifyItemChanged(position);
    }

    private void onSubmitButtonClicked() {
        String userInput = editText.getText().toString().trim();
        if (userInput.length() == 0) {
            Toast.makeText(this, "you can't create an empty TODO item, oh silly!", Toast.LENGTH_SHORT).show();
            return;
        }
        editText.setText("");
        data.add(new TodoItem(userInput));
        adapter.notifyItemInserted(data.size() - 1);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<TodoItem> data;

        public MyAdapter(ArrayList<TodoItem> data) {
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
            TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.todo_item_tv);
            }

            public void bind(int position, TodoItem todoItem) {
                textView.setText(todoItem.getText());
                if (todoItem.isDone()) {
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                textView.setOnClickListener(view -> onTodoItemClicked(position, todoItem));
            }
        }
    }



}
