package com.helpsumo.api.ticketing.ticket.ClassObjects;

import java.util.ArrayList;

public class Faqdetails {

    public String faq;
    public String faqid;
    public String status;
    public String faqanswer;
    public String faqcategoryid;
    public String faqcategoryname;
    private ArrayList<Faqdetails> Items;

    public Faqdetails() {

    }

    public void setFaq(String faq) {
        this.faq = faq;
    }

    public void setFaqid(String faqid) {
        this.faqid = faqid;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFaqanswer(String faqanswer) {
        this.faqanswer = faqanswer;
    }

    public void setFaqcategoryid(String faqid) {
        this.faqcategoryid = faqid;
    }

    public void setFaqcategoryname(String faqname) {
        this.faqcategoryname = faqname;
    }

    public String getFaqid() {
        return faqid;
    }

    public String getFaq() {
        return faq;
    }

    public String getFaqanswer() {
        return faqanswer;
    }

    public String getStatus() {
        return status;
    }

    public String getFaqcategoryid() {
        return faqcategoryid;
    }

    public String getFaqcategoryname() {
        return faqcategoryname;
    }

    public ArrayList<Faqdetails> getItems() {
        return Items;
    }

    public void setItems(ArrayList<Faqdetails> Items) {
        this.Items = Items;
    }

}
