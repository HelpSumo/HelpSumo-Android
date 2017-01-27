package com.helpsumo.api.ticketing.ticket.ClassObjects;

public class TypeDropDown {
    public String typeid;
    public String typename;

    public TypeDropDown() {

    }

    public TypeDropDown(String typeid, String typename) {
        this.typeid = typeid;
        this.typename = typename;
    }

    public void setId(String typeid) {
        this.typeid = typeid;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getId() {
        return typeid;
    }

    public String getName() {
        return typename;
    }

}
