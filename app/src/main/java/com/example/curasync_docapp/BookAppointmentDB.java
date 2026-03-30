package com.example.curasync_docapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookAppointmentDB extends SQLiteOpenHelper {
    public BookAppointmentDB(Context context) {
        super(context, "appointments.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("Database Creation");

        String sql = "CREATE TABLE appointments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_email TEXT NOT NULL, " +
                "doctor_name TEXT NOT NULL, " +
                "specialization TEXT NOT NULL, " +
                "patient_name TEXT NOT NULL, " +
                "age INTEGER NOT NULL, " +
                "blood_group TEXT NOT NULL, " +
                "sex TEXT NOT NULL, " +
                "selected_date TEXT NOT NULL, " +
                "selected_time TEXT NOT NULL, " +
                "consultation_type TEXT NOT NULL, " +
                "problem_description TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS appointments");
        onCreate(db);
    }

    public boolean insertAppointment(String userEmail, String doctorName, String specialization,
                                     String patientName, int age, String bloodGroup, String sex,
                                     String selectedDate, String selectedTime, String consultationType,
                                     String problemDescription) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("user_email", userEmail);
        values.put("doctor_name", doctorName);
        values.put("specialization", specialization);
        values.put("patient_name", patientName);
        values.put("age", age);
        values.put("blood_group", bloodGroup);
        values.put("sex", sex);
        values.put("selected_date", selectedDate);
        values.put("selected_time", selectedTime);
        values.put("problem_description", problemDescription);
        values.put("consultation_type", consultationType);

        long result = db.insert("appointments", null, values);
        db.close();
        return result != -1;
    }

    public Cursor getAppointmentsByEmail(String userEmail) { //for firebae
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM appointments WHERE user_email = ?", new String[]{userEmail});
    }

}
