package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.AssignmentDTO;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.dto.GradeDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cst438.test.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@AutoConfigureMockMvc
@SpringBootTest
public class InstructorControllerUnitTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  AssignmentRepository assignmentRepository;

  @Autowired
  GradeRepository gradeRepository;

  @Autowired
  EnrollmentRepository enrollmentRepository;

  // instructor adds a new assignment
  @Test
  public void addNewAssignment() throws Exception {

    MockHttpServletResponse response;

    // create DTO with data for new assignment.
    // the primary key, id, is set to 0. it will be
    // set by the database when the section is inserted.
    AssignmentDTO assignment = new AssignmentDTO(
      0,
      "Test Assignment",
      "2024-03-03",
      "cst363",
      "The Good Stuff",
      1,
      8
    );

    // issue a http POST request to SpringTestServer
    // convert Assignment to String data and set as request content
    response = mvc.perform(
        MockMvcRequestBuilders
          .post("/assignments")
          .param("instructorEmail", "dwisneski@csumb.edu")
          .accept(APPLICATION_JSON)
          .contentType(APPLICATION_JSON)
          .content(asJsonString(assignment)))
      .andReturn()
      .getResponse();

    // check the response code for 200 meaning OK
    assertEquals(200, response.getStatus());

    // return data converted from String to DTO
    AssignmentDTO result = fromJsonString(response.getContentAsString(), AssignmentDTO.class);

    // primary key should have a non zero value from the database
    assertNotEquals(0, result.secNo());
    // check other fields of the DTO for expected values
    assertEquals("cst363", result.courseId());

    // check the database
    Assignment a = assignmentRepository.findById(result.id()).orElse(null);
    assertNotNull(a);
    assertEquals(8, a.getSection().getSectionNo());
  }

  // instructor adds a new assignment with a due date past the end date of the class
  @Test
  public void addNewAssignmentBadDueDate() throws Exception {

    MockHttpServletResponse response;

    // create DTO with data for new assignment.
    // the primary key, id, is set to 0. it will be
    // set by the database when the section is inserted.
    AssignmentDTO assignment = new AssignmentDTO(
      0,
      "Test Assignment",
      "2025-03-03",
      "cst363",
      "The Good Stuff",
      1,
      8
    );

    // issue an http POST request to SpringTestServer
    // specify MediaType for request and response data
    // convert Assignment to String data and set as request content
    response = mvc.perform(
        MockMvcRequestBuilders
          .post("/assignments")
          .param("instructorEmail", "dwisneski@csumb.edu")
          .accept(APPLICATION_JSON)
          .contentType(APPLICATION_JSON)
          .content(asJsonString(assignment)))
      .andReturn()
      .getResponse();

    // response should be 409, CONFLICT
    assertEquals(409, response.getStatus());

    // check the expected error message
    String message = response.getErrorMessage();
    assertEquals("You have attempted to add an assignment with the Due Date: 2025-03-03 after the Section End Date: 2024-05-17", message);
  }

  // instructor adds a new assignment with invalid section number.
  @Test
  public void addNewAssignmentBadSectionNumber() throws Exception {

    MockHttpServletResponse response;

    // create DTO with data for new assignment.
    // the primary key, id, is set to 0. it will be
    // set by the database when the section is inserted.
    AssignmentDTO assignment = new AssignmentDTO(
      0,
      "Test Assignment",
      "2024-03-03",
      "cst363",
      "The Good Stuff",
      1,
      11
    );

    // issue an http POST request to SpringTestServer
    // specify MediaType for request and response data
    // convert Assignment to String data and set as request content
    response = mvc.perform(
        MockMvcRequestBuilders
          .post("/assignments")
          .param("instructorEmail", "dwisneski@csumb.edu")
          .accept(APPLICATION_JSON)
          .contentType(APPLICATION_JSON)
          .content(asJsonString(assignment)))
      .andReturn()
      .getResponse();

    // response should be 404, NOT_FOUND
    assertEquals(404, response.getStatus());

    // check the expected error message
    String message = response.getErrorMessage();
    assertEquals("Section 11 not found.", message);
  }

  // instructor grades an assignment and enters scores
  //  for all enrolled students and uploads the scores
  @Test
  public void gradeAssignmentEnterScoresForEnrolledStudents() throws Exception {

    MockHttpServletResponse response;

    // create DTO with data for a.
    // the primary key, id, is set to 0. it will be
    // set by the database when the section is inserted.
    List<GradeDTO> gradeList = new ArrayList<>();
    GradeDTO grade = new GradeDTO(
            1,
            "Test Name",
            "test@email.com",
            "Test Assignment",
            "cst363",
            8,
            89
    );

    gradeList.add(grade);

    // issue an http PUT request to SpringTestServer
    // specify MediaType for request and response data
    // convert Grade to String data and set as request content
    response = mvc.perform(
                    MockMvcRequestBuilders
                            .put("/grades")
                            .param("instructorEmail", "dwisneski@csumb.edu")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(asJsonString(gradeList)))
            .andReturn()
            .getResponse();

    // check the response code for 200 meaning OK
    assertEquals(200, response.getStatus());

    // check if the grade was updated in the DB from 95 to 89
    Grade updatedGradeFromDB = gradeRepository.findById(grade.gradeId()).orElse(null);
    assertNotNull(updatedGradeFromDB);
    assertEquals(89,updatedGradeFromDB.getScore());

    // check if the assignmentTitle is the same and has not changed.
    assertEquals("db homework 1",updatedGradeFromDB.getAssignment().getTitle());

    // clean up after test. Set the grade back to the original grade.
    updatedGradeFromDB.setScore(95);
    gradeRepository.save(updatedGradeFromDB);
    Grade resetGrade = gradeRepository.findById(grade.gradeId()).orElse(null);
    assertEquals(95,resetGrade.getScore());
  }

  // instructor attempts to grade an assignment
  //  but the assignment id is invalid
  @Test
  public void gradeAssignmentBadAssignmentId() throws Exception {

    MockHttpServletResponse response;

    // issue an http GET request to the SpringTestServer
    //  specifying assignmentId as -17 which doesn't exist
    response = mvc.perform(
        MockMvcRequestBuilders
          .get("/assignments/-17/grades")
          .param("instructorEmail", "dwisneski@csumb.edu")
          .accept(APPLICATION_JSON)
          .contentType(APPLICATION_JSON)
          .content(asJsonString("")))
      .andReturn()
      .getResponse();

    // response should be 404, NOT_FOUND
    assertEquals(404, response.getStatus());

    // check the expected error message
    String message = response.getErrorMessage();
    assertEquals("Assignment -17 not found", message);
  }

  // instructor enters final class grades
  //  for all enrolled students
  @Test
  public void enterFinalGrades() throws Exception {

    MockHttpServletResponse response;

    // create EnrollmentDTO List with final grades.
    List<EnrollmentDTO> enrollmentList = new ArrayList<>();
    Collections.addAll(enrollmentList,
      (new EnrollmentDTO(
        4,
        "A",
        0,
        null,
        null,
        null,
        0,
        0,
        null,
        null,
        null,
        0,
        0,
        null,
        null)
      ),
      (new EnrollmentDTO(
        5,
        "B",
        0,
        null,
        null,
        null,
        0,
        0,
        null,
        null,
        null,
        0,
        0,
        null,
        null)
      ),
      (new EnrollmentDTO(
        2,
        "C",
        0,
        null,
        null,
        null,
        0,
        0,
        null,
        null,
        null,
        0,
        0,
        null,
        null)
      )
    );

    // issue an http PUT request to the SpringTestServer
    response = mvc.perform(
        MockMvcRequestBuilders
          .put("/enrollments")
          .param("instructorEmail", "dwisneski@csumb.edu")
          .accept(APPLICATION_JSON)
          .contentType(APPLICATION_JSON)
          .content(asJsonString(enrollmentList)))
      .andReturn()
      .getResponse();

    // check the response code for 200 meaning OK
    assertEquals(200, response.getStatus());

    // issue an http GET request to the SpringTestServer
    //  to get all enrollments for Section Number 8
    response = mvc.perform(
        MockMvcRequestBuilders
          .get("/sections/8/enrollments")
          .param("instructorEmail", "dwisneski@csumb.edu")
          .accept(APPLICATION_JSON)
          .contentType(APPLICATION_JSON)
          .content(asJsonString("")))
      .andReturn()
      .getResponse();

    // verify correct grades
    Gson gson = new Gson();
    Type enrollmentListType = new TypeToken<List<EnrollmentDTO>>() {}.getType();
    List<EnrollmentDTO> enrollmentDTOs = gson.fromJson(response.getContentAsString(), enrollmentListType);

    // check that the grades show that they were updated
    assertEquals("A", enrollmentDTOs.get(0).grade());
    assertEquals("B", enrollmentDTOs.get(1).grade());
    assertEquals("C", enrollmentDTOs.get(2).grade());
  }
}
