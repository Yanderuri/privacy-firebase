package com.example.privacy_firebase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.UUID;

public class SurveysListActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "staging_page";
    private TextView question_field;
    private FirebaseDatabase mDatabase;
    private EditText answer_field;
    private static final Survey[] surveys_list_retrieved = new Survey[1];
    final int[] current_question = {0};
    final int[] current_survey = {0};
    private String[] user_answers;

    public static Survey snapshotToSurvey(@NonNull HashMap snap){
        ArrayList<HashMap> questionList = (ArrayList) snap.get("questionList");
        Survey answer = new Survey((String) snap.get("topic"));
        for(HashMap i : questionList){
            Question temp = new Question((String) i.get("question"));
            answer.addQuestion(temp);
        }
        return answer;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.surveys_list);

        TextView userID_display = findViewById(R.id.user_id_field);
        Intent intent = getIntent();
        userID_display.setText(MessageFormat.format("Debug\n{0}", intent.getStringExtra(MainActivity.UUID)));


        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        Button mSignOutButton = findViewById(R.id.sign_out_button);
        mSignOutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent sign_out = MainActivity.createIntent(SurveysListActivity.this, MainActivity.class);
            startActivity(sign_out);
            finish();
        });
//        Question name = new Question("What's your name?");
//        Question gender = new Question("What's your gender?","Male","Female","Other");
//        Question age_group = new Question("What's your age?","18-27","28-37","38-47","48-57","58+");
//        Question major = new Question("What's your major?");
//        Survey intro_survey = new Survey("Introduction",name,gender,age_group,major);
//
//        List<Survey> surveys_list = Collections.singletonList(intro_survey);
        question_field = findViewById(R.id.question_field);
        try {
            mDatabase.getReference().child("surveys").child(String.valueOf(current_survey[0])).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Error getting data", task.getException());
                }
                else {
                    // TODO: Convert this to request survey on command
                    surveys_list_retrieved[current_survey[0]] = snapshotToSurvey( (HashMap) task.getResult().getValue());
                    Log.d(TAG, "Successful questions retrieval");
                    question_field.setText(surveys_list_retrieved[current_survey[0]].getQuestionList().get(current_question[0]).getQuestion());
                    Log.d(TAG, String.valueOf(surveys_list_retrieved[0].getQuestionList().size()));
                    user_answers = new String[surveys_list_retrieved[0].getQuestionList().size()];
                }

            });
        }
        catch(Exception e){
            Log.e(TAG,"Retrieval failed");
        }


        answer_field = findViewById(R.id.answer_field);
        answer_field.setText("");
        // TODO: Actually saving users answers


        Button mLastQuestionButton = findViewById(R.id.last_question_button);
        mLastQuestionButton.setOnClickListener(view -> {
            if (current_question[0] <= 0){
               Toast.makeText(SurveysListActivity.this,"Can you don't",Toast.LENGTH_SHORT).show();
            }
            else {
                current_question[0] -= 1;
                answer_field.setText(user_answers[current_question[0]]);
                question_field.setText(surveys_list_retrieved[0].getQuestionList().get(current_question[0]).getQuestion());
            }
        });
        Button mNextQuestionButton = findViewById(R.id.next_question_button);
        mNextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current_question[0] >= surveys_list_retrieved[0].getQuestionList().size() - 1) {
                    Toast.makeText(SurveysListActivity.this, "Can you don't", Toast.LENGTH_SHORT).show();
                    user_answers[current_question[0]] = String.valueOf(answer_field.getText());
                } else {
                    user_answers[current_question[0]] = String.valueOf(answer_field.getText());
                    current_question[0] += 1;
                    answer_field.setText(user_answers[current_question[0]]);
                    question_field.setText(surveys_list_retrieved[0].getQuestionList().get(current_question[0]).getQuestion());
                }
            }
        });

        Button mSubmitButton = findViewById(R.id.submit_button);
        mSubmitButton.setOnClickListener(v -> {
            mDatabase.getReference().child("responses").child(intent.getStringExtra(MainActivity.UUID)).setValue(user_answers);
            // TODO: Also remember to apply OLH
        });
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