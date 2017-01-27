package com.helpsumo.api.ticketing.ticket.ClassObjects;

public class Articledetails {

    public String artid, artdescrip;
    public String arthead;
    public String artdate;
    public String artrating;
    public String artcomment;
    public String artstatus;

    public Articledetails() {

    }

    public void setArtid(String artid) {
        this.artid = artid;
    }

    public void setArthead(String arthead) {
        this.arthead = arthead;
    }

    public void setArtdate(String artdate) {
        this.artdate = artdate;
    }

    public void setArtrating(String artrating) {
        this.artrating = artrating;
    }

    public void setArtcomment(String artcomment) {
        this.artcomment = artcomment;
    }

    public void setArtdescrip(String artdescrip) {
        this.artdescrip = artdescrip;
    }

    public void setArtstatus(String artstatus) {
        this.artstatus = artstatus;
    }

    public String getArtid() {
        return artid;
    }

    public String getArthead() {
        return arthead;
    }

    public String getArtdate() {
        return artdate;
    }

    public String getArtrating() {
        return artrating;
    }

    public String getArtcomment() {
        return artcomment;
    }

    public String getArtdescrip() {
        return artdescrip;
    }

    public String getArtstatus() {
        return artstatus;
    }
}
