package com.itjobtracker.model;

import java.util.Date;

public class JobPosting {
    private int id;
    private String companyName;
    private String jobTitle;
    private String salary;
    private String requirements;
    private String location;
    private Date postDate;
    private String status;
    private String notes;
    private Date createdAt;
    private Date updatedAt;

    // 构造函数
    public JobPosting() {
    }

    public JobPosting(String companyName, String jobTitle, String salary, String requirements, 
                     String location, Date postDate, String status, String notes) {
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.requirements = requirements;
        this.location = location;
        this.postDate = postDate;
        this.status = status;
        this.notes = notes;
    }

    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "JobPosting{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", salary='" + salary + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                '}' + "\n";
    }
}