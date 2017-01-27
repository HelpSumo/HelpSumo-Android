package com.helpsumo.api.ticketing.ticket.ClassObjects;

import java.util.ArrayList;

public class Comment {

    public String mName, mNam, mNa;
    public String status;
    public String name;
    public String message;
    public String date;
    public String staffname;
    public String id;
    public String email;
    public String attach;
    public ArrayList<String> children = new ArrayList<String>();

    public Comment(String name) {
        mName = name;
    }

    public Comment() {

    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStaffname(String staffname) {
        this.staffname = staffname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public void setTicketId(String ticketid) {
        this.id = ticketid;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getStaffname() {
        return staffname;
    }

    public String getEmail() {
        return email;
    }

    public String getAttach() {
        return attach;
    }

    public String getTicketId() {
        return id;
    }

}