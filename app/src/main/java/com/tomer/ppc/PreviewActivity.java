package com.tomer.ppc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class PreviewActivity extends Activity {
    private static final String KEY_TO_DELETE = "to_delete";
    private boolean toDelete;
    TodoItem todoItem;
    private TextView idTextView;
    private TextView textTextView;
    private TextView creationTextView;
    private TextView editTimeTextView;
    private EditText editText;
    private Button commitButton;
    private Button toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Intent intent = getIntent();
        todoItem = intent.getParcelableExtra(MainActivity.TODO_ITEM_KEY);

        idTextView = findViewById(R.id.todo_id_tv);
        idTextView.setText("ID: " + todoItem.getId());

        textTextView = findViewById(R.id.todo_text_tv);
        textTextView.setText("Content: " + todoItem.getText());

        creationTextView = findViewById(R.id.todo_creation_tv);
        creationTextView.setText("Creation timestamp: " + todoItem.getCreationTimestamp());

        editTimeTextView = findViewById(R.id.todo_edit_tv);
        editTimeTextView.setText("Last Edit Timestamp (test me!): " + todoItem.getEditTimestamp());

        editText = findViewById(R.id.edit_text);
        commitButton = findViewById(R.id.commit_button);
        if (todoItem.isDone()) {
            editText.setVisibility(View.INVISIBLE);
            commitButton.setOnClickListener(view -> {
                toDelete = true;
                showDeleteDialog();
            });
        } else {
            commitButton.setOnClickListener(view -> onSubmitButtonClicked());
        }

        toggleButton = findViewById(R.id.toggle_button);
        toggleButton.setOnClickListener(view -> {
            todoItem.toggleIsDone();
            returnUpdatedItem();
        });

        if (savedInstanceState != null) {
            toDelete = savedInstanceState.getBoolean(KEY_TO_DELETE, false);
            if (toDelete) {
                showDeleteDialog();
            }
        }
    }

    private void onSubmitButtonClicked() {
        String userInput = editText.getText().toString().trim();
        if (userInput.length() == 0) {
            Toast.makeText(this, "you can't create an empty TODO item, oh silly!", Toast.LENGTH_SHORT).show();
            return;
        }
        editText.setText("");
        todoItem.setText(userInput);
        returnUpdatedItem();
    }

    private void returnUpdatedItem() {
        Intent intent = new Intent();
        intent.putExtra(MainActivity.UPDATED_ITEM_KEY, todoItem);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void deleteItem() {
        Intent intent = new Intent();
        intent.putExtra(MainActivity.SHOULD_DELETE_KEY, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void returnUnchanged() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        returnUnchanged();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_TO_DELETE, toDelete);
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteItem())
                .setNegativeButton(android.R.string.no, null)
                .setOnDismissListener(view -> toDelete = false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}