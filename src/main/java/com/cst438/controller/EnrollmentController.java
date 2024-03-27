package com.cst438.controller;


import com.cst438.domain.*;
import com.cst438.dto.EnrollmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class EnrollmentController {

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseController courseController;

    // instructor downloads student enrollments for a section, ordered by student name
    // user must be instructor for the section
    @GetMapping("/sections/{sectionNo}/enrollments")
    public List<EnrollmentDTO> getEnrollments(
      @PathVariable("sectionNo") int sectionNo,
      @RequestParam("instructorEmail") String instructorEmail
      ) {

        List<Enrollment> enrollments =
          enrollmentRepository.findEnrollmentsBySectionNoOrderByStudentName(sectionNo);

        if (enrollments == null) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No enrollments found.");
        }

        List<EnrollmentDTO> dto_list = new ArrayList<>();
        for (Enrollment e : enrollments) {
            User user = e.getUser();
            Section section = e.getSection();

            validateInstructor(instructorEmail, section.getInstructorEmail());
            Course course = section.getCourse();
            Term term = section.getTerm();

            dto_list.add(
              new EnrollmentDTO(
                e.getEnrollmentId(),
                e.getGrade(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                course.getCourseId(),
                section.getSecId(),
                section.getSectionNo(),
                section.getBuilding(),
                section.getRoom(),
                section.getTimes(),
                course.getCredits(),
                term.getYear(),
                term.getSemester(),
                course.getTitle()
              )
            );
        }
        return dto_list;
    }

    // instructor uploads enrollments with the final grades for the section
    // user must be instructor for the section
    @PutMapping("/enrollments")
    public void updateEnrollmentGrades(
      @RequestBody List<EnrollmentDTO> dlist,
      @RequestParam("instructorEmail") String instructorEmail
      ) {

        // For each EnrollmentDTO in the list
        //  find the Enrollment entity using enrollmentId
        //  update the grade and save back to database
        for (EnrollmentDTO e : dlist) {
            Enrollment enrollment = enrollmentRepository.findEnrollmentByEnrollmentId(e.enrollmentId());

            if (enrollment == null) {
              throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Enrollment Not found.");
            }

            Section s = enrollment.getSection();
            // Verify user exists and is an instructor and is the correct instructor
            validateInstructor(instructorEmail, s.getInstructorEmail());

            enrollment.setGrade(e.grade());
            enrollmentRepository.save(enrollment);
        }
    }

  private void validateInstructor(String email, String InstructorEmail) {
    // Verify user exists and is an instructor
    User user = userRepository.findByEmail(email);
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
        "User not found.");
    }
    if (!(user.getType().equals("INSTRUCTOR"))) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
        "You are not an Instructor.");
    }
    if (!(email.equals(InstructorEmail))) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
        "You are not the Instructor of the Section.");
    }
  }
}
