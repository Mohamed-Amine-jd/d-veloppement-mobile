package com.iset.tp7.entities;

import java.io.Serializable;

public class Course implements Serializable {
    private int id;
    private String name;
    private String description;
    private byte[] pdf; // Changed from String to byte[] to handle BLOB data
    private String type;
    private String teacherName;
    private String phone;

    // Constructor
    public Course(int id, String name, String description, byte[] pdf, String type, String teacherName,String phone) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.pdf = pdf;
        this.type = type;
        this.teacherName = teacherName;
        this.phone = phone;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public byte[] getPdf() { return pdf; }
    public String getType() { return type; }
    public String getTeacherName() { return teacherName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPdf(byte[] pdf) { this.pdf = pdf; }
    public void setType(String type) { this.type = type; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
}
