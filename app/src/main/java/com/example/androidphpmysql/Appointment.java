package com.example.androidphpmysql;

class Appointment {
    private int id;
    private int patientid;
    private String appdate, appdesc;

    public Appointment(int id, int patientid, String appdate, String appdesc){
        this.id = id;
        this.patientid = patientid;
        this.appdate = appdate;
        this.appdesc = appdesc;
    }
    public int getId() {
        return id;
    }

    public int getPatientid() {
        return patientid;
    }

    public String getAppdate() {
        return appdate;
    }

    public String getAppdesc() {
        return appdesc;
    }
}
