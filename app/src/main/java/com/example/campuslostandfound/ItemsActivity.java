package com.example.campuslostandfound;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ItemsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout llItemsContainer;
    private EditText etSearch;
    private Spinner spFilterCategory;
    private Button btnAllTab, btnLostTab, btnFoundTab;
    private String selectedTabFilter = "All"; // "All", "Lost", "Found"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        dbHelper = new DatabaseHelper(this);

        View btnBack = findViewById(R.id.btn_back);
        View btnNavReport = findViewById(R.id.btn_nav_report);
        llItemsContainer = findViewById(R.id.ll_items);
        etSearch = findViewById(R.id.et_search);
        spFilterCategory = findViewById(R.id.sp_filter_category);
        btnAllTab = findViewById(R.id.btn_all_tab);
        btnLostTab = findViewById(R.id.btn_lost_tab);
        btnFoundTab = findViewById(R.id.btn_found_tab);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnNavReport != null) {
            btnNavReport.setOnClickListener(v -> {
                Intent reportIntent = new Intent(ItemsActivity.this, ReportActivity.class);
                reportIntent.putExtra("status", "Lost");
                startActivity(reportIntent);
            });
        }

        setupCategorySpinner();

        if (btnAllTab != null) {
            btnAllTab.setOnClickListener(v -> {
                selectedTabFilter = "All";
                updateTabStyles();
                loadItems();
            });
        }

        if (btnLostTab != null) {
            btnLostTab.setOnClickListener(v -> {
                selectedTabFilter = "Lost";
                updateTabStyles();
                loadItems();
            });
        }

        if (btnFoundTab != null) {
            btnFoundTab.setOnClickListener(v -> {
                selectedTabFilter = "Found";
                updateTabStyles();
                loadItems();
            });
        }

        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    loadItems();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        updateTabStyles();
        loadItems();
    }

    private void setupCategorySpinner() {
        if (spFilterCategory == null) return;
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.filter_categories_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilterCategory.setAdapter(categoryAdapter);

        spFilterCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadItems();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateTabStyles() {
        if ("Lost".equalsIgnoreCase(selectedTabFilter)) {
            if (btnLostTab != null) {
                btnLostTab.setBackgroundResource(R.drawable.bg_button_lost);
                btnLostTab.setTextColor(Color.WHITE);
            }
            if (btnFoundTab != null) {
                btnFoundTab.setBackgroundResource(android.R.color.transparent);
                btnFoundTab.setTextColor(Color.parseColor("#64748B"));
            }
            if (btnAllTab != null) {
                btnAllTab.setBackgroundResource(android.R.color.transparent);
                btnAllTab.setTextColor(Color.parseColor("#64748B"));
            }
        } else if ("Found".equalsIgnoreCase(selectedTabFilter)) {
            if (btnFoundTab != null) {
                btnFoundTab.setBackgroundResource(R.drawable.bg_button_found);
                btnFoundTab.setTextColor(Color.WHITE);
            }
            if (btnLostTab != null) {
                btnLostTab.setBackgroundResource(android.R.color.transparent);
                btnLostTab.setTextColor(Color.parseColor("#64748B"));
            }
            if (btnAllTab != null) {
                btnAllTab.setBackgroundResource(android.R.color.transparent);
                btnAllTab.setTextColor(Color.parseColor("#64748B"));
            }
        } else {
            if (btnAllTab != null) {
                btnAllTab.setBackgroundResource(R.drawable.bg_button_view);
                btnAllTab.setTextColor(Color.WHITE);
            }
            if (btnLostTab != null) {
                btnLostTab.setBackgroundResource(android.R.color.transparent);
                btnLostTab.setTextColor(Color.parseColor("#64748B"));
            }
            if (btnFoundTab != null) {
                btnFoundTab.setBackgroundResource(android.R.color.transparent);
                btnFoundTab.setTextColor(Color.parseColor("#64748B"));
            }
        }
    }

    private void loadItems() {
        if (llItemsContainer == null) return;
        llItemsContainer.removeAllViews();

        String searchQuery = etSearch != null ? etSearch.getText().toString() : "";
        String categoryFilter = spFilterCategory != null && spFilterCategory.getSelectedItem() != null ?
                spFilterCategory.getSelectedItem().toString() : "All Categories";

        Cursor cursor = dbHelper.getFilteredItems(searchQuery, selectedTabFilter, categoryFilter);

        if (cursor != null && cursor.getCount() > 0) {
            LayoutInflater inflater = LayoutInflater.from(this);

            while (cursor.moveToNext()) {
                long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION));

                int phoneIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE);
                String phone = (phoneIdx != -1) ? cursor.getString(phoneIdx) : "";
                if (phone == null || phone.isEmpty()) {
                    int contactIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTACT);
                    phone = (contactIdx != -1) ? cursor.getString(contactIdx) : "";
                }

                int categoryIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY);
                String category = (categoryIndex != -1) ? cursor.getString(categoryIndex) : null;

                View cardView = inflater.inflate(R.layout.item_card, llItemsContainer, false);

                TextView tvBadge = cardView.findViewById(R.id.tv_item_badge);
                TextView tvName = cardView.findViewById(R.id.tv_item_name);
                TextView tvCategory = cardView.findViewById(R.id.tv_item_category);
                TextView tvDescription = cardView.findViewById(R.id.tv_item_description);
                TextView tvLocation = cardView.findViewById(R.id.tv_item_location);
                TextView tvDate = cardView.findViewById(R.id.tv_item_date);
                TextView tvPhone = cardView.findViewById(R.id.tv_item_phone);
                Button btnCallContact = cardView.findViewById(R.id.btn_call_contact);
                Button btnMarkClaimed = cardView.findViewById(R.id.btn_mark_claimed);
                Button btnMarkReturned = cardView.findViewById(R.id.btn_mark_returned);

                if ("Claimed".equalsIgnoreCase(status)) {
                    tvBadge.setText("CLAIMED");
                    tvBadge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                    tvBadge.setBackgroundResource(R.drawable.bg_button_claimed);
                    btnMarkClaimed.setText("Claimed ✓");
                    btnMarkClaimed.setBackgroundResource(R.drawable.bg_button_claimed);
                    btnMarkClaimed.setTextColor(Color.WHITE);
                    btnMarkReturned.setText("Mark Returned");
                    btnMarkReturned.setBackgroundResource(R.drawable.bg_button_action_outline);
                    btnMarkReturned.setTextColor(Color.parseColor("#1E3A8A"));
                } else if ("Returned".equalsIgnoreCase(status)) {
                    tvBadge.setText("RETURNED");
                    tvBadge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                    tvBadge.setBackgroundResource(R.drawable.bg_button_returned);
                    btnMarkReturned.setText("Returned ✓");
                    btnMarkReturned.setBackgroundResource(R.drawable.bg_button_returned);
                    btnMarkReturned.setTextColor(Color.WHITE);
                    btnMarkClaimed.setText("Mark Claimed");
                    btnMarkClaimed.setBackgroundResource(R.drawable.bg_button_action_outline);
                    btnMarkClaimed.setTextColor(Color.parseColor("#1E3A8A"));
                } else if ("Found".equalsIgnoreCase(status)) {
                    tvBadge.setText("FOUND");
                    tvBadge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report_found, 0, 0, 0);
                    tvBadge.setBackgroundResource(R.drawable.bg_button_found);
                    btnMarkClaimed.setText("Mark Claimed");
                    btnMarkClaimed.setBackgroundResource(R.drawable.bg_button_action_outline);
                    btnMarkClaimed.setTextColor(Color.parseColor("#1E3A8A"));
                    btnMarkReturned.setText("Mark Returned");
                    btnMarkReturned.setBackgroundResource(R.drawable.bg_button_action_outline);
                    btnMarkReturned.setTextColor(Color.parseColor("#1E3A8A"));
                } else {
                    tvBadge.setText("LOST");
                    tvBadge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report_lost, 0, 0, 0);
                    tvBadge.setBackgroundResource(R.drawable.bg_button_lost);
                    btnMarkClaimed.setText("Mark Claimed");
                    btnMarkClaimed.setBackgroundResource(R.drawable.bg_button_action_outline);
                    btnMarkClaimed.setTextColor(Color.parseColor("#1E3A8A"));
                    btnMarkReturned.setText("Mark Returned");
                    btnMarkReturned.setBackgroundResource(R.drawable.bg_button_action_outline);
                    btnMarkReturned.setTextColor(Color.parseColor("#1E3A8A"));
                }

                tvName.setText(name);

                if (category != null && !category.isEmpty()) {
                    tvCategory.setText(category);
                    tvCategory.setVisibility(View.VISIBLE);
                } else {
                    tvCategory.setVisibility(View.GONE);
                }

                if (desc != null && !desc.isEmpty()) {
                    tvDescription.setText("Description: " + desc);
                } else {
                    tvDescription.setText("Description: No details provided.");
                }

                if (location != null && !location.isEmpty()) {
                    tvLocation.setText("Location: " + location);
                } else {
                    tvLocation.setText("Location: Not specified");
                }

                if (date != null && !date.isEmpty()) {
                    tvDate.setText("Date: " + date);
                    tvDate.setVisibility(View.VISIBLE);
                } else {
                    tvDate.setVisibility(View.GONE);
                }

                if (phone != null && !phone.isEmpty()) {
                    tvPhone.setText("Contact: " + phone);
                    tvPhone.setVisibility(View.VISIBLE);
                    btnCallContact.setVisibility(View.VISIBLE);

                    final String contactPhone = phone;
                    btnCallContact.setOnClickListener(v -> {
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                        dialIntent.setData(Uri.parse("tel:" + contactPhone));
                        startActivity(dialIntent);
                    });
                } else {
                    tvPhone.setText("Contact: Not provided");
                    tvPhone.setVisibility(View.VISIBLE);
                    btnCallContact.setVisibility(View.GONE);
                }

                btnMarkClaimed.setOnClickListener(v -> {
                    boolean updated = dbHelper.updateItemStatus(itemId, "Claimed");
                    if (updated) {
                        Toast.makeText(ItemsActivity.this, "Item marked as Claimed!", Toast.LENGTH_SHORT).show();
                        loadItems();
                    } else {
                        Toast.makeText(ItemsActivity.this, "Failed to update item status.", Toast.LENGTH_SHORT).show();
                    }
                });

                btnMarkReturned.setOnClickListener(v -> {
                    boolean updated = dbHelper.updateItemStatus(itemId, "Returned");
                    if (updated) {
                        Toast.makeText(ItemsActivity.this, "Item marked as Returned!", Toast.LENGTH_SHORT).show();
                        loadItems();
                    } else {
                        Toast.makeText(ItemsActivity.this, "Failed to update item status.", Toast.LENGTH_SHORT).show();
                    }
                });

                llItemsContainer.addView(cardView);
            }
            cursor.close();
        } else {
            TextView emptyTv = new TextView(this);
            emptyTv.setText(R.string.no_items_found);
            emptyTv.setTextSize(16);
            emptyTv.setTextColor(Color.parseColor("#64748B"));
            emptyTv.setPadding(32, 64, 32, 32);
            llItemsContainer.addView(emptyTv);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }
}
