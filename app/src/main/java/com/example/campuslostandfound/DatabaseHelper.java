package com.example.campuslostandfound;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "campus_lost_found.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_CATEGORY = "category";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_STATUS + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_CONTACT + " TEXT, " +
                COLUMN_CATEGORY + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    public boolean addItem(String status, String name, String description, String location, String date, String contact, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_LOCATION, location);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_PHONE, contact);
        cv.put(COLUMN_CONTACT, contact);
        cv.put(COLUMN_CATEGORY, category);
        long result = db.insert(TABLE_ITEMS, null, cv);
        return result != -1;
    }

    public ArrayList<Item> getAllItems() {
        ArrayList<Item> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                    String desc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                    int phoneIdx = cursor.getColumnIndex(COLUMN_PHONE);
                    String contact = (phoneIdx != -1) ? cursor.getString(phoneIdx) : "";
                    if (contact == null || contact.isEmpty()) {
                        int contactIdx = cursor.getColumnIndex(COLUMN_CONTACT);
                        contact = (contactIdx != -1) ? cursor.getString(contactIdx) : "";
                    }
                    int catIdx = cursor.getColumnIndex(COLUMN_CATEGORY);
                    String category = (catIdx != -1) ? cursor.getString(catIdx) : "";

                    list.add(new Item(id, status, name, desc, location, date, contact, category));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    public Cursor getFilteredItems(String searchQuery, String statusFilter, String categoryFilter) {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder query = new StringBuilder("SELECT * FROM " + TABLE_ITEMS + " WHERE 1=1");
        ArrayList<String> args = new ArrayList<>();

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            query.append(" AND (").append(COLUMN_NAME).append(" LIKE ? OR ").append(COLUMN_DESCRIPTION).append(" LIKE ?)");
            args.add("%" + searchQuery.trim() + "%");
            args.add("%" + searchQuery.trim() + "%");
        }

        if (statusFilter != null && !statusFilter.equalsIgnoreCase("All Statuses") && !statusFilter.equalsIgnoreCase("All") && !statusFilter.isEmpty()) {
            query.append(" AND ").append(COLUMN_STATUS).append(" = ?");
            args.add(statusFilter);
        }

        if (categoryFilter != null && !categoryFilter.equalsIgnoreCase("All Categories") && !categoryFilter.equalsIgnoreCase("All") && !categoryFilter.isEmpty()) {
            query.append(" AND ").append(COLUMN_CATEGORY).append(" = ?");
            args.add(categoryFilter);
        }

        return db.rawQuery(query.toString(), args.toArray(new String[0]));
    }

    public boolean updateItemStatus(long id, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, newStatus);
        int rows = db.update(TABLE_ITEMS, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }
}
