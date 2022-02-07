package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "skywang-->Bundle01";

    private Button mBtnBasic = null;
    private Button mBtnPar = null;
    private Button mBtnSer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnBasic = (Button) findViewById(R.id.btnBasic);
        mBtnBasic.setOnClickListener((View.OnClickListener) this);

        mBtnPar = (Button) findViewById(R.id.btnPar);
        mBtnPar.setOnClickListener((View.OnClickListener)this);

        mBtnSer = (Button) findViewById(R.id.btnSer);
        mBtnSer.setOnClickListener((View.OnClickListener)this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBasic:
                sendBasicDataThroughBundle();
                break;
            case R.id.btnPar:
                sendParcelableDataThroughBundle();
                break;
            case R.id.btnSer:
                sendSeriableDataThroughBundle();
                break;
            default:
                break;
        }
    }

    // sent basic data, such as int, strin, etc...  through bundle
    private void sendBasicDataThroughBundle() {
        // "com.test" is the package name of the destination class
        // "com.test.Activity02" is the full class path of the destination class
        Intent intent = new Intent().setClassName("com.example.myapplication", "com.example.myapplication.Bundle02");

        Bundle bundle = new Bundle();
        bundle.putString("name", "skywang");
        bundle.putInt("height", 175);
        intent.putExtras(bundle);

        startActivity(intent);

        // end current class
        finish();
    }
    // sent object through Pacelable
    private void sendParcelableDataThroughBundle(){
        Intent intent = new Intent().setClassName("com.example.myapplication", "com.example.myapplication.Bundle02");

        Book mBook = new Book();
        mBook.setBookName("Android");
        mBook.setAuthor("skywang");
        mBook.setPublishTime(2013);

        Bundle mBundle = new Bundle();
        mBundle.putParcelable("ParcelableValue", mBook);
        intent.putExtras(mBundle);

        startActivity(intent);
        finish();
    }
    // sent object through seriable
    private void sendSeriableDataThroughBundle(){
        Intent intent = new Intent().setClassName("com.example.myapplication", "com.example.myapplication.Bundle02");

        Person mPerson = new Person();
        mPerson.setName("skywang");
        mPerson.setAge(24);

        Bundle mBundle = new Bundle();
        mBundle.putSerializable("SeriableValue",mPerson);
        intent.putExtras(mBundle);

        startActivity(intent);
        finish();
    }
}