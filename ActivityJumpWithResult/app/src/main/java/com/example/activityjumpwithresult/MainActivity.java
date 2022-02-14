package com.example.activityjumpwithresult;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ONE = 1;
    private static final int REQUEST_TWO = 2;

    private Button btn1;
    private Button btn2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(ChooseActivity.newIntent(MainActivity.this), REQUEST_ONE);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(ChooseActivity.newIntent(MainActivity.this), REQUEST_TWO);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            showResult(requestCode, ChooseActivity.getResultAnswer(data));
        }
    }
    private void showResult(int requestCode, int answer) {
        String str;
        if (requestCode == answer) {
            str = "正确";
        } else {
            str = "错误";
        }
        Snackbar.make(this.getWindow().getDecorView(), str, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}