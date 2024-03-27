package com.cst438.domain;

import jakarta.persistence.*;
import java.util.List;
@Entity
public class Course {
    @Id
    @Column(name="course_id")
    private String courseId;
    private String title;
    private int credits;

    @OneToMany(mappedBy="course")
    private List<Section> sections;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getCredits() {
        return credits;
    }
    public void setCredits(int credits) {
        this.credits = credits;
    }
    public String getCourseId() {
        return courseId;
    }
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public List<Section> getSection() {
        return sections;
    }
}
