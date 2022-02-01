package com.example.privacy_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SurveysListActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "staging_page";
    private Button mSignOutButton, mSubmitButton, mNextQuestionButton, mLastQuestionButton;
    private TextView question_field;
//    private EditText name_input_field;
//    private TextView name_display_field;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.surveys_list);

        TextView userID_display = findViewById(R.id.user_id_field);
        Intent intent = getIntent();
        userID_display.setText("Debug\n"+intent.getStringExtra(MainActivity.UUID));

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mSignOutButton = findViewById(R.id.sign_out_button);
        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent sign_out = MainActivity.createIntent(SurveysListActivity.this, MainActivity.class);
                startActivity(sign_out);
                finish();
            }
        });
        Question name = new Question("What's your name?");
        Question gender = new Question("What's your gender?","Male","Female","Other");
        Question major = new Question("What's your major?");
        Survey intro_survey = new Survey("Introduction",name,gender,major);
        List<Survey> surveys_list = Collections.singletonList(intro_survey);


        final int[] current_question = {0};


        question_field = findViewById(R.id.question_field);
        question_field.setText(intro_survey.getQuestionList().get(0).getQuestion());

        mLastQuestionButton = findViewById(R.id.last_question_button);
        mLastQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current_question[0] <= 0){
                   Toast.makeText(SurveysListActivity.this,"Can you don't",Toast.LENGTH_SHORT).show();
                }
                else {
                    current_question[0] -= 1;
                    question_field.setText(intro_survey.getQuestionList().get(current_question[0]).getQuestion());
                }
            }
        });
        mNextQuestionButton = findViewById(R.id.next_question_button);
        mNextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current_question[0] >= intro_survey.getQuestionList().size()-1){
                    Toast.makeText(SurveysListActivity.this,"Can you don't",Toast.LENGTH_SHORT).show();
                }
                else {
                    current_question[0] += 1;
                    question_field.setText(intro_survey.getQuestionList().get(current_question[0]).getQuestion());
                }
            }
        });

        DatabaseReference surveys_ref = database.getReference("surveys");
        mSubmitButton = findViewById(R.id.submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                surveys_ref.setValue(surveys_list);

            }
        });


//        Testing/learning code
//        final DatabaseReference myRef = database.getReference("name");
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String value = snapshot.getValue(String.class);
//                name_display_field.setText(value);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.d(TAG,"Couldn't read value");
//            }
//        });
//        name_input_field = findViewById(R.id.name_input_field);
//        name_display_field = findViewById(R.id.name_display_field);



        // TODO: Figure out how to use RecyclerView
        // TODO: How to add questions, from the app or from the console?
    }

    public static Intent createIntent(Context context){
        return new Intent(context, SurveysListActivity.class);
    }
    public static Intent createIntent(Context context, Class<?> cls){
        return new Intent(context,cls);
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