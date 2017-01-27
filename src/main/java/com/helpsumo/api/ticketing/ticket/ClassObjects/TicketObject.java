package com.helpsumo.api.ticketing.ticket.ClassObjects;

public class TicketObject {

    public String status;
    public String name;
    public String message;
    public String date;
    public String staffname;
    public String id;
    public String faqcategoryid;
    public String faqcategoryname;

    public TicketObject() {

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

    public String getTicketId() {
        return id;
    }

}
