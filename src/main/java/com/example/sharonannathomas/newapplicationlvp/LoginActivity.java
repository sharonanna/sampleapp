package com.example.sharonannathomas.newapplicationlvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.support.annotation.NonNull;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {



        private static final String TAG = "LoginActivity";
        /**
         * Keep track of the login task to ensure we can cancel it if requested.
         */
        private FirebaseAuth mAuth;
        private FirebaseAuth.AuthStateListener mAuthListener;
        // UI references.
        private EditText mEmailView;
        private EditText mPasswordView;
        private Button mEmailSignInButton;
        private Button mEmailRegisterButton;

        @Override
        public void onStart() {
            super.onStart();
            mAuth.addAuthStateListener(mAuthListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // If a user is signed in then proceed to file upload page.
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                }
            };
            mEmailView = (EditText) findViewById(R.id.email);
            mPasswordView = (EditText) findViewById(R.id.password);


            mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isValidInput(mEmailView,mPasswordView)){
                        userLogin(mEmailView.getText().toString(), mPasswordView.getText().toString());
                    }
                }
            });

            mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);
            mEmailRegisterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isValidInput(mEmailView,mEmailView)){
                        registerUser(mEmailView.getText().toString(),mPasswordView.getText().toString());
                    }

                }
            });
        }

        private boolean isValidInput(EditText mEmailView,EditText mPasswordView){
            if(mEmailView.getText().toString().isEmpty()){
                mEmailView.setError("Email field should not be empty");
                return false;
            }else if(mPasswordView.getText().toString().isEmpty()){
                mPasswordView.setError("Password field should not be empty");
                return false;
            }
            return true;
        }


        private void registerUser(String email, String password) {

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e) {
                        mPasswordView.setError(e.getReason());
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        mEmailView.setError(e.getMessage());
                    } catch (FirebaseAuthUserCollisionException e) {
                        mEmailView.setError(e.getMessage());
                    } catch (Exception e) {
                        Log.d(TAG,e.getMessage());
                    }
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                    }

                }
            });

        }

        private void userLogin(String email, String password) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthInvalidUserException e) {
                                mEmailView.setError(e.getMessage());
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                switch(e.getErrorCode()){
                                    case "ERROR_INVALID_EMAIL":
                                        mEmailView.setError(e.getMessage());
                                        break;
                                    case "ERROR_INVALID_PASSWORD":
                                        mPasswordView.setError(e.getMessage());
                                        break;
                                    default:
                                        mEmailView.setError(e.getMessage());
                                }
                            } catch (Exception e) {
                                Log.d(TAG,e.getMessage());
                            }
                            if(task.isSuccessful()){
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);

                            }
                        }
                    });
        }

        @Override
        public void onResume(){
            super.onResume();
            mPasswordView.setText("");
        }
        @Override
        public void onStop() {
            super.onStop();
        }




    }
