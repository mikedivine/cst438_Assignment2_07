package com.cst438.dto;
/*
 * Data Transfer Object for student enrollment data for a section of a course
 */
public record EnrollmentDTO(
    int enrollmentId,
    String grade,  // final grade. May be null until instructor enters final grades.
    int studentId,
    String name,
    String email,
    String courseId,
    int sectionId,
    int sectionNo,
    String building,
    String room,
    String times,
    int credits,
    int year,
    String semester,
    String courseTitle
) {
}
