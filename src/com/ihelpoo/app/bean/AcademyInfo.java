package com.ihelpoo.app.bean;

/**
 * @author sy
 */
public class AcademyInfo {
    public String toString(){
        return academyName;
    }
    private Integer id;
    private String academyName;
    private Integer schoolId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAcademyName() {
        return academyName;
    }

    public void setAcademyName(String academyName) {
        this.academyName = academyName;
    }

    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }
}
