package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Bundle02 extends Activity implements View.OnClickListener {
    private static final String TAG = "skywang-->Bundle02";

    private Button mBtnBack = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2);

        mBtnBack = (Button) findViewById(R.id.btnBack);
        mBtnBack.setOnClickListener(this);

        receiveBasicData();
        receiveParcelableData();
        receiveSeriableData();
    }

    private void receiveBasicData() {
        Bundle bundle = this.getIntent().getExtras();

        String name = bundle.getString("name");
        int height = bundle.getInt("height");
        if (name != null && height != 0)
            Log.d(TAG, "receice basic data -- " +
                    "name="+name+", height="+height);
    }

    private void receiveParcelableData() {
        Book mBook = (Book)getIntent().getParcelableExtra("ParcelableValue");
        if (mBook != null)
            Log.d(TAG, "receice parcel data -- " +
                    "Book name is: " + mBook.getBookName()+", "+
                    "Author is: " + mBook.getAuthor() + ", "+
                    "PublishTime is: " + mBook.getPublishTime());
    }

    private void receiveSeriableData() {
        Person mPerson = (Person)getIntent().getSerializableExtra("SeriableValue");
        if (mPerson != null)
            Log.d(TAG, "receice serial data -- " +
                    "The name is:" + mPerson.getName() + ", "+
                    "age is:" + mPerson.getAge());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
            {
                // "com.test" is the package name of the destination class
                // "com.test.Activity01" is the full class path of the destination class
                Intent intent = new Intent().setClassName("com.example.myapplication", "com.example.myapplication.MainActivity");
                startActivity(intent);
                // end current class
                finish();
            }
            break;
            default:
                break;

        }
    }

}
