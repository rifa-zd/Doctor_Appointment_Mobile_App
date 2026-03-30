package com.example.curasync_docapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class signup extends AppCompatActivity {

    final Calendar calendar = Calendar.getInstance();

    private EditText etFullNameSign, etEmailSign, etBirthDateSign, etPhoneSign, etPasswordSign, etRetypePassSign;
    private ImageView ivShowPassSign, ivShowRetypePassSign, ivCalendarIcon;
    private Button btnRegister;
    private TextView tvLogin_inSignUp;
    private ProgressBar progressBarSign;
    private FirebaseAuth auth;
    Handler handler = new Handler(Looper.getMainLooper());


//    Firebase Auth runs on background threads


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etFullNameSign = findViewById(R.id.etFullNameSign);
        etEmailSign = findViewById(R.id.etEmailSign);
        etBirthDateSign = findViewById(R.id.etBirthDateSign);
        etPhoneSign = findViewById(R.id.etPhoneSign);
        etPasswordSign = findViewById(R.id.etPasswordSign);
        etRetypePassSign = findViewById(R.id.etRetypePassSign);
        ivShowPassSign = findViewById(R.id.ivShowPassSign);
        ivShowRetypePassSign = findViewById(R.id.ivShowRetypePassSign);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin_inSignUp = findViewById(R.id.tvLogin_inSignUp);
        progressBarSign = findViewById(R.id.progressBarSign);
        ivCalendarIcon = findViewById(R.id.ivCalendarIcon);


        //password
        ivShowPassSign.setOnClickListener(v -> showPasswordToast(etPasswordSign, "Password"));
        //re-type password
        ivShowRetypePassSign.setOnClickListener(v -> showPasswordToast(etRetypePassSign, "Re-type Password"));

        //date picker
        ivCalendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(signup.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String format = "dd-MMM-yyyy";
                        SimpleDateFormat simple_format = new SimpleDateFormat(format, Locale.US);
                        etBirthDateSign.setText(simple_format.format(calendar.getTime()));

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        tvLogin_inSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Sign Up - Login clicked");
                Intent loginIntent = new Intent(signup.this, login.class);
                startActivity(loginIntent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBarSign.setVisibility(View.VISIBLE);
                btnRegister.setEnabled(false);

                new Thread(() -> {
                    boolean isValid = validateInputs();

                    handler.post(() -> {
                        progressBarSign.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);

                        if (isValid) {
                            createFirebaseUSer();
                        } else {
                            Toast.makeText(signup.this, "Registration failed. Please fix Inputs.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();

            }
        });
    }//onCreate


    //password visibility
    private void showPasswordToast(EditText passwordField, String label) {
        String password = passwordField.getText().toString().trim();
        if (!password.isEmpty()) {
            Toast.makeText(signup.this, label + ": " + password, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(signup.this, "Please enter password first", Toast.LENGTH_SHORT).show();
        }
    }

    private String[] getInputValues() {
        String fullName = etFullNameSign.getText().toString().trim();
        String email = etEmailSign.getText().toString().trim();
        String birthDate = etBirthDateSign.getText().toString().trim();
        String phone = etPhoneSign.getText().toString().trim();
        String password = etPasswordSign.getText().toString().trim();
        String retypePass = etRetypePassSign.getText().toString().trim();

        return new String[]{fullName, email, birthDate, phone, password, retypePass};
    }

    private boolean validateInputs() {

        String[] inputs = getInputValues();
        String fullName = inputs[0];
        String email = inputs[1];
        String birthDate = inputs[2];
        String phone = inputs[3];
        String password = inputs[4];
        String retypePass = inputs[5];

        List<String> errors = new ArrayList<>();

        if (fullName.isEmpty() || email.isEmpty() || birthDate.isEmpty() || phone.isEmpty() ||
                password.isEmpty() || retypePass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            System.out.println("SignUp = validation failed: Empty fields");
            return false;
        }

        //name
        if (fullName.length() < 4) {
            errors.add("Name must be at least 4 characters long!");
        }

        // Email
        if (!isValidEmailAddress(email)) {
            errors.add("Invalid email address!");
        }

        //DOB
        String birthError = dob_Validation(birthDate);
        if (birthError != null) {//null for valid
            errors.add(birthError);
            System.out.println("Validation failed: " + birthError);
        }

        System.out.println("Check 1");
        // Phone
        if (!phoneValidation(phone)) {
            errors.add("Phone number must be BD format: 01XXXXXXXXX");
        }

        // Password
        if (password.length() < 5) {
            errors.add("Password must be at least 5 characters");
        }

        // Retype password check
        if(!password.equals(retypePass)){
            errors.add("Password do not match!!");
            System.out.println("Validation failed: Password mismatch");
        }

        if (!errors.isEmpty()) {
//            System.out.println("Check 2");
            showValidationErrors(errors);
            return false;
        }
        return true;
    }//validation

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private String dob_Validation(String birthDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        sdf.setLenient(false); // strict parsing

        try {
            Date dob = sdf.parse(birthDate);
            Date today = new Date();

            // Future date check
            if (dob.after(today)) return "Birth date cannot be in the future";

            // Age > 100 check
            Calendar calLimit = Calendar.getInstance();
            calLimit.add(Calendar.YEAR, -100); // 100 years ago
            if (dob.before(calLimit.getTime()))
                return "Age cannot be more than 100 years";

        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid birth date format! Use DD-MMM-YYYY";
        }

        return null; // valid
    }

    private boolean phoneValidation(String phn){
        // taking digits on;y
        String n = phn.replaceAll("[^0-9]", "");
        String r = "^01[0-9]{9}$"; // BD number
        return n.matches(r);
    }

    private void showValidationErrors(List<String> errors) {
        System.out.println("Check 3");
        StringBuilder errorMessage = new StringBuilder();

        for (String error : errors) {
            errorMessage.append(error).append("\n");
//            System.out.println("Check 4");
//            System.out.println(error);
        }
        Toast.makeText(signup.this, ""+errorMessage.toString(), Toast.LENGTH_LONG).show();
//        System.out.println("Check 5");
    }

    private void createFirebaseUSer(){
//        System.out.println("IN FIrebase creation");
        String[] inputs = getInputValues();
        String fullName = inputs[0];
        String email = inputs[1];
        String birthDate = inputs[2];
        String phone = inputs[3];
        String password = inputs[4];
//        System.out.println(inputs);
//        System.out.println(birthDate);
//        System.out.println(phone);

        //  progress bar . disable register button
        progressBarSign.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

//        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();
                        // saving user data
                        saveUserData(userId, fullName, email, birthDate, phone);
                    } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        // the email is already in use
                        System.out.println("Email in use");
                        handler.post(() -> {
                            progressBarSign.setVisibility(View.GONE);
                            btnRegister.setEnabled(true);
                            Toast.makeText(signup.this, "Email already in use", Toast.LENGTH_LONG).show();
                        });
                    }else {
                        // failing!!!!!
                        handler.post(() -> {//  UI thread
                            progressBarSign.setVisibility(View.GONE);
                            btnRegister.setEnabled(true);
                            Toast.makeText(signup.this, "Account creation failed", Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }//createfirbase

    private void saveUserData(String userId, String fullName, String email, String birthDate, String phone){
        System.out.println("SignupDebug Saving to Firebase: " + fullName + ", " + email + ", " + birthDate + ", " + phone);
        User user = new User(userId, fullName, email, birthDate, phone);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(userId).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {//runs on main by firebase sdk
                        progressBarSign.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(signup.this, "User Registration successful", Toast.LENGTH_SHORT).show();
                            Intent i_homeSign = new Intent(signup.this, Home.class);
                            startActivity(i_homeSign);
                            finish();
                        } else {
                            Toast.makeText(signup.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                            System.out.println("Failed User in Firebase" + task.getException().getMessage());
                        }
                    }
                });
    }


}//signup