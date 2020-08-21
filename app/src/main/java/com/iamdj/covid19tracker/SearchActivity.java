package com.iamdj.covid19tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setSubtitle("Created by dj");

        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);

        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String countryName = editText.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("myKey", countryName); // passing country name to main activity
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}