package com.example.curasync_docapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DoctorSignupActivity extends AppCompatActivity {

    private EditText etFullNameDoc, etEmailDoc, etBirthDateDoc, etPhoneDoc;
    private EditText etBmdcNo, etYearsExperience, etHospital, etFee, etAvailableDays, etAvailableTime;
    private Spinner spSpecialization;

    private EditText etPasswordDoc, etRetypePasswordDoc;
    private ImageView ivShowPasswordDoc, ivShowRetypePasswordDoc, ivCalendarIconDoc;
    private CheckBox cbAgree;
    private ImageView ivPhotoDocProfile, ivPhotoLicense;

    private Button btnRegisterDoctor;
    private TextView tvLogin_FromDoc;

    private List<String> selectedDays = new ArrayList<>();
    private Map<String, String> selectedTimeSlots = new HashMap<>();
    private FirebaseAuth auth;
    Handler handler = new Handler(Looper.getMainLooper());



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();
        setupSpecializationSpinner();
        clickListener();
    }

    private void bindViews() {
        // Basic
        etFullNameDoc = findViewById(R.id.etFullNameDoc);
        etEmailDoc = findViewById(R.id.etEmailDoc);
        etBirthDateDoc = findViewById(R.id.etBirthDateDoc);
        etPhoneDoc = findViewById(R.id.etPhoneDoc);

        // Professional
        etBmdcNo = findViewById(R.id.etBmdcNo);
        spSpecialization = findViewById(R.id.spSpecialization);
        etYearsExperience = findViewById(R.id.etYearsExperience);
        etHospital = findViewById(R.id.etHospital);
        etFee = findViewById(R.id.etFee);
        etAvailableDays = findViewById(R.id.etAvailableDays);
        etAvailableTime = findViewById(R.id.etAvailableTime);
        ivPhotoDocProfile = findViewById(R.id.ivPhotoDocProfile);
        ivPhotoLicense = findViewById(R.id.ivPhotoLicense);

        // Passwords / Terms
        etPasswordDoc = findViewById(R.id.etPasswordDoc);
        ivShowPasswordDoc = findViewById(R.id.ivShowPasswordDoc);
        etRetypePasswordDoc = findViewById(R.id.etRetypePasswordDoc);
        ivShowRetypePasswordDoc = findViewById(R.id.ivShowRetypePasswordDoc);
        ivCalendarIconDoc = findViewById(R.id.ivCalendarIconDoc);
        cbAgree = findViewById(R.id.cbAgree);

        // Actions
        btnRegisterDoctor = findViewById(R.id.btnRegisterDoctor);
        tvLogin_FromDoc = findViewById(R.id.tvLogin_FromDoc);
    }

//     Populate specialization spinner
    private void setupSpecializationSpinner() {
        String[] options = {"Cardiology", "Neurology", "Dental", "Medicine", "Pediatrics", "General"};
        spSpecialization.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options)
        );
    }

    /** all interaction handlers */
    private void clickListener() {

        etBirthDateDoc.setOnClickListener(v -> openDobPicker());

        etAvailableDays.setOnClickListener(v -> showDaysSelectionDialog());
        etAvailableTime.setOnClickListener(v -> showTimeSlotsSelectionDialog());


        ivShowPasswordDoc.setOnClickListener(v -> showPasswordToast(etPasswordDoc, "Password"));
        ivShowRetypePasswordDoc.setOnClickListener(v -> showPasswordToast(etRetypePasswordDoc, "Re-type Password"));


        ivPhotoDocProfile.setOnClickListener(v ->
                Toast.makeText(this, "Profile photo upload coming soon", Toast.LENGTH_SHORT).show());
        ivPhotoLicense.setOnClickListener(v ->
                Toast.makeText(this, "License upload coming soon", Toast.LENGTH_SHORT).show());

        tvLogin_FromDoc.setOnClickListener(v -> {
            startActivity(new Intent(DoctorSignupActivity.this, login.class));
            finish();
        });

        btnRegisterDoctor.setOnClickListener(v -> {
            if (validateInputs()) {
                createFirebaseUser();



            } else {
                Toast.makeText(DoctorSignupActivity.this, "Doctor Registration failed. Please fix Inputs.", Toast.LENGTH_SHORT).show();
                System.out.println("staying on doc reg page");
            }
        });
    }

    private void openDobPicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (DatePicker view, int y, int m, int d) -> {
                    Calendar picked = Calendar.getInstance();
                    picked.set(y, m, d);
                    etBirthDateDoc.setText(new SimpleDateFormat("dd-MMM-yyyy", Locale.US)
                            .format(picked.getTime()));
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // future DOBs not allowed
        dialog.show();
    }
    private boolean validateInputs() {
        String fullName = etFullNameDoc.getText().toString().trim();
        String email = etEmailDoc.getText().toString().trim();
        String birthDate = etBirthDateDoc.getText().toString().trim();
        String phone = etPhoneDoc.getText().toString().trim();

        String bmdc = etBmdcNo.getText().toString().trim();
        String yearsStr = etYearsExperience.getText().toString().trim();
        String hospital = etHospital.getText().toString().trim();
        String feeStr = etFee.getText().toString().trim();
        String days = etAvailableDays.getText().toString().trim();
        String time = etAvailableTime.getText().toString().trim();

        String pass = etPasswordDoc.getText().toString().trim();
        String rePass = etRetypePasswordDoc.getText().toString().trim();

        List<String> errors = new ArrayList<>();

        // Required
        if (fullName.isEmpty() || email.isEmpty() || birthDate.isEmpty() || phone.isEmpty()
                || bmdc.isEmpty() || yearsStr.isEmpty() || hospital.isEmpty() || feeStr.isEmpty()
                || days.isEmpty() || time.isEmpty() || pass.isEmpty() || rePass.isEmpty()) {
            errors.add("Please fill all fields");
        }

        // Name length
        if (!fullName.isEmpty() && fullName.length() < 4) errors.add("Name must be at least 4 characters");

        // Email format
        if (!email.isEmpty() && !isValidEmailAddress(email)) errors.add("Invalid email address");

        // DOB validity
        String birthErr = dobValidation(birthDate);
        if (birthErr != null) errors.add(birthErr);

        // Phone (BD format 01XXXXXXXXX)
        if (!phone.isEmpty() && !phoneValidation(phone)) errors.add("Phone must be BD format: 01XXXXXXXXX");

        // BMDC basic pattern
        if (!bmdc.matches("^[A-Za-z0-9-]{4,}$")) errors.add("Invalid BMDC Registration Number");

        // Years of experience (0..60)
        try {
            int years = Integer.parseInt(yearsStr);
            if (years < 0 || years > 60) errors.add("Years of experience must be between 0 and 60");
        } catch (Exception e) { errors.add("Invalid years of experience"); }

        // Fee > 0
        try {
            double fee = Double.parseDouble(feeStr);
            if (fee <= 0) errors.add("Consultation fee must be greater than 0");
        } catch (Exception e) { errors.add("Invalid consultation fee"); }

        // Password rules
        if (pass.length() < 5) errors.add("Password must be at least 5 characters");
        if (!pass.equals(rePass)) errors.add("Passwords do not match");

        // Terms checkbox
        if (!cbAgree.isChecked()) errors.add("Please agree to the Terms & Privacy Policy");

        // Show errors if any
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String e : errors) sb.append("• ").append(e).append("\n");
            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
            return false;
        }
        return true; // all good
    }
    private void showPasswordToast(EditText field, String label) {
        String value = field.getText().toString().trim();
        if (value.isEmpty()) {
            Toast.makeText(this, "Please enter password first", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, label + ": " + value, Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isValidEmailAddress(String email) {
        String ePattern =
                "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        return java.util.regex.Pattern.compile(ePattern).matcher(email).matches();
    }
    private boolean phoneValidation(String phn) {
        String n = phn.replaceAll("[^0-9]", "");
        return n.matches("^01[0-9]{9}$");  /** BD phone format: 01XXXXXXXXX*/
    }
    private String dobValidation(String birthDate) {
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

    private void showDaysSelectionDialog() {
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        boolean[] checked = new boolean[days.length];

        for (int i = 0; i < days.length; i++) {
            checked[i] = selectedDays.contains(days[i]);
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Available Days")
                .setMultiChoiceItems(days, checked, (dialog, i, isChecked) -> {
                    if (isChecked) selectedDays.add(days[i]);
                    else selectedDays.remove(days[i]);
                })
                .setPositiveButton("OK", (dialog, which) ->
                        etAvailableDays.setText(String.join(", ", selectedDays))
                )
                .show();
    }

    private void showTimeSlotsSelectionDialog() {
        String[] labels = {"Morning (9:00-10:00)", "Afternoon (12:00-13:00)", "Evening (16:00-18:00)"};
        String[] keys = {"Morning", "Afternoon", "Evening"};
        String[] times = {"9:00-10:00", "12:00-13:00", "16:00-18:00"};

        boolean[] checked = new boolean[keys.length];
        for (int i = 0; i < keys.length; i++) {
            checked[i] = selectedTimeSlots.containsKey(keys[i]);
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Available Time Slots")
                .setMultiChoiceItems(labels, checked, (dialog, i, isChecked) -> {
                    if (isChecked) selectedTimeSlots.put(keys[i], times[i]);
                    else selectedTimeSlots.remove(keys[i]);
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    List<String> display = new ArrayList<>();
                    for (int i = 0; i < keys.length; i++) {
                        if (selectedTimeSlots.containsKey(keys[i])) {
                            display.add(labels[i]);
                        }
                    }
                    etAvailableTime.setText(String.join(", ", display));
                })
                .show();
    }
    // to get all values together
    private Doctor createDoctorObject() {
        Doctor doctor = new Doctor();
        doctor.setName(etFullNameDoc.getText().toString().trim());
        doctor.setEmail(etEmailDoc.getText().toString().trim());
        doctor.setBirthDate(etBirthDateDoc.getText().toString().trim());
        doctor.setPhoneDoc(etPhoneDoc.getText().toString().trim());
        doctor.setBmdcNo(etBmdcNo.getText().toString().trim());
        doctor.setSpeciality(spSpecialization.getSelectedItem().toString());
        doctor.setYearsExperience(Integer.parseInt(etYearsExperience.getText().toString().trim()));
        doctor.setHospital(etHospital.getText().toString().trim());
        doctor.setConsultationFee(Double.parseDouble(etFee.getText().toString().trim()));

        // Set the selected days and time slots
        doctor.setAvailableDays(new ArrayList<>(selectedDays));          // List<String>
        doctor.setAvailableSlots(new HashMap<>(selectedTimeSlots));

        return doctor;
    }

    private void createFirebaseUser(){
        Doctor doctor = createDoctorObject();
        String email = doctor.getEmail();
        String password = etPasswordDoc.getText().toString().trim();

        // disable register button
        btnRegisterDoctor.setEnabled(false);

        auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        System.out.println("Check inside task succes");
                        String userId = auth.getCurrentUser().getUid();
                        doctor.setDoctorId(userId);
                        System.out.println("saving");
                        saveDoctorData(doctor);
                    } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        System.out.println("Email in use");
                        handler.post(() -> {
                            btnRegisterDoctor.setEnabled(true);
                            Toast.makeText(DoctorSignupActivity.this, "Doctor Email already in use", Toast.LENGTH_LONG).show();
                        });
                    }else {
                        System.out.println("failing");
                        handler.post(() -> {//  UI thread
                            btnRegisterDoctor.setEnabled(true);
                            Toast.makeText(DoctorSignupActivity.this, "Account creation failed", Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }//createfirbase

    private void saveDoctorData(Doctor doctor){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Doctors");
        String doctorId = reference.push().getKey();

        if (doctorId == null) {
            Toast.makeText(this, "Error creating Doc id", Toast.LENGTH_SHORT).show();
            return;
        }

        reference.child(doctor.getDoctorId()).setValue(doctor)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {//runs on main by firebase sdk
                        btnRegisterDoctor.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(DoctorSignupActivity.this, "Doctor Registration successful", Toast.LENGTH_SHORT).show();
                            Intent i_DoctorList = new Intent(DoctorSignupActivity.this, DoctorListShow.class);
                            startActivity(i_DoctorList);
                            finish();
                        } else {
                            Toast.makeText(DoctorSignupActivity.this, "Failed to save doctor data", Toast.LENGTH_SHORT).show();
                            System.out.println("Failed Doctor in Firebase" + task.getException().getMessage());
                        }
                    }
                });
    }


}