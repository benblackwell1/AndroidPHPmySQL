package com.example.androidphpmysql;

//model class for patients
class Patient {
    private int id;
    private int userid;
    private String fname, lname, street, city, zip, phone, prsi;

    public Patient(int id, int userid, String fname, String lname, String street, String city, String zip, String phone, String prsi){
        this.id = id;
        this.userid = userid;
        this.fname = fname;
        this.lname = lname;
        this.street = street;
        this.city = city;
        this.zip = zip;
        this.phone = phone;
        this.prsi = prsi;
    }

    public int getId() {
        return id;
    }

    public int getUserid() {
        return userid;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getZip() {
        return zip;
    }

    public String getPhone() {
        return phone;
    }

    public String getPrsi() {
        return prsi;
    }
}
