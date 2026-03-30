package com.example.curasync_docapp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Doctor {
    private String doctorId;
    private String name;
    private String email;
    private String birthDate;
    private String phoneDoc; // Professional phone
    private String bmdcNo;
    private String speciality;
    private int yearsExperience;
    private String hospital;
    private double consultationFee;
    private List<String> availableDays;
//    private String availableTime;
    private String profileImageUrl;
    private String licenseImageUrl;

    // Available slots as a map structure
    private Map<String, String> availableSlots;

    public Doctor() {
        // Initialize the availableSlots map
        availableSlots = new HashMap<>();
    }

    // Constructor with parameters
    public Doctor(String doctorId, String name, String email, String birthDate,
                  String phoneDoc, String bmdcNo, String speciality, int yearsExperience,
                  String hospital, double consultationFee, List<String> availableDays, String availableTime,
                  String profileImageUrl, String licenseImageUrl) {
        this.doctorId = doctorId;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.phoneDoc = phoneDoc;
        this.bmdcNo = bmdcNo;
        this.speciality = speciality;
        this.yearsExperience = yearsExperience;
        this.hospital = hospital;
        this.consultationFee = consultationFee;
        this.availableDays = availableDays;
//        this.availableTime = availableTime;
        this.profileImageUrl = profileImageUrl;
        this.licenseImageUrl = licenseImageUrl;
        this.availableSlots = new HashMap<>();
    }

    // Getters and Setters
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getPhoneDoc() { return phoneDoc; }
    public void setPhoneDoc(String phoneDoc) { this.phoneDoc = phoneDoc; }

    public String getBmdcNo() { return bmdcNo; }
    public void setBmdcNo(String bmdcNo) { this.bmdcNo = bmdcNo; }

    public String getSpeciality() { return speciality; }
    public void setSpeciality(String speciality) { this.speciality = speciality; }

    public int getYearsExperience() { return yearsExperience; }
    public void setYearsExperience(int yearsExperience) { this.yearsExperience = yearsExperience; }

    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public List<String> getAvailableDays() { return availableDays; }
    public void setAvailableDays(List<String> availableDays) { this.availableDays = availableDays; }


//    public String getAvailableTime() { return availableTime; }
//    public void setAvailableTime(String availableTime) { this.availableTime = availableTime; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getLicenseImageUrl() { return licenseImageUrl; }
    public void setLicenseImageUrl(String licenseImageUrl) { this.licenseImageUrl = licenseImageUrl; }

    public Map<String, String> getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(Map<String, String> availableSlots) { this.availableSlots = availableSlots; }

    // Helper method to set time slots
    public void setTimeSlot(String period, String timeRange) {
        if (availableSlots == null) {
            availableSlots = new HashMap<>();
        }
        availableSlots.put(period, timeRange);
    }

    // Helper method to get a specific time slot
    public String getTimeSlot(String period) {
        return availableSlots != null ? availableSlots.get(period) : null;
    }
}
