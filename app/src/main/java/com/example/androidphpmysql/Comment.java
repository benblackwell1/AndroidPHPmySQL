package com.example.androidphpmysql;

class Comment {
private int id;
private int patientid;
private String comment;


    public Comment(int id, int patientid, String comment) {
        this.id = id;
    }

    public int getPatientid() {
        return patientid;
    }

    public String getComment() {
        return comment;
    }
    public int getId() {
        return id;
    }
}
