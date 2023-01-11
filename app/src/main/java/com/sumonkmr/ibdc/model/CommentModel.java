package com.sumonkmr.ibdc.model;

public class CommentModel {

    String userName,usermsg,userImg,uid,time,date, editTime,editDate,cmtEdit;

    public CommentModel() {
    }

    public CommentModel(String userName, String usermsg, String userImg, String uid, String time, String date, String editTime, String editDate, String cmtEdit) {
        this.userName = userName;
        this.usermsg = usermsg;
        this.userImg = userImg;
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.editTime = editTime;
        this.editDate = editDate;
        this.cmtEdit = cmtEdit;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUsermsg() {
        return usermsg;
    }

    public void setUsermsg(String usermsg) {
        this.usermsg = usermsg;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEditTime() {
        return editTime;
    }

    public void setEditTime(String editTime) {
        this.editTime = editTime;
    }

    public String getEditDate() {
        return editDate;
    }

    public void setEditDate(String editDate) {
        this.editDate = editDate;
    }

    public String getCmtEdit() {
        return cmtEdit;
    }

    public void setCmtEdit(String cmtEdit) {
        this.cmtEdit = cmtEdit;
    }
}


