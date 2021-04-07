package com.oricadu.financemanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oricadu.financemanager.FNS.*;

import java.io.IOException;



public class AuthActivity extends AppCompatActivity {

    private TextView message;

    private EditText inputEmail, inputPassword, inputPhone;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private FirebaseAuth auth;
    private static int SIGN_IN_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        message = findViewById(R.id.message);

        auth = FirebaseAuth.getInstance();

//        btnSignIn = (Button) findViewById(R.id.);
        btnSignUp = (Button) findViewById(R.id.sign_up);
        btnSignIn = (Button) findViewById(R.id.sign_in);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPhone = (EditText) findViewById(R.id.phone);
//        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

//        FirebaseUser currentUser = auth.getCurrentUser();
//        updateUI(currentUser);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                String phone = inputPhone.getText().toString().trim();

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    Log.d("TAG", "signInWithEmail:success");
//                                    updateUI(user);
                                    Intent intent = new Intent(AuthActivity.this, ListCategoriesActivity.class);
                                    startActivity(intent);
                                } else {
                                    Log.w("TAG", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(AuthActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
                                }
                            }
                        });
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                String phone = inputPhone.getText().toString().trim();

              /*  if(auth.getCurrentUser() == null) {
                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),
                            SIGN_IN_CODE);
                } else {
                    Toast.makeText(AuthActivity.this, "you are authorised",
                            Toast.LENGTH_LONG).show();
                }*/
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "createUserWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    Intent intent = new Intent(AuthActivity.this, ListCategoriesActivity.class);
                                    startActivity(intent);
//                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(AuthActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
                                }

                            }
                        });
            }


        });

        /*try {
            FNS.signUp("maksimova0130@gmail.com", "oricadu", "+79831323294");
        } catch (InternalFnsException | UserAlreadyExistException | IOException e) {
            message.setText(e.getMessage());
            System.err.println(e.getMessage());
        }*/


    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(AuthActivity.this, "authorised", Toast.LENGTH_LONG).show();
            }
        }
    }*/
}