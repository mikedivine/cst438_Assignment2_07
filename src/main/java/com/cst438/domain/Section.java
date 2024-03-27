package com.cst438.domain;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.List;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="section_no")
    private int sectionNo;  // unique id assigned by database.  Used to enroll into a section.

    @ManyToOne
    @JoinColumn(name="course_id", nullable=false)
    private Course course;

    @ManyToOne
    @JoinColumn(name="term_id", nullable=false)
    private Term term;

    @Column(name="sec_id")
    private int secId;   // sequential numbering of sections of a course in a term:  1, 2, 3, ....

    private String building;
    private String room;
    private String times;
    @Column(name="instructor_email")
    private String instructorEmail;

    @OneToMany(mappedBy="section")
    List<Enrollment> enrollments;

    @OneToMany(mappedBy="section")
    List<Assignment> assignments;

    public int getSectionNo() {
        return sectionNo;
    }

    public void setSectionNo(int sectionNo) {
        this.sectionNo = sectionNo;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public int getSecId() {
        return secId;
    }

    public void setSecId(int secId) {
        this.secId = secId;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getInstructorEmail() {
        return instructorEmail;
    }

    public void setInstructor_email(String instructorEmail) {
        this.instructorEmail = instructorEmail;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public List<Assignment> getAssignments() { return assignments; }
}
