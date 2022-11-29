package com.sumonkmr.ibdc.model;

public class User {
    String FName,LName, uid,Email,BloodGroup,Mobile,Division,District, Upazila,Village,Img_url,birthdate,lastDonateDate;

    public User() {
    } //default constructor

    public User(String FName, String LName, String uid, String email, String bloodGroup, String mobile, String division, String district, String upazila, String village, String img_url, String birthdate, String lastDonateDate) {
        this.FName = FName;
        this.LName = LName;
        this.uid = uid;
        Email = email;
        BloodGroup = bloodGroup;
        Mobile = mobile;
        Division = division;
        District = district;
        Upazila = upazila;
        Village = village;
        Img_url = img_url;
        this.birthdate = birthdate;
        this.lastDonateDate = lastDonateDate;
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public String getLName() {
        return LName;
    }

    public void setLName(String LName) {
        this.LName = LName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBloodGroup() {
        return BloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        BloodGroup = bloodGroup;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getDivision() {
        return Division;
    }

    public void setDivision(String division) {
        Division = division;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getUpazila() {
        return Upazila;
    }

    public void setUpazila(String upazila) {
        Upazila = upazila;
    }

    public String getVillage() {
        return Village;
    }

    public void setVillage(String village) {
        Village = village;
    }

    public String getImg_url() {
        return Img_url;
    }

    public void setImg_url(String img_url) {
        Img_url = img_url;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getLastDonateDate() {
        return lastDonateDate;
    }

    public void setLastDonateDate(String lastDonateDate) {
        this.lastDonateDate = lastDonateDate;
    }
}
