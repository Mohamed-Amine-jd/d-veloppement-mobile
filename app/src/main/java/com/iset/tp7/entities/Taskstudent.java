package com.iset.tp7.entities;

public class Taskstudent {
    private int id;
    private String name;
    private long dueDate;
    private boolean isCompleted;
    private int priority;

    // Constructor
    public Taskstudent(String name, long dueDate, boolean isCompleted, int priority) {
        this.name = name;
        this.dueDate = dueDate;
        this.isCompleted = isCompleted;
        this.priority = priority;
    }

    // Empty constructor
    public Taskstudent() {}

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}