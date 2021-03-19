package com.example.androidphpmysql;

class WaitingRoom {
    private int id;
    private int userid;
    private String timestamp;

    public WaitingRoom(int id, int userid, String timestamp) {
        this.id = id;
        this.userid = userid;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getUserid() {
        return userid;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
