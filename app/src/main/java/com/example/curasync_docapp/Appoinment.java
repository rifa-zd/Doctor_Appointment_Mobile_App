package com.example.curasync_docapp;
public class Appoinment {

    public String doctorName;
    public String specialization;
    public String patientName;
    public int age;
    public String sex;
    public String selectedDate;
    public String selectedTime;
    public String consultationType;
    public String problemDescription;

    public Appoinment(String doctorName, String specialization, String patientName, int age, String sex,
                              String selectedDate, String selectedTime, String consultationType, String problemDescription) {
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.patientName = patientName;
        this.age = age;
        this.sex = sex;
        this.selectedDate = selectedDate;
        this.selectedTime = selectedTime;
        this.consultationType = consultationType;
        this.problemDescription = problemDescription;
    }
}