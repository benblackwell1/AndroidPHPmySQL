package com.example.androidphpmysql;

class Screening {

    private int id;
    private int appointmentid;
    private String screeningdate;
    private String status;


    public Screening(int id, int appointmentid, String screeningdate, String status) {
        this.id = id;
        this.appointmentid = appointmentid;
        this.screeningdate = screeningdate;
        this.status = status;
    }
    public int getId() {
        return id;
    }

    public int getAppointmentid() {
        return appointmentid;
    }

    public String getScreeningdate() {
        return screeningdate;
    }

    public String getStatus() {
        return status;
    }

}
