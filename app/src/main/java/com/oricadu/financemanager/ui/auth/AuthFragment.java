package com.oricadu.financemanager.ui.auth;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oricadu.financemanager.AuthActivity;
import com.oricadu.financemanager.ListCategoriesActivity;
import com.oricadu.financemanager.R;
import com.oricadu.financemanager.ui.profile.ProfileFragment;

public class AuthFragment extends Fragment {

    private TextView message;

    private EditText inputEmail, inputPassword, inputPhone;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private FirebaseAuth auth;
    private static int SIGN_IN_CODE = 1;

    private AuthViewModel mViewModel;

    private NavController navController;

    public static AuthFragment newInstance() {
        return new AuthFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        final View root = inflater.inflate(R.layout.auth_fragment, container, false);

        navController = Navigation.findNavController((Activity) root.getContext(), R.id.nav_host_fragment);

        auth = FirebaseAuth.getInstance();
//        btnSignIn = (Button) findViewById(R.id.);
        btnSignUp = (Button) root.findViewById(R.id.sign_up);
        btnSignIn = (Button) root.findViewById(R.id.sign_in);
        inputEmail = (EditText) root.findViewById(R.id.email);
        inputPassword = (EditText) root.findViewById(R.id.password);
        inputPhone = (EditText) root.findViewById(R.id.phone);
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
                        .addOnCompleteListener((Activity) root.getContext(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    Log.d("TAG", "signInWithEmail:success");
//                                    updateUI(user);
                                    navController.navigate(R.id.navigation_profile);
                                } else {
                                    Log.w("TAG", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(root.getContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
//                                    onCreateView(inflater, container, savedInstanceState);
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
                        .addOnCompleteListener((Activity) root.getContext(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "createUserWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    navController.navigate(R.id.navigation_profile);
//                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(root.getContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
                                }

                            }
                        });
            }


        });
        return root;
    }


}