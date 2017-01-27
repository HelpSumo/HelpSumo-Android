package com.helpsumo.api.ticketing.ticket.ClassObjects;

public class DepartmentDropDown {
    public String dptid;
    public String dptname;

    public DepartmentDropDown() {

    }

    public void setDptId(String dpttid) {
        this.dptid = dpttid;
    }

    public void setDptname(String dpttname) {
        this.dptname = dpttname;
    }

    public String getDptId() {
        return dptid;
    }

    public String getDptname() {
        return dptname;
    }
}
