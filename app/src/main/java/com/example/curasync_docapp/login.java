package com.example.curasync_docapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class login extends AppCompatActivity {

    private TextView tvForgotPass, tvSignUp_inLogin;
    private EditText etEmailLogin, etPasswordLogin;
    private ImageView ivShowPassword;
    private CheckBox cbRemember;
    private Button btnLogin;
    private ProgressBar progressBarLogin;
    Handler handler = new Handler(Looper.getMainLooper());
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvForgotPass = findViewById(R.id.tvForgotPass);
        tvSignUp_inLogin = findViewById(R.id.tvSignUp_inLogin);
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        ivShowPassword = findViewById(R.id.ivShowPassword);
        cbRemember = findViewById(R.id.cbRemember);
        btnLogin = findViewById(R.id.btnLogin);
        progressBarLogin = findViewById(R.id.progressBarLogin);

        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(login.this, "Forgot Password will be in next Update.", Toast.LENGTH_SHORT).show();
            }
        });

        tvSignUp_inLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Login=Sign Up clicked");
                Intent i_signFromLogin = new Intent(login.this, signup.class);
                startActivity(i_signFromLogin);
            }
        });

        ivShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPasswordLogin.getText().toString().trim();
                if (!password.isEmpty()) {
                    Toast.makeText(login.this, "Password: " + password, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(login.this, "Please enter password first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(verify_inputs()){//success
                    loginUser();
//                    Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
//                    Intent i_homeFromLogin = new Intent(login.this, Home.class);
//                    startActivity(i_homeFromLogin);
//                    finish();
                }
                else{
                    Toast.makeText(login.this, "Login failed. Please fix errors.", Toast.LENGTH_SHORT).show();
                    System.out.println("staying on login page");
//
//                    // Clear fields after 1.5 seconds
//                    new android.os.Handler().postDelayed(() -> clearALlInfo(), 1500);
                }
            }
        });
    }//onCreate

    private String[] getInputValues() {
        String email = etEmailLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();
        boolean rememberMe = cbRemember.isChecked();
        return new String[]{email, password, String.valueOf(rememberMe)};
    }

    public boolean verify_inputs(){
        String[] inputs = getInputValues();
        String email = inputs[0];
        String password = inputs[1];

        List<String> errors = new ArrayList<>();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill email & password", Toast.LENGTH_SHORT).show();
            System.out.println("Login = validation failed: Empty fields");
            return false;
        }

        if (!isValidEmailAddress(email)) {
            errors.add("Invalid email address!");
        }

        if (password.length() < 5) {
            errors.add("Password must be at least 5 characters");
        }

        if (!errors.isEmpty()) {
            showValidationErrors(errors);
            return false;
        }

        // Remember Me checkbox
        boolean shouldRememberUser = cbRemember.isChecked();
        System.out.println("In Log In Page");
        System.out.println("Email: " + email + " Pass: " + password );
        System.out.println("Remember User " + shouldRememberUser);
        return true;
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private void showValidationErrors(List<String> errors) {
        StringBuilder errorMessage = new StringBuilder();

        for (String error : errors) {
            errorMessage.append(error).append("\n");
        }
        Toast.makeText(this, ""+errorMessage.toString(), Toast.LENGTH_LONG).show();
    }

    private void loginUser() {
        String[] inputs = getInputValues();
        String email = inputs[0];
        String password = inputs[1];
        boolean rememberMe = Boolean.parseBoolean(inputs[2]);


        progressBarLogin.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        handler.post(() -> {
                            progressBarLogin.setVisibility(View.GONE);
                            btnLogin.setEnabled(true);
                            Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                            Intent i_homeFromLogin = new Intent(login.this, Home.class);
                            startActivity(i_homeFromLogin);
                            finish();
                        });
                    } else {
                        // failed
                        handler.post(() -> {
                            progressBarLogin.setVisibility(View.GONE);
                            btnLogin.setEnabled(true);
                            Toast.makeText(login.this, "Login failed: ", Toast.LENGTH_LONG).show();
                            System.out.println("Log In - " + task.getException().getMessage());
                        });
                    }
                });

        //  login check
//        new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
//            progressBarLogin.setVisibility(View.GONE);
//            btnLogin.setEnabled(true);
//
//            Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
//            Intent i_homeFromLogin = new Intent(login.this, Home.class);
//            startActivity(i_homeFromLogin);
//            finish();
//        }, 2000);
    }
}