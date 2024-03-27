package com.cst438.dto;

import java.sql.Date;
/*
 * Data Transfer Object for assignment data including student's grade
 */
public record AssignmentStudentDTO(
        int assignmentId,
        String title,
        Date dueDate,
        String courseId,
        String courseTitle,
        int sectionId,
        Integer score
) {
}
