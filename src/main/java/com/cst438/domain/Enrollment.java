package com.cst438.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Enrollment {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="enrollment_id")
    int enrollmentId;

    // add additional attribute for grade
    private String grade;

    // create relationship between enrollment and user entities
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    // create relationship between enrollment and section entities
    @ManyToOne
    @JoinColumn(name="section_no", nullable=false)
    private Section section;

    @OneToMany(mappedBy="enrollment")
    private List<Grade> grades;

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public List<Grade> getGrades() {
      return grades;
    }

    public void setGrades(List<Grade> grades) {
      this.grades = grades;
    }
}
