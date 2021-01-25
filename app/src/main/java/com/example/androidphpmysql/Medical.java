package com.example.androidphpmysql;

import java.util.Date;

class Medical {
    private int id;
    private int patientid;
    private String gender, age, weight, alcohol, smoking, underlyingcondition, allergy, medication;
    private Date timestamp;

    public Medical(int id, int patientid, String gender, String age, String weight, String alcohol, String smoking, String underlyingcondition, String allergy, String medication, Date timestamp){
        this.id = id;
        this.patientid = patientid;
        this.gender = gender;
        this.age = age;
        this.weight = weight;
        this.alcohol = alcohol;
        this.smoking = smoking;
        this.underlyingcondition = underlyingcondition;
        this.allergy = allergy;
        this.timestamp = timestamp;

    }

    public int getId() {
        return id;
    }

    public int getPatientid() {
        return patientid;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getWeight() {
        return weight;
    }

    public String getAlcohol() {
        return alcohol;
    }

    public String getSmoking() {
        return smoking;
    }

    public String getUnderlyingcondition() {
        return underlyingcondition;
    }

    public String getAllergy() {
        return allergy;
    }

    public String getMedication() {
        return medication;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
