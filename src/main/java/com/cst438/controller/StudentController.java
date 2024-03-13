package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.CourseDTO;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    TermRepository termRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseRepository courseRepository;

    // student gets transcript showing list of all enrollments
    // studentId will be temporary until Login security is implemented
    //example URL  /transcript?studentId=19803
    @GetMapping("/transcripts")
    public List<EnrollmentDTO> getTranscript(
      @RequestParam("studentId") int studentId) {

        User user = userRepository.findById(studentId).orElse(null);
        // Verify user exists and is a student
        studentExists(user);

        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByStudentIdOrderByTermId(studentId);
        List<EnrollmentDTO> dto_list = new ArrayList<>();
        for (Enrollment e : enrollments) {
          Section section = e.getSection();
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
              term.getSemester()
            )
          );
        }
        return dto_list;
    }

    // student gets a list of their enrollments for the given year, semester
    // user must be student
    // studentId will be temporary until Login security is implemented
    @GetMapping("/enrollments")
    public List<EnrollmentDTO> getSchedule(
      @RequestParam("year") int year,
      @RequestParam("semester") String semester,
      @RequestParam("studentId") int studentId) {

        User user = userRepository.findById(studentId).orElse(null);
        // Verify user exists and is a student
        studentExists(user);

        List<Enrollment> enrollments = enrollmentRepository.findByYearAndSemesterOrderByCourseId(year, semester, studentId);
        List<EnrollmentDTO> dto_list = new ArrayList<>();
        for (Enrollment e : enrollments) {
          Section section = e.getSection();
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
              term.getSemester()
            )
          );
        }
        return dto_list;
    }


    // student adds enrollment into a section
    // user must be student
    // return EnrollmentDTO with enrollmentId generated by database
    @PostMapping("/enrollments/sections/{sectionNo}")
    public EnrollmentDTO addCourse(
      @PathVariable int sectionNo,
      @RequestParam("studentId") int studentId ) {

        User user = userRepository.findById(studentId).orElse(null);
        // Verify user exists and is a student
        studentExists(user);

        Enrollment e = new Enrollment();
        e.setUser(user);

        Section section = sectionRepository.findById(sectionNo).orElse(null);
        // check that the Section entity with primary key sectionNo exists
        if (section == null) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found.");
        }
        e.setSection(section);

        // check that today is between addDate and addDeadline for the section
        Term term = section.getTerm();
        Date today = new Date();
        if (today.before(term.getAddDate())) {
          throw new ResponseStatusException(HttpStatus.CONFLICT,
            "You have attempted to add a course before the Add Date.");
        } else if (today.after(term.getAddDeadline())) {
          throw new ResponseStatusException(HttpStatus.CONFLICT,
            "You have attempted to add a course after the Add Deadline.");
        }

        // check that student is not already enrolled into this section
        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByStudentIdOrderByTermId(studentId);
        for (Enrollment enrollment: enrollments) {
          if (enrollment.getSection().getSectionNo() == sectionNo) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
              "You have attempted to add a course the student is already enrolled in.");
          }
        }

        Course course = section.getCourse();
        // create a new enrollment entity and save.  The enrollment grade will
        // be NULL until instructor enters final grades for the course.
        enrollmentRepository.save(e);

        return new EnrollmentDTO(e.getEnrollmentId(), e.getGrade(), user.getId(),
          user.getName(), user.getEmail(), course.getCourseId(), section.getSecId(),
          section.getSectionNo(), section.getBuilding(), section.getRoom(), section.getTimes(),
          course.getCredits(), term.getYear(), term.getSemester());
    }

    // student drops a course
    // user must be student
    @DeleteMapping("/enrollments/{enrollmentId}")
    public void dropCourse(
     @PathVariable("enrollmentId") int enrollmentId,
     @RequestBody UserDTO userDTO) {

        User user = new User();
        user.setId(userDTO.id());
        user.setType(userDTO.type());
        // Verify user exists and is a student
        studentExists(user);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElse(null);
        if (enrollment == null) {
         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment ID not found.");
        }

        Section section = enrollment.getSection();
        Term term = section.getTerm();
        Date today = new Date();

        // check that today is not after the dropDeadline for section
        if (today.before(term.getDropDeadline())) {
         enrollmentRepository.delete(enrollment);
        } else {
         throw new ResponseStatusException(HttpStatus.CONFLICT, "Course " + enrollmentId +
           " cannot be dropped after the Drop Deadline.");
        }
    }

    private void studentExists(User user) {
        // Verify user exists and is a student
        if (user == null) {
         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }
        if (!(user.getType().equals("STUDENT"))) {
         throw new ResponseStatusException(HttpStatus.CONFLICT,
           "User is not a student.");
        }
    }
}