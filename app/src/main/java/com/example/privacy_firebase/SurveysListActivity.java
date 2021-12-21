package com.example.privacy_firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SurveysListActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private static final String TAG = "staging_page";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.surveys_list);

        TextView userID_display = findViewById(R.id.user_id_field);
        Intent intent = getIntent();
        userID_display.setText("Debug\n"+intent.getStringExtra(MainActivity.UUID));

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // TODO: Figure out how to use RecyclerView
        // TODO: How to define questions/answers, surveys, and store them in Firebase
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume() called");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy() called");
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG,"onPause() called");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop() called");
    }
}