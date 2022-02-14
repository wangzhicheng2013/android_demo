package com.example.activityjumpwithresult;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseActivity extends AppCompatActivity {

    public static final String RESULT_ANSWER = "result_answer";

    public static Intent newIntent(Context context) {
        return new Intent(context, ChooseActivity.class);
    }

    public static int getResultAnswer(Intent intent) {
        return intent.getIntExtra(RESULT_ANSWER, 0);
    }

    private Button btn1;
    private Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra(RESULT_ANSWER, 1);
                setResult(RESULT_OK, i);
                finish();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra(RESULT_ANSWER, 2);
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }
}