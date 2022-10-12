package com.jared.apktest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Resources res = getResources();
        Log.d("MainActivity", String.format("--- res 111: " + res));

        tvName = findViewById(R.id.tvName);
        tvName.setText("--- activity 111");
        Button btNext = findViewById(R.id.btNext);
        btNext.setOnClickListener(view -> {
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        });
    }

}