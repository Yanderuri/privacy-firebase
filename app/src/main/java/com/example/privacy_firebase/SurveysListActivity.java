package com.example.privacy_firebase;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SurveysListActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "staging_page";
    private TextView question_field;
    private FirebaseDatabase mDatabase;
    private EditText answer_field;
    private String[] user_answers;
    private static final Survey[] surveys_list_retrieved = new Survey[1];
    static final int[] current_question = {0};
    static final int[] current_survey = {0};
    private TextView answers_suggestions;


    public static Survey snapshotToSurvey(@NonNull HashMap snap){
        Log.d(TAG, String.valueOf(snap));
        List<HashMap> questionList = (ArrayList <HashMap>) snap.get("questionList");
        Survey answer = new Survey((String) snap.get("topic"));
        assert questionList != null;
        for(HashMap i : questionList){
            Question temp = new Question((String) i.get("question"));
            if (i.containsKey("answers_list")){
                temp =  new Question((String) i.get("question"), (List<String>) i.get("answers_list"));
            }
            answer.addQuestion(temp);
        }
        return answer;
    }
    public static List<String> answersFormatter(int current_question){
        return surveys_list_retrieved[current_survey[0]].getQuestionList().get(current_question).getAnswers_list() == null ? Arrays.asList("Answers vary") : surveys_list_retrieved[current_survey[0]].getQuestionList().get(current_question).getAnswers_list();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.surveys_list);

        TextView userID_display = findViewById(R.id.user_id_field);
        Intent intent = getIntent();
        userID_display.setText(MessageFormat.format("Debug\n{0}", intent.getStringExtra(MainActivity.UUID)));

        mDatabase = FirebaseDatabase.getInstance("https://data-privacy-app-v2-default-rtdb.firebaseio.com/");
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
//        Question age_group = new Question("What's your age?","18-27","28-37","38-47","48-57","58-99");
//        Question major = new Question("What's your major?");
//        Survey intro_survey = new Survey("Introduction",name,gender,age_group,major);
//        List<Survey> surveys_list = Collections.singletonList(intro_survey);
//        mDatabase.getReference().child("surveys").setValue(surveys_list);
//        Log.d(TAG,"Submitted questions");

        answers_suggestions     = findViewById(R.id.answers_suggestion);
        question_field          = findViewById(R.id.question_field);
        answer_field            = findViewById(R.id.answer_field);
        final boolean[] retrieval_success = {false};
        try {
            mDatabase.getReference().child("surveys").child(String.valueOf(current_survey[0])).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Error getting data", task.getException());
                }
                else {
                    // TODO: Convert this to request survey on command
                    surveys_list_retrieved[current_survey[0]] = snapshotToSurvey( (HashMap) Objects.requireNonNull(task.getResult().getValue()));
                    Log.d(TAG, "Successful survey retrieval");
                    retrieval_success[0] = true;
                    answers_suggestions.setText(answersFormatter(current_question[0]).toString());
                    question_field.setText(surveys_list_retrieved[current_survey[0]].getQuestionList().get(current_question[0]).getQuestion());
                    user_answers = new String[surveys_list_retrieved[current_survey[0]].getQuestionList().size()];
                }

            });
        }
        catch(Exception e){
            Log.e(TAG,"Retrieval failed somewhere");
        }


        answer_field.setText("");

        Button mLastQuestionButton = findViewById(R.id.last_question_button);
        mLastQuestionButton.setOnClickListener(view -> {
            if (current_question[0] <= 0){
               Toast.makeText(SurveysListActivity.this,"Can you don't",Toast.LENGTH_SHORT).show();
            }
            else {
                user_answers[current_question[0]] = String.valueOf(answer_field.getText());
                current_question[0] -= 1;
                answers_suggestions.setText(answersFormatter(current_question[0]).toString());
                answer_field.setText(user_answers[current_question[0]]);
                question_field.setText(surveys_list_retrieved[0].getQuestionList().get(current_question[0]).getQuestion());
            }
        });
        Button mNextQuestionButton = findViewById(R.id.next_question_button);
        mNextQuestionButton.setOnClickListener(view -> {
            if (current_question[0] >= surveys_list_retrieved[0].getQuestionList().size()-1){
                Toast.makeText(SurveysListActivity.this,"Press submit if finished",Toast.LENGTH_SHORT).show();
                user_answers[current_question[0]] = String.valueOf(answer_field.getText());
            }
            else {
                user_answers[current_question[0]] = String.valueOf(answer_field.getText());
                current_question[0] += 1;
                answers_suggestions.setText(answersFormatter(current_question[0]).toString());
                question_field.setText(surveys_list_retrieved[0].getQuestionList().get(current_question[0]).getQuestion());
                answer_field.setText(user_answers[current_question[0]]);
            }
        });

        Button mSubmitButton = findViewById(R.id.submit_button);
        mSubmitButton.setOnClickListener(v -> {
            // Data Sanitation

            for(int i = 0;i<user_answers.length;i++)
                user_answers[i] = user_answers[i] == null ? "" : user_answers[i].toLowerCase().trim();

            int[] filtered_answers = new int[user_answers.length];
            for(int i = 0;i<user_answers.length;i++)
                filtered_answers[i] = sortToBuckets(user_answers[i], answersFormatter(i));

            int[] perturbed_answers = new int[filtered_answers.length];
            for(int i = 0;i<filtered_answers.length;i++){
                if(retrieval_success[0] && filtered_answers[i] != -1){
                    LH_client client = new LH_client(2,surveys_list_retrieved[current_survey[0]].getQuestionList().get(i).getAnswers_list().size());
                    perturbed_answers[i] = client.perturb(filtered_answers[i]);
                }
            }

            String[] fil_answers = new String[filtered_answers.length];
            for(int i = 0;i<filtered_answers.length;i++){
                fil_answers[i] = String.valueOf(filtered_answers[i]);
            }
            String[] ptb_answers = new String[perturbed_answers.length];
            for(int i = 0;i<perturbed_answers.length;i++){
                ptb_answers[i] = String.valueOf(perturbed_answers[i]);
            }

            mDatabase.getReference().child("responses").child(intent.getStringExtra(MainActivity.UUID)).setValue(Arrays.asList(user_answers));
            mDatabase.getReference().child("converted").child(intent.getStringExtra(MainActivity.UUID)).setValue(Arrays.asList(fil_answers));
            mDatabase.getReference().child("perturbed").child(intent.getStringExtra(MainActivity.UUID)).setValue(Arrays.asList(ptb_answers));


            Toast.makeText(SurveysListActivity.this, "Thanks", Toast.LENGTH_SHORT).show();
            // TODO: Also remember to apply OLH
        });
    }
    public static int sortToBuckets(String answer, List<String> dataRange){
        if (Objects.equals(dataRange.get(0), "Answers vary")) {
            return -1;
        }
        try {
            int number = Integer.parseInt(answer.toLowerCase().trim());
            // only for strings in the form of "a-b", with a and b being integers, and a dash(-)
            for(int i = 0;i<dataRange.size();i++){
                String[] splitted = dataRange.get(i).split("-",2);
                Integer[] conv = new Integer[splitted.length];
                conv[0] = Integer.parseInt(splitted[0]);
                conv[1] = Integer.parseInt(splitted[1]);
                if (number>=conv[0] && number<=conv[1]){
                    return i;
                }
            }
        }
        catch (NumberFormatException e) {
            Log.d(TAG,String.format("%s: not a number",answer));
        }
        catch(Exception e){
            Log.d(TAG, "Crashed inside sortToBuckets");
        }
        // generic data matching
        for(int j = 0;j<dataRange.size();j++){
            if(answer.equalsIgnoreCase(dataRange.get(j))){
                return j;
            }
        }

        return 17;
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