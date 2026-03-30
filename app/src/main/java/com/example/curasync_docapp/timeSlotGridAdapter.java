package com.example.curasync_docapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class timeSlotGridAdapter extends BaseAdapter {
    private final Context context;
    private final List<Map.Entry<String, String>> items = new ArrayList<>();

    // Fixed order for display
    private static final List<String> ORDER = Arrays.asList("Morning", "Afternoon", "Evening");

    public timeSlotGridAdapter(Context context, Map<String, String> slots) {
        this.context = context;

        // Add in fixed order if present
        for (String key : ORDER) {
            if (slots.containsKey(key)) {
                items.add(new AbstractMap.SimpleEntry<>(key, slots.get(key)));
            }
        }
        // Add any unexpected keys after (if they exist)
        for (Map.Entry<String, String> e : slots.entrySet()) {
            if (!ORDER.contains(e.getKey())) items.add(e);
        }
    }

    @Override public int getCount() { return items.size(); }
    @Override public Object getItem(int position) { return items.get(position); }
    @Override public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.time_grid, parent, false);
        }

        TextView tvSlotName = convertView.findViewById(R.id.tvSlotName);
        TextView tvSlotTime = convertView.findViewById(R.id.tvSlotTime);

        Map.Entry<String,String> slot = items.get(position);
        System.out.println("ADAPTER -> Setting slot: " + slot.getKey() + " " + slot.getValue());
        tvSlotName.setText(slot.getKey());
        tvSlotTime.setText(slot.getValue());

        return convertView;
    }
}
