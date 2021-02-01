package com.example.androidphpmysql;

class Comment {
private int id;
private int patientid;
private String commentmade;
    private String timestamp;


    public Comment(int id, int patientid, String commentmade, String timestamp) {
        this.id = id;
        this.patientid = patientid;
        this.commentmade = commentmade;
        this. timestamp = timestamp;
    }

    public int getPatientid() {
        return patientid;
    }

    public String getCommentmade() {
        return commentmade;
    }
    public String getTimestamp() {
        return timestamp;
    }

    public int getId() {
        return id;
    }
}
