package com.example.curasync_docapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorListShow extends AppCompatActivity {

    private ListView lvDoctorList;
    private ArrayList<Doctor> doctorList;
    private doctorAdapter doctorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_list_show);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lvDoctorList = findViewById(R.id.lvDoctorList);
        doctorList = new ArrayList<>();
        doctorAdapter = new doctorAdapter(this, doctorList);
        lvDoctorList.setAdapter(doctorAdapter);

        loadDoctorsFromFirebase();

    }//create

    private void loadDoctorsFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Doctors");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctorList.clear();
                for (DataSnapshot docSnap : snapshot.getChildren()) {
                    Doctor doctor = docSnap.getValue(Doctor.class);
                    if (doctor != null) {
                        doctor.setDoctorId(docSnap.getKey());
                        doctorList.add(doctor);
                    }
                }
                doctorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorListShow.this, "Failed to load doctors", Toast.LENGTH_SHORT).show();
            }
        });
    }
}