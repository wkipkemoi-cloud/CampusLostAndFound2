package com.example.campuslostandfound;

public class Item {
    public long id;
    public String status;
    public String name;
    public String description;
    public String location;
    public String date;
    public String contact;
    public String category;
    public boolean claimed = false;

    public Item(String status, String name, String description, String location, String date, String contact, String category) {
        this.status = status;
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
        this.contact = contact;
        this.category = category;
    }

    public Item(long id, String status, String name, String description, String location, String date, String contact, String category) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
        this.contact = contact;
        this.category = category;
    }
}
