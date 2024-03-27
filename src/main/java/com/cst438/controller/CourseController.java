package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.CourseDTO;
import com.cst438.dto.SectionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/*
 * Controller for managing courses and sections.
 *   List courses, add course, update, delete course.
 *   List sections for a course, add, update and delete section.
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class CourseController {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    TermRepository termRepository;

    @Autowired
    UserRepository userRepository;


    // ADMIN function to create a new course
    @PostMapping("/courses")
    public CourseDTO addCourse(@RequestBody CourseDTO course) {
        Course c = new Course();
        c.setCredits(course.credits());
        c.setTitle(course.title());
        c.setCourseId(course.courseId());
        courseRepository.save(c);
        return new CourseDTO(
                c.getCourseId(),
                c.getTitle(),
                c.getCredits()
        );
    }

    // ADMIN function to update a course
    @PutMapping("/courses")
    public CourseDTO updateCourse(@RequestBody CourseDTO course) {
        Course c = courseRepository.findById(course.courseId()).orElse(null);
        if (c==null) {
            throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "course not found "+course.courseId());
        } else {
            c.setTitle(course.title());
            c.setCredits(course.credits());
            courseRepository.save(c);
            return new CourseDTO(
                    c.getCourseId(),
                    c.getTitle(),
                    c.getCredits()
            );
        }
    }

    // ADMIN function to delete a course
    // delete will fail if the course has sections
    @DeleteMapping("/courses/{courseid}")
    public void deleteCourse(@PathVariable String courseid) {
        Course c = courseRepository.findById(courseid).orElse(null);
        // if course does not exist, do nothing.
        if (c!=null) {
            courseRepository.delete(c);
        }
    }

    @GetMapping("/courses")
    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAllByOrderByCourseIdAsc();
        List<CourseDTO> dto_list = new ArrayList<>();
        for (Course c : courses) {
            dto_list.add(new CourseDTO(c.getCourseId(), c.getTitle(), c.getCredits()));
        }
        return dto_list;
    }

    @GetMapping("/terms")
    public List<Term> getAllTerms() {
        return termRepository.findAllByOrderByTermIdDesc();
    }


}
