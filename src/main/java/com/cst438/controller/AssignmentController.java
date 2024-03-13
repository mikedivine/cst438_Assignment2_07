package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.AssignmentDTO;
import com.cst438.dto.AssignmentStudentDTO;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.dto.GradeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AssignmentController {

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    SectionRepository sectionRepository;

    // instructor lists assignments for a section.  Assignments ordered by due date.
    // logged in user must be the instructor for the section
    @GetMapping("/sections/{secNo}/assignments")
    public List<AssignmentDTO> getAssignments(
      @PathVariable("secNo") int secNo,
      @RequestBody int instructorId
      ) {

        User user = userRepository.findById(instructorId).orElse(null);
        // Verify user exists and is an instructor
        instructorExists(user);
		
        // hint: use the assignment repository method
        //  findBySectionNoOrderByDueDate to return
        //  a list of assignments
        List<Assignment> assignments = assignmentRepository.findBySectionNoOrderByDueDate(secNo);
        List<AssignmentDTO> dto_list = new ArrayList<>();
        for(Assignment a : assignments) {
            Section section = a.getSection();
            Course course = section.getCourse();
            dto_list.add(
              new AssignmentDTO(
                a.getAssignmentId(),
                a.getTitle(),
                a.getDue_date().toString(),
                course.getCourseId(),
                section.getSecId(),
                section.getSectionNo()
                ));
        }
        return dto_list;
    }

    // add assignment
    // user must be instructor of the section
    // return AssignmentDTO with assignmentID generated by database
    @PostMapping("/assignments")
    public AssignmentDTO createAssignment(
      @RequestBody AssignmentDTO dto,
      @RequestBody int instructorId
      ) {

        User user = userRepository.findById(instructorId).orElse(null);
        // Verify user exists and is an instructor
        instructorExists(user);

        Assignment a = new Assignment();
        a.setAssignmentId(dto.id());
        a.setTitle(dto.title());
        a.setDue_date(Date.valueOf(dto.dueDate()));

        //check if the course exists
        Course c = courseRepository.findById(dto.courseId()).orElse(null);
        if(c==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "course not found " + dto.id());
        }

        //check if the section exists
        Section s = sectionRepository.findBySecIdAndSectionNo(dto.secId(),dto.secNo());
        if (s==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "section not found " + dto.secId());
        }

        //link the assignment to the section
        a.setSection(s);

        //save Assignment ID, Title, Duedate, and Section to Assignment Table.
        assignmentRepository.save(a);

        //return the information
        return new AssignmentDTO(
                a.getAssignmentId(),
                a.getTitle(),
                dto.dueDate(),
                c.getCourseId(),
                s.getSecId(),
                s.getSectionNo()
        );
    }

    // update assignment for a section.  Only title and dueDate may be changed.
    // user must be instructor of the section
    // return updated AssignmentDTO
    @PutMapping("/assignments")
    public AssignmentDTO updateAssignment(
      @RequestBody AssignmentDTO dto,
      @RequestBody int instructorId
      ) {

        User user = userRepository.findById(instructorId).orElse(null);
        // Verify user exists and is an instructor
        instructorExists(user);

        Assignment a = assignmentRepository.findById(dto.id()).orElse(null);
        if (a==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "assignment not found " + dto.id());
        } else {
            a.setTitle(dto.title());
            a.setDue_date(Date.valueOf(dto.dueDate()));
            assignmentRepository.save(a);
            return new AssignmentDTO(
                    a.getAssignmentId(),
                    a.getTitle(),
                    a.getDue_date().toString(),
                    null,
                    a.getSection().getSecId(),
                    a.getSection().getSectionNo()
            );
        }
    }

    // delete assignment for a section
    // logged in user must be instructor of the section
    @DeleteMapping("/assignments/{assignmentId}")
    public void deleteAssignment(
      @PathVariable("assignmentId") int assignmentId,
      @RequestBody int instructorId
      ) {

        User user = userRepository.findById(instructorId).orElse(null);
        // Verify user exists and is an instructor
        instructorExists(user);

        Assignment a = assignmentRepository.findById(assignmentId).orElse(null);
        //if assignment does not exist, do nothing.
        if(a!=null){
            assignmentRepository.delete(a);
        }
    }

    // instructor gets grades for assignment ordered by student name
    // user must be instructor for the section
    @GetMapping("/assignments/{assignmentId}/grades")
    public List<GradeDTO> getAssignmentGrades(
      @PathVariable("assignmentId") int assignmentId) {

        // TODO remove the following line when done

        // get the list of enrollments for the section related to this assignment.
		// hint: use te enrollment repository method findEnrollmentsBySectionOrderByStudentName.
        // for each enrollment, get the grade related to the assignment and enrollment
		//   hint: use the gradeRepository findByEnrollmentIdAndAssignmentId method.
        //   if the grade does not exist, create a grade entity and set the score to NULL
        //   and then save the new entity

        return null;
    }

    // instructor uploads grades for assignment
    // user must be instructor for the section
    @PutMapping("/grades")
    public void updateGrades(@RequestBody List<GradeDTO> dlist) {

        // TODO

        // for each grade in the GradeDTO list, retrieve the grade entity
        // update the score and save the entity

    }
    
    // student lists their assignments/grades for an enrollment ordered by due date
    // student must be enrolled in the section
    @GetMapping("/assignments")
    public List<AssignmentStudentDTO> getStudentAssignments(
      @RequestParam("studentId") int studentId,
      @RequestParam("year") int year,
      @RequestParam("semester") String semester) {

        //creates a list of sections based on studentId, year, semester
        List<Section> sections = sectionRepository.findByStudentIdAndYearAndSemester(studentId, year, semester);
        List<AssignmentStudentDTO> assignmentDTO = new ArrayList<>();

        //creates a list assignments for the sections above
        for (Section section : sections){
            List<Assignment> assignments = assignmentRepository.findBySectionNoOrderByDueDate(section.getSectionNo());

            //creates a list of AssignmentStudentDTO's based on the list of assignments above
            for(Assignment assignment : assignments){
                String courseId = section.getCourse().getCourseId();
                Grade grade = gradeRepository.findByEnrollmentIdAndAssignmentId(studentId, assignment.getAssignmentId());
                Integer score = grade.getScore();
                assignmentDTO.add(new AssignmentStudentDTO(
                                    assignment.getAssignmentId(),
                                    assignment.getTitle(),
                                    assignment.getDue_date(),
                                    courseId,
                                    assignment.getSection().getSecId(),
                                    score

               ));
            }

        }



 /*       User user = userRepository.findById(studentId).orElse(null);
        // Verify user exists and is a student
        studentExists(user);

        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByStudentIdOrderByTermId(studentId);

        List<Assignment> assignments = assignmentRepository.findByStudentIdAndYearAndSemesterOrderByDueDate(studentId, year, semester);

        List<AssignmentStudentDTO> dto_list = new ArrayList<>();*/
//        for (Assignment a : assignments) {
//          Grade g = gradeRepository.findByEnrollmentIdAndAssignmentId(enrollmentId, a.getAssignmentId());
//          dto_list.add(new AssignmentStudentDTO(a.getAssignmentId(), a.getTitle(), a.getDue_date(),
//            a.getSection().getCourse().getCourseId(), a.getSection().getSecId(), a.getScore()));
//        }
//        return dto_list;
        return assignmentDTO;
        // return a list of assignments and (if they exist) the assignment grade
        //  for all sections that the student is enrolled for the given year and semester
        //  hint: use the assignment repository method findByStudentIdAndYearAndSemesterOrderByDueDate
    }

    private void studentExists(User user) {
      // Verify user exists and is a student
      if (user == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
      }
      if (!(user.getType().equals("STUDENT"))) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
          "You have attempted to add a course to a user that is not a student.");
      }
    }

  private void instructorExists(User user) {
    // Verify user exists and is a student
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
    }
    if (!(user.getType().equals("INSTRUCTOR"))) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
        "You have attempted to add a course to a user that is not a student.");
    }
  }
}
