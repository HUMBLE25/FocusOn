package com.example.focuson;

public class Task {
    // 여기서 모든 것을 불러올 필요가 있을까?
    private int id;
    private String title;
    private int importance;
    private int obligation;
    private String deadline;
    private int desire;
    private boolean isChecked;
    private String whenChecked;
    private int priority; // 우선순위를 나타내는 정수값

    public Task(int id, String title, int importance, int obligation, String deadline, int desire, boolean isChecked, String whenChecked, int priority) {
        this.id = id;
        this.title = title;
        this.importance = importance;
        this.obligation = obligation;
        this.deadline = deadline;
        this.desire = desire;
        this.isChecked = isChecked;
        this.whenChecked = whenChecked;
        this.priority = priority;
    }

    // Getters and setters for each field
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getImportance() {
        return importance;
    }

    public int getObligation() {
        return obligation;
    }

    public String getDeadline() {
        return deadline;
    }

    public int getDesire() {
        return desire;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public String getWhenChecked() {
        return whenChecked;
    }
    public int getPriority() {
        return priority;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setWhenChecked(String whenChecked) {
        this.whenChecked = whenChecked;
    }
    public void setPriority(int priority){ this.priority = priority;}
}
