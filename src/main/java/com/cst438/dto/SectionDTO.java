package com.cst438.dto;


/*
 * Data Transfer Object for data for a section of a course
 */
public record SectionDTO(
        int secNo,
        int year,
        String semester,
        String courseId,
        int secId,
        String building,
        String room,
        String times,
        String courseTitle,
        String instructorName,
        String instructorEmail

       ) {
}
