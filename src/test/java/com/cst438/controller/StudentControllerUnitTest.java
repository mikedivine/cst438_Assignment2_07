package com.cst438.controller;

import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Section;
import com.cst438.domain.SectionRepository;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.dto.SectionDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static com.cst438.test.utils.TestUtils.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest

public class StudentControllerUnitTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    EnrollmentRepository enrollmentRepository;


    // student enrolls into a section
    @Test
    public void enrollSection() throws Exception {
        MockHttpServletResponse response;

        //enroll in section 6
        response = mvc.perform(
                MockMvcRequestBuilders
                        .post("/enrollments/sections/6?studentId=3")
        ).andReturn().getResponse();

        assertEquals(200, response.getStatus());

        EnrollmentDTO result = fromJsonString(response.getContentAsString(), EnrollmentDTO.class);

        //check successful add
        assertEquals(6, result.sectionNo());

        //check database
        Enrollment e = enrollmentRepository.findById(result.enrollmentId()).orElse(null);
        assertNotNull(e);
        assertEquals(3, e.getUser().getId());

        //cleanup after test, delete enrollment
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .delete("/enrollments/"+result.enrollmentId()+"?studentId=3")
        ).andReturn().getResponse();

        assertEquals(200, response.getStatus());

        //check database for delete
        e = enrollmentRepository.findById(result.enrollmentId()).orElse(null);
        assertNull(e);
    }

    // #7 student attempts to enroll in section but fails
    //  because student is already enrolled
    @Test
    public void alreadyEnrolled() throws Exception {
        MockHttpServletResponse response;

        //studentId=3, enrolls in section 8
        response = mvc.perform(
                MockMvcRequestBuilders
                        .post("/enrollments/sections/8?studentId=3")
        ).andReturn().getResponse();


        assertEquals(409, response.getStatus());
        assertEquals("You have attempted to add a course the student is already enrolled in.", response.getErrorMessage());

    }

    // student attempts to enroll in section but fails
    //  because section does not exist
    @Test
    public void invalidSection() throws Exception {
        MockHttpServletResponse response;

        //studentId=3, input sectionNo=999999
        response = mvc.perform(
                MockMvcRequestBuilders
                        .post("/enrollments/sections/999999?studentId=3")
        ).andReturn().getResponse();

        assertEquals(404, response.getStatus());
        assertEquals("Section not found", response.getErrorMessage());

    }

    // student attempts to enroll in section but fails
    //  because deadline to enroll has passed
    @Test
    public void pastDeadline() throws Exception {
        MockHttpServletResponse response;

        //studentId=3, sectionNo=1, sectionNo 1 add deadline == 2023-08-30
        response = mvc.perform(
                MockMvcRequestBuilders
                        .post("/enrollments/sections/1?studentId=3")
        ).andReturn().getResponse();

        assertEquals(409, response.getStatus());
        assertEquals("You have attempted to add a course after the Add Deadline.", response.getErrorMessage());
    }

}
