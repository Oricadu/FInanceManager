package com.oricadu.financemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_PERMISSION_CODE = 100;

    private Button btnScan;
    private Button btnAuth;
    private BottomNavigationView bottomNavigationView;
    private BottomAppBar appBar;
    NavController navController;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();


    @Override
    protected void onStart() {
        super.onStart();

        /*if (user != null) {
            Intent intent = new Intent(MainActivity.this, ListCategoriesActivity.class);
            startActivity(intent);
        }*/
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



/*
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);*/

      /*  appBar = findViewById(R.id.bottom_app_bar);

        appBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent;
                // Handle presses on the action bar items
                switch (item.getItemId()) {
                    case R.id.profile:
                        intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.categories:
                        intent = new Intent(MainActivity.this, ListCategoriesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.home:
                        Toast.makeText(MainActivity.this, "click Home", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });*/



        Log.i("User", user.getUid());

        btnScan = findViewById(R.id.scan);
        btnAuth = findViewById(R.id.auth);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCameraPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE)) {
                    Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(MainActivity.this, "you deniyed permission", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (checkCameraPermission(Manifest.permission.INTERNET, CAMERA_PERMISSION_CODE)) {
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                startActivity(intent);

//                } else {
//                    Toast.makeText(MainActivity.this, "you deniyed permission", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }*/


    public boolean checkCameraPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{permission}, requestCode);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


}