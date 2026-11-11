package com.example.campuslostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnReportLost = findViewById(R.id.btn_report_lost);
        Button btnReportFound = findViewById(R.id.btn_report_found);
        Button btnViewItems = findViewById(R.id.btn_view_items);

        btnReportLost.setOnClickListener(v -> {
            Intent i = new Intent(this, ReportActivity.class);
            i.putExtra("status", "Lost");
            startActivity(i);
        });

        btnReportFound.setOnClickListener(v -> {
            Intent i = new Intent(this, ReportActivity.class);
            i.putExtra("status", "Found");
            startActivity(i);
        });

        btnViewItems.setOnClickListener(v -> {
            startActivity(new Intent(this, ItemsActivity.class));
        });
    }
}