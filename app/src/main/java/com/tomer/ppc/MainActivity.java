package com.tomer.ppc;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    EditText editText;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text_view);
        editText = findViewById(R.id.edit_text);
        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(view -> {
            String userInput = editText.getText().toString();
            editText.setText("");
            textView.setText(userInput);
        });
    }
}
