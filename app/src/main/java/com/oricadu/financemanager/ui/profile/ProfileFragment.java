package com.oricadu.financemanager.ui.profile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oricadu.financemanager.R;
import com.oricadu.financemanager.ui.auth.AuthFragment;

public class ProfileFragment extends Fragment {

    private ProfileViewModel notificationsViewModel;
    private Button btnLogOut, btnAuth;
    private ViewGroup linear_container;
    private TextView textView;

    private FirebaseUser user;
    private FirebaseAuth auth;

    private View root;

    private NavController navController;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        /*final TextView textView = root.findViewById(R.id.text_profile);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        navController = Navigation.findNavController((Activity) root.getContext(), R.id.nav_host_fragment);

        textView = root.findViewById(R.id.text_profile);
        linear_container = root.findViewById(R.id.linear_container);

        updateUI(user);

        return root;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {

            textView = new TextView(root.getContext());
            textView.setText(user.getEmail());
            linear_container.addView(textView);

            btnLogOut = new Button(root.getContext());
            btnLogOut.setText("log out");
            linear_container.addView(btnLogOut);

            btnLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    auth.signOut();
                    updateUI(auth.getCurrentUser());

                }
            });

        } else {
            linear_container.removeAllViews();

            btnAuth = new Button(root.getContext());
            btnAuth.setText("auth");
            linear_container.addView(btnAuth);

            btnAuth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AuthFragment nextFrag= new AuthFragment();
                    navController.navigate(R.id.navigation_auth);

                    /*getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(((ViewGroup) getView().getParent()).getId(), nextFrag, "findThisFragment")
                            .addToBackStack(null)
                            .commit();*/


                }
            });


        }
    }
}