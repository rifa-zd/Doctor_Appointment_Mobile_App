package com.example.curasync_docapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ViewAppoinment extends AppCompatActivity {

    private ListView lvAppointmentList;
    BookAppointmentDB db;
    private FirebaseAuth mAuth;
    private TextView tvUserEmailView, tvUserPhoneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_appoinment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lvAppointmentList = findViewById(R.id.lvAppointmentList);
        tvUserEmailView = findViewById(R.id.tvUserEmailView);
        tvUserPhoneView = findViewById(R.id.tvUserPhoneView);
        mAuth = FirebaseAuth.getInstance();
        db = new BookAppointmentDB(this);

        load_displayAppointments();
    }

    private void load_displayAppointments() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) { //could be guest
            String userEmail = currentUser.getEmail();
            String phoneNumber = currentUser.getPhoneNumber();

            tvUserEmailView.setText(userEmail);
            tvUserPhoneView.setText(phoneNumber);

            Cursor cursor = db.getAppointmentsByEmail(userEmail);
            List<Appoinment> appointments = new ArrayList<>(); //model class

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String doctorName = cursor.getString(cursor.getColumnIndexOrThrow("doctor_name"));
                    String specialization = cursor.getString(cursor.getColumnIndexOrThrow("specialization"));
                    String patientName = cursor.getString(cursor.getColumnIndexOrThrow("patient_name"));
                    int age = cursor.getInt(cursor.getColumnIndexOrThrow("age"));
                    String sex = cursor.getString(cursor.getColumnIndexOrThrow("sex"));
                    String selectedDate = cursor.getString(cursor.getColumnIndexOrThrow("selected_date"));
                    String selectedTime = cursor.getString(cursor.getColumnIndexOrThrow("selected_time"));
                    String consultationType = cursor.getString(cursor.getColumnIndexOrThrow("consultation_type"));
                    String problemDescription = cursor.getString(cursor.getColumnIndexOrThrow("problem_description"));

                    Appoinment appointment = new Appoinment( //model class
                            doctorName, specialization, patientName, age, sex,
                            selectedDate, selectedTime, consultationType, problemDescription
                    );

                    appointments.add(appointment);
                }
                cursor.close();
            }//cursor null if
            // Set adapter
            customListAdapter adapter = new customListAdapter(this, appointments);
            lvAppointmentList.setAdapter(adapter);

        }//user null if
    }
}