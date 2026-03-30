package com.example.curasync_docapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class bookAppoinment extends AppCompatActivity {

    private ImageView ivBack, ivNotification, ivDoctorPic;
    private TextView tvDoctorName, tvSpecialization, tvMonth, tvSlotAvailability;
    private GridView gvTimeSlots;
    private EditText etFullName, etAge, etBloodGroup, etSex, etProblem;
    private RadioGroup rgPackages;
    private RadioButton rbVideo, rbAudio, rbChat, rbVisit;

    private Button btnSetAppointment;
    Handler handler = new Handler(Looper.getMainLooper());
    private BookAppointmentDB dbHelper;

    private String selectedTimeSlot = "";
    private String selectedTimeSlotKey = "";
//    private timeSlotGridAdapter timeSlotAdapter;
//    private int selectedSlotPosition = -1;
//    private TextView selectedDateView = null;
    private String selectedDate = null;
//    private Doctor currentDoctor;

    private LinearLayout llTimeSlotsContainer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_appoinment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        new Thread(() -> {
            dbHelper = new BookAppointmentDB(getApplicationContext());
            dbHelper.getWritableDatabase();
        }).start();

        // Get intent values
        String doctorId = getIntent().getStringExtra("DOCTOR_ID");
        String doctorName = getIntent().getStringExtra("DOCTOR_NAME");
        String doctorSpeciality = getIntent().getStringExtra("DOCTOR_SPECIALITY");

        if (doctorId == null) {
            Toast.makeText(this, "No doctor id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDoctorName.setText(doctorName);
        tvSpecialization.setText(doctorSpeciality);

        fetchAvailableSlots(doctorId);

        btnSetAppointment.setOnClickListener(v -> saveAppointment(doctorName, doctorSpeciality));
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.ivBack);
        ivNotification = findViewById(R.id.ivNotification);
        ivDoctorPic = findViewById(R.id.ivDoctor);
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvSpecialization = findViewById(R.id.tvSpecialization);
        tvMonth = findViewById(R.id.tvMonth);
//        gvTimeSlots = findViewById(R.id.gvTimeSlots);
        llTimeSlotsContainer = findViewById(R.id.timeSlotContainer);

        etFullName = findViewById(R.id.etFullName);
        etAge = findViewById(R.id.etAge);
        etBloodGroup = findViewById(R.id.etBloodGroup);
        etSex = findViewById(R.id.etSex);
        etProblem = findViewById(R.id.etProblem);
        rgPackages = findViewById(R.id.rgPackages);
        rbVideo = findViewById(R.id.rbVideo);
        rbAudio = findViewById(R.id.rbAudio);
        rbChat = findViewById(R.id.rbChat);
        rbVisit = findViewById(R.id.rbVisit);
        btnSetAppointment = findViewById(R.id.btnSetAppointment);
        tvSlotAvailability = findViewById(R.id.tvSlotAvailability);
//        tvSlotAvailability.setText("⏰ Doctor's not available today");

        ivBack.setOnClickListener(v -> {
            startActivity(new Intent(bookAppoinment.this, Home.class));
            finish();
        });

//        Map<String, String> slots = new HashMap<>();
//        slots.put("Morning", "08:00 AM - 12:00 PM");
//        slots.put("Afternoon", "01:00 PM - 04:00 PM");
//        gvTimeSlots.setAdapter(new timeSlotGridAdapter(this, slots));
    }

    private void fetchAvailableSlots(String doctorId) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Doctors")
                .child(doctorId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    handler.post(() ->
                            Toast.makeText(bookAppoinment.this, "Doctor not found", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                //  in background
                new Thread(() -> {
                    List<String> availableDays = new ArrayList<>();
                    Map<String, String> timeSlots = new HashMap<>();

                    for (DataSnapshot daySnap : snapshot.child("availableDays").getChildren()) {
                        String day = daySnap.getValue(String.class);
                        if (day != null) availableDays.add(day);
                    }

                    for (DataSnapshot slotSnap : snapshot.child("availableSlots").getChildren()) {
                        timeSlots.put(slotSnap.getKey(), slotSnap.getValue(String.class));
                    }

                    // main thread for UI update
                    handler.post(() -> setupCalendar(availableDays, timeSlots));
                }).start();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                handler.post(() ->
                        Toast.makeText(bookAppoinment.this, "Failed to load slots", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }


    private void setupCalendar(List<String> availableDays, Map<String, String> timeSlots) {
        new Thread(() -> {
            Calendar calendar = Calendar.getInstance();
            String monthName = new SimpleDateFormat("MMMM").format(calendar.getTime());

            List<String> weekdays = new ArrayList<>();
            List<String> days = new ArrayList<>();
            List<Boolean> enabledFlags = new ArrayList<>();

            for (int i = 0; i < 7; i++) {
                Date date = calendar.getTime();
                String weekday = new SimpleDateFormat("EEEE").format(date);
                String day = new SimpleDateFormat("d").format(date);

                weekdays.add(weekday);
                days.add(day);
                enabledFlags.add(availableDays.contains(weekday));

                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            handler.post(() -> {
                tvMonth.setText(monthName);

                TableLayout tableLayout = findViewById(R.id.tableLayout);
                TableRow weekdaysRow = (TableRow) tableLayout.getChildAt(0);
                TableRow datesRow = (TableRow) tableLayout.getChildAt(1);

//                System.out.println("DEBUG -> weekdaysRow child count: " + weekdaysRow.getChildCount());
//        System.out.println("DEBUG -> datesRow child count: " + datesRow.getChildCount());
                for (int i = 0; i < 7; i++) {
                    String weekday = weekdays.get(i);
                    String day = days.get(i);
                    boolean isEnabled = enabledFlags.get(i);

                    ((TextView) weekdaysRow.getChildAt(i)).setText(weekday);
                    TextView dateView = (TextView) datesRow.getChildAt(i);
                    dateView.setText(day);

                    if (isEnabled) {
                        dateView.setTextColor(Color.BLACK);
                        dateView.setOnClickListener(v -> {
                            try {
                                for (int j = 0; j < datesRow.getChildCount(); j++) {
                                    datesRow.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                                }
                                v.setBackgroundResource(R.drawable.orange_gradient);
                                selectedDate = day;
                                showTimeSlots(timeSlots);
                            } catch (Exception e) {
                                Log.e("CalendarError", "Click error", e);
                            }
                        });
                    } else {
                        dateView.setTextColor(Color.GRAY);
                        dateView.setOnClickListener(null);  // Disable click
                    }
                }
            });
        }).start();
    }

    private void showTimeSlots(Map<String, String> timeSlots) {
        handler.post(() -> {
            llTimeSlotsContainer.removeAllViews(); // Clear previous

            if (timeSlots == null || timeSlots.isEmpty()) {
                tvSlotAvailability.setText("No slots available");
                return;
            }

            tvSlotAvailability.setText("Available slots");

            final AtomicReference<View> selectedView = new AtomicReference<>(null);
            final int highlightColor = Color.CYAN;

            for (Map.Entry<String, String> entry : timeSlots.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                TextView slotView = new TextView(this);
                slotView.setText(key + ": " + value);
                slotView.setTextSize(16);
                slotView.setPadding(30, 20, 30, 20);
                slotView.setBackgroundResource(R.drawable.rounded_outline_button);
                slotView.setTextColor(Color.BLACK);
                slotView.setClickable(true);
                slotView.setFocusable(true);

                slotView.setOnClickListener(v -> {
                    View previous = selectedView.get();
                    if (previous != null) {
                        previous.setBackgroundResource(R.drawable.rounded_outline_button);
                    }
                    v.setBackgroundColor(highlightColor);
                    selectedView.set(v);

                    selectedTimeSlot = value;
                    selectedTimeSlotKey = key;

                    Log.d("TimeSlotSelect", "Selected: " + key + " -> " + value);
                });
                Toast.makeText(this, "Showing " + timeSlots.size() + " slots", Toast.LENGTH_SHORT).show();
                Log.d("ShowSlots", "Slots count: " + timeSlots.size());


                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 8, 0, 8);

                llTimeSlotsContainer.addView(slotView, params);
            }
        });
    }



    private void saveAppointment(String doctorName, String doctorSpeciality) {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String patientName = etFullName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String bloodGroup = etBloodGroup.getText().toString().trim();
        String sex = etSex.getText().toString().trim();
        String problem = etProblem.getText().toString().trim();

        final String consultationType;
        if (rbVideo.isChecked()) consultationType = "Video";
        else if (rbAudio.isChecked()) consultationType = "Audio";
        else if (rbChat.isChecked()) consultationType = "Chat";
        else if (rbVisit.isChecked()) consultationType = "Visit";
        else consultationType = ""; // Default case

        // Simple validation - only check empty
        if (patientName.isEmpty() || age.isEmpty() || bloodGroup.isEmpty() ||
                sex.isEmpty() || problem.isEmpty() || selectedDate == null ||
                selectedTimeSlot.isEmpty() || consultationType.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                boolean isInserted = dbHelper.insertAppointment(
                        userEmail, doctorName, doctorSpeciality,
                        patientName, Integer.parseInt(age), bloodGroup, sex, selectedDate,
                        selectedTimeSlot, consultationType, problem
                );

                handler.post(() -> {
                    if (isInserted) {
                        Toast.makeText(this, "Appointment booked!", Toast.LENGTH_SHORT).show();

                        // Log saved appointment info
                        System.out.println("Appointment Saved:\n"
                                + "User Email: " + userEmail + "\n"
                                + "Doctor: " + doctorName + " (" + doctorSpeciality + ")\n"
                                + "Patient: " + patientName + ", Age: " + age + ", Blood Group: " + bloodGroup + ", Sex: " + sex + "\n"
                                + "Date: " + selectedDate + ", Time: " + selectedTimeSlot + "\n"
                                + "Consultation Type: " + consultationType + "\n"
                                + "Problem: " + problem);

                        Intent intent = new Intent(bookAppoinment.this, Home.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to save appointment", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                handler.post(() ->
                        Toast.makeText(this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
        System.out.println("User data has been saved successfully.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
