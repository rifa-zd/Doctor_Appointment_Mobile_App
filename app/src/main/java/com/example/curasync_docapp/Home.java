package com.example.curasync_docapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    private Button btnFindDoc;
    private TextView tvGuestMessage, tvwelcomeTextHome, tvCardiology, tvNeurology, tvDental, tvMedicine, tvPediatrics, tvGeneral;
    private TextView tvSeeMore, tvBookAppointHome, tvViewAppointHome, tvContactHome, tvDocListHome, tvAboutUsHome;
    private ListView doctorsList;
    private ArrayList<Doctor> doctorList;
    private doctorAdapter doctorAdapter;
    Handler handler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sp = getSharedPreferences("GUEST_USER", MODE_PRIVATE);
        boolean isGuest = sp.getBoolean("isGuest", false);
        tvGuestMessage = findViewById(R.id.tvGuestMessage);


        btnFindDoc = findViewById(R.id.btnFindDoc);
        tvwelcomeTextHome = findViewById(R.id.tvwelcomeTextHome);

        tvCardiology = findViewById(R.id.tvCardiology);
        tvNeurology = findViewById(R.id.tvNeurology);
        tvDental = findViewById(R.id.tvDental);
        tvMedicine = findViewById(R.id.tvMedicine);
        tvPediatrics = findViewById(R.id.tvPediatrics);
        tvGeneral = findViewById(R.id.tvGeneral);

        tvSeeMore = findViewById(R.id.tvSeeMore);

        tvBookAppointHome = findViewById(R.id.tvBookAppointHome);
        tvViewAppointHome = findViewById(R.id.tvViewAppointHome);
        tvContactHome = findViewById(R.id.tvContactHome);
        tvDocListHome = findViewById(R.id.tvDocListHome);
        tvAboutUsHome = findViewById(R.id.tvAboutUsHome);
        doctorsList = findViewById(R.id.doctorsList);
        doctorList = new ArrayList<>();
        doctorAdapter = new doctorAdapter(this, doctorList);
        doctorsList.setAdapter(doctorAdapter);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String fullName = task.getResult().child("fullName").getValue(String.class);
                    if (fullName != null) {
                        tvwelcomeTextHome.setText("Welcome, " + fullName);
                        tvwelcomeTextHome.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvwelcomeTextHome.setText("Welcome, User");
                }
            });
        }


        if (isGuest) {
            Toast.makeText(this, "Guest mode active", Toast.LENGTH_SHORT).show();
            tvGuestMessage.setVisibility(View.VISIBLE);


            View.OnClickListener guestBlocker = v -> {
                Toast.makeText(Home.this, "Guest users need to register first", Toast.LENGTH_LONG).show();

                handler.postDelayed(() -> {
                    // Clear guest mode
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isGuest", false);
                    editor.apply();

                    Intent intent = new Intent(Home.this, signup.class);
                    startActivity(intent);
                }, 2500);
            };

            tvSeeMore.setOnClickListener(guestBlocker);
            tvBookAppointHome.setOnClickListener(guestBlocker);
            tvViewAppointHome.setOnClickListener(guestBlocker);
            tvGuestMessage.setOnClickListener(guestBlocker);
        }else {
            tvSeeMore.setOnClickListener(v -> navigateToDoctorList("All"));

            loadTwoDoctors();

            tvBookAppointHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Home.this, "Choose a Doctor First", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Home.this, DoctorListShow.class);
                startActivity(intent);
                }
            });

            tvViewAppointHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFutureUpdateToast("Viewing Appoinments");
//                Intent intent = new Intent(Home.this, ViewAppointment.class);
//                startActivity(intent);
                }
            });
        }

        tvContactHome.setOnClickListener(v -> showFutureUpdateToast("Contact Support"));

        tvDocListHome.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, DoctorListShow.class);
            startActivity(intent);
        });


        tvAboutUsHome.setOnClickListener(v -> showFutureUpdateToast("About Us"));

        btnFindDoc.setOnClickListener(v -> showFutureUpdateToast("Finding Doctors"));

        tvCardiology.setOnClickListener(v -> navigateToDoctorList("Cardiology"));

        tvNeurology.setOnClickListener(v -> navigateToDoctorList("Neurology"));

        tvDental.setOnClickListener(v -> navigateToDoctorList("Dental"));

        tvMedicine.setOnClickListener(v -> navigateToDoctorList("Medicine"));

        tvPediatrics.setOnClickListener(v -> navigateToDoctorList("Pediatrics"));

        tvGeneral.setOnClickListener(v -> navigateToDoctorList("General"));

    }//create

    private void showFutureUpdateToast(String pageName) {
        Toast.makeText(Home.this, pageName + " will be incorporated in the next update", Toast.LENGTH_SHORT).show();
    }

    private void navigateToDoctorList(String specialty) {
          Intent intent = new Intent(Home.this, DoctorListShow.class);
            startActivity(intent);
    }

    private void loadTwoDoctors() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Doctors");
        ref.limitToFirst(2).addListenerForSingleValueEvent(new ValueEventListener() {
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
                Toast.makeText(Home.this, "Failed to load doctors", Toast.LENGTH_SHORT).show();
            }
        });
    }
}