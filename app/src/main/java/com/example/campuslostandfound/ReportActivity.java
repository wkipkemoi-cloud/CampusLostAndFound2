package com.example.campuslostandfound;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ReportActivity extends AppCompatActivity {

    private EditText etName, etDesc, etLocation, etDate, etContact;
    private Spinner spCategory;
    private String status = "Lost";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        dbHelper = new DatabaseHelper(this);

        String passedStatus = getIntent().getStringExtra("status");
        if (passedStatus != null && !passedStatus.isEmpty()) {
            status = passedStatus;
        }

        TextView tvTitle = findViewById(R.id.tv_report_title);
        TextView btnBack = findViewById(R.id.btn_back);
        TextView tvStatusBadge = findViewById(R.id.tv_status_badge);
        etName = findViewById(R.id.et_item_name);
        spCategory = findViewById(R.id.sp_category);
        etContact = findViewById(R.id.et_phone_number);
        etDesc = findViewById(R.id.et_description);
        etDate = findViewById(R.id.et_date);
        etLocation = findViewById(R.id.et_location);
        Button btnSubmit = findViewById(R.id.btn_submit);

        if ("Found".equalsIgnoreCase(status)) {
            status = "Found";
            if (tvTitle != null) {
                tvTitle.setText("Report Found Item");
            }
            if (tvStatusBadge != null) {
                tvStatusBadge.setText("REPORTING FOUND ITEM");
                tvStatusBadge.setBackgroundResource(R.drawable.bg_button_found);
            }
            if (btnSubmit != null) {
                btnSubmit.setBackgroundResource(R.drawable.bg_button_found);
            }
        } else {
            status = "Lost";
            if (tvTitle != null) {
                tvTitle.setText("Report Lost Item");
            }
            if (tvStatusBadge != null) {
                tvStatusBadge.setText("REPORTING LOST ITEM");
                tvStatusBadge.setBackgroundResource(R.drawable.bg_button_lost);
            }
            if (btnSubmit != null) {
                btnSubmit.setBackgroundResource(R.drawable.bg_button_lost);
            }
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spCategory != null) {
            spCategory.setAdapter(adapter);
        }

        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                String name = etName != null ? etName.getText().toString().trim() : "";
                String desc = etDesc != null ? etDesc.getText().toString().trim() : "";
                String location = etLocation != null ? etLocation.getText().toString().trim() : "";
                String date = etDate != null ? etDate.getText().toString().trim() : "";
                String contact = etContact != null ? etContact.getText().toString().trim() : "";
                String category = spCategory != null && spCategory.getSelectedItem() != null ? spCategory.getSelectedItem().toString() : "Other";

                if (name.isEmpty()) {
                    Toast.makeText(this, "Please enter item name", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbHelper.addItem(status, name, desc, location, date, contact, category);
                Toast.makeText(this, status + " item reported successfully!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }
}
