package com.example.curasync_docapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//import com.google.firebase.FirebaseApp;

public class welcomeScreen extends AppCompatActivity {

    private TextView tvLoginWelcome, tvDocReg;
    private Button btnCreateAccWelcome;
    private LinearLayout googleUser, guestUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_screen);
//        FirebaseApp.initializeApp(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvLoginWelcome = findViewById(R.id.tvLoginWelcome);
        tvDocReg = findViewById(R.id.tvDocReg);
        btnCreateAccWelcome = findViewById(R.id.btnCreateAccWelcome);
        googleUser = findViewById(R.id.googleUser);
        guestUser = findViewById(R.id.guestUser);


        tvLoginWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(welcomeScreen.this, login.class);
                startActivity(loginIntent);
                System.out.println("Login button has been Clicked");
                finish();
            }
        });

        btnCreateAccWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(welcomeScreen.this, signup.class);
                startActivity(registerIntent);
                System.out.println("Create Account has been Clicked");
                finish();
            }
        });

        googleUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(welcomeScreen.this, "Google Login will be incorporated in next Update.", Toast.LENGTH_SHORT).show();
            }
        });

        guestUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("GUEST_USER", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isGuest", true);   //guest flag - guest user
                editor.putBoolean("isAuthenticated", false); //authentic user
                editor.apply();

                Intent guestIntent = new Intent(welcomeScreen.this, Home.class);
                startActivity(guestIntent);
//                finish();
                System.out.println("Guest has been Clicked");

            }
        });

        tvDocReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("DOC Reg button has been Clicked");

                Intent docIntent = new Intent(welcomeScreen.this, DoctorSignupActivity.class);
                startActivity(docIntent);
//                finish();
            }
        });
    }//onCreate
}