package com.example.privacy_firebase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mUsernameField, mPasswordField;
    private Button mSignInButton;
    private Button mSignUpButton;
    private Button mClearFieldsButton;
    private static final String TAG = "sign_in_page";
    public static final String UUID = "user_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate(Bundle) called");
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mUsernameField = findViewById(R.id.username_input);
        mPasswordField = findViewById(R.id.password_input);



        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
        catch (Exception e){
            Log.d(TAG,"Couldn't getCurrentUser()");
            updateUI(null);
        }
        mClearFieldsButton = (Button) findViewById(R.id.clear_fields_button);
        mClearFieldsButton.setOnClickListener(v -> {
            mUsernameField.setText("");
            mPasswordField.setText("");
            signIn("huanmai101@gmail.com","huanmai101");
        });

        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password;
                String username;
                try{
                    password = inputFieldCheck(String.valueOf(mPasswordField.getText()),"Password");
                    username = inputFieldCheck(String.valueOf(mUsernameField.getText()),"Email");
                }
                catch(Exception e){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                signIn(username,password);
            }
        });

        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password;
                String username;
                try{
                    password = inputFieldCheck(String.valueOf(mPasswordField.getText()),"Password");
                    username = inputFieldCheck(String.valueOf(mUsernameField.getText()),"Email");
                }
                catch(Exception e){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                createAccount(username,password);

            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
        catch (Exception e){
            Log.d(TAG,"Couldn't getCurrentUser()");
            updateUI(null);
        }
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

    private void updateUI(FirebaseUser user) {
        if (user == null){
            mAuth.signOut();
            return;
        }
        else{
            Intent intent = SurveysListActivity.createIntent(this,SurveysListActivity.class);
            intent.putExtra(UUID, user.getUid());
            startActivity(intent);
        }
    }
    public static Intent createIntent(Context context){
        return new Intent(context,SurveysListActivity.class);
    }
    public static Intent createIntent(Context context, Class<?> cls){
        return new Intent(context,cls);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private String inputFieldCheck(String input, String field) throws Exception {
        if (input.equals("")){
            throw new Exception(String.format("%s cannot be blank",field));
        }
        else if (field.equalsIgnoreCase("password") && input.length()<8){
            throw new Exception(String.format("%s has to be at least 8 characters",field));
        }
        else if (field.equalsIgnoreCase("email")){
            if (!input.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")){
                throw new Exception("Invalid email formatting");
            }
        }
        return input;
    }
    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(MainActivity.this, "Account created.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }
    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Account signed in successfully",Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }
}