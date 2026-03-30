package com.example.curasync_docapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class customListAdapter extends ArrayAdapter<Appoinment> {

    public customListAdapter(Context context, List<Appoinment> appointments) {
        super(context, 0, appointments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Appoinment appointment = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.appointment_item, parent, false);
        }

        TextView tvDoctorInfoView = convertView.findViewById(R.id.tvDoctorInfoView);
        TextView tvPatientNameView = convertView.findViewById(R.id.tvPatientNameView);
        TextView tvDateTimeView = convertView.findViewById(R.id.tvDateTimeView);
        TextView tvConsultationType = convertView.findViewById(R.id.tvConsultationType);
        TextView tvProblemView = convertView.findViewById(R.id.tvProblemView);


        tvDoctorInfoView.setText(appointment.doctorName + " - " + appointment.specialization);
        tvPatientNameView.setText(appointment.patientName + " | " + appointment.age + " | " + appointment.sex);
        tvDateTimeView.setText(appointment.selectedDate + " | " + appointment.selectedTime);
        tvConsultationType.setText(appointment.consultationType);
        tvProblemView.setText("Problem: " + appointment.problemDescription);

        return convertView;
    }
}
