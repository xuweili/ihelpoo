package com.ihelpoo.app.bean;

/**
 * @author sy
 */
public class SchoolInfo {
    public String toString(){
        return school;
    }

    private String school;
    private String initial;

    public int id;

    public int city_op;

    public String domain;

    public String domain_main;

    public int time;

    public int status;

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCity_op() {
        return city_op;
    }

    public void setCity_op(int city_op) {
        this.city_op = city_op;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain_main() {
        return domain_main;
    }

    public void setDomain_main(String domain_main) {
        this.domain_main = domain_main;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
