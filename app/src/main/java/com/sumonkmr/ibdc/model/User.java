package com.sumonkmr.ibdc.model;

public class User {
    String FName,LName,UID,Email,BloodGroup,Mobile,State,District, Upazila,Village,Step, Visible,RequestBlood, bloodImg_url,lastDonateDate;

    public User(String FName, String LName, String UID, String email, String bloodGroup, String mobile, String state, String district, String upazila, String village, String step, String visible, String requestBlood, String bloodImg_url, String lastDonateDate) {
        this.FName = FName;
        this.LName = LName;
        this.UID = UID;
        Email = email;
        BloodGroup = bloodGroup;
        Mobile = mobile;
        State = state;
        District = district;
        Upazila = upazila;
        Village = village;
        Step = step;
        Visible = visible;
        RequestBlood = requestBlood;
        this.bloodImg_url = bloodImg_url;
        this.lastDonateDate = lastDonateDate;
    }

    public User() {
    }

    public String getFName() {
        return FName;
    }

    public String getLName() {
        return LName;
    }

    public String getUID() {
        return UID;
    }

    public String getEmail() {
        return Email;
    }

    public String getBloodGroup() {
        return BloodGroup;
    }

    public String getMobile() {
        return Mobile;
    }

    public String getState() {
        return State;
    }

    public String getDistrict() {
        return District;
    }

    public String getUpazila() {
        return Upazila;
    }

    public String getVillage() {
        return Village;
    }

    public String getStep() {
        return Step;
    }

    public String getVisible() {
        return Visible;
    }

    public String getRequestBlood() {
        return RequestBlood;
    }

    public String getBloodImg_url() {
        return bloodImg_url;
    }

    public String getLastDonateDate() {
        return lastDonateDate;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public void setLName(String LName) {
        this.LName = LName;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setBloodGroup(String bloodGroup) {
        BloodGroup = bloodGroup;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public void setState(String state) {
        State = state;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public void setUpazila(String upazila) {
        Upazila = upazila;
    }

    public void setVillage(String village) {
        Village = village;
    }

    public void setStep(String step) {
        Step = step;
    }

    public void setVisible(String visible) {
        Visible = visible;
    }

    public void setRequestBlood(String requestBlood) {
        RequestBlood = requestBlood;
    }

    public void setBloodImg_url(String bloodImg_url) {
        this.bloodImg_url = bloodImg_url;
    }

    public void setLastDonateDate(String lastDonateDate) {
        this.lastDonateDate = lastDonateDate;
    }
}
