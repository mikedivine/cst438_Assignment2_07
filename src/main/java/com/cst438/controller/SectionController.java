package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.SectionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class SectionController {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    TermRepository termRepository;

    @Autowired
    UserRepository userRepository;


    // ADMIN function to create a new section
    @PostMapping("/sections")
    public SectionDTO addSection(@RequestBody SectionDTO section) {

        Course course = courseRepository.findById(section.courseId()).orElse(null);
        if (course == null ){
            throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "course not found " + section.courseId());
        }
        Section s = new Section();
        s.setCourse(course);

        Term term = termRepository.findByYearAndSemester(section.year(), section.semester());
        if (term == null) {
            throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "year, semester invalid ");
        }
        s.setTerm(term);

        s.setSecId(section.secId());
        s.setBuilding(section.building());
        s.setRoom(section.room());
        s.setTimes(section.times());

        User instructor = null;
        if (section.instructorEmail()==null || section.instructorEmail().equals("")) {
            s.setInstructor_email("");
        } else {
            instructor = userRepository.findByEmail(section.instructorEmail());
            if (instructor == null || !instructor.getType().equals("INSTRUCTOR")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "email not found or not an instructor " + section.instructorEmail());
            }
            s.setInstructor_email(section.instructorEmail());
        }

        sectionRepository.save(s);
        return new SectionDTO(
                s.getSectionNo(),
                s.getTerm().getYear(),
                s.getTerm().getSemester(),
                s.getCourse().getCourseId(),
                s.getSecId(),
                s.getBuilding(),
                s.getRoom(),
                s.getTimes(),
                course.getTitle(),
                (instructor!=null) ? instructor.getName() : "",
                (instructor!=null) ? instructor.getEmail() : ""
        );
    }

    // ADMIN function to update a section
    @PutMapping("/sections")
    public void updateSection(@RequestBody SectionDTO section) {
        // can only change instructor email, sec_id, building, room, times, start, end dates
        Section s = sectionRepository.findById(section.secNo()).orElse(null);
        if (s==null) {
            throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "section not found "+section.secNo());
        }
        s.setSecId(section.secId());
        s.setBuilding(section.building());
        s.setRoom(section.room());
        s.setTimes(section.times());

        User instructor = null;
        if (section.instructorEmail()==null || section.instructorEmail().equals("")) {
            s.setInstructor_email("");
        } else {
            instructor = userRepository.findByEmail(section.instructorEmail());
            if (instructor == null || !instructor.getType().equals("INSTRUCTOR")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "email not found or not an instructor " + section.instructorEmail());
            }
            s.setInstructor_email(section.instructorEmail());
        }
        sectionRepository.save(s);
    }

    // ADMIN function to create a delete section
    // delete will fail if there are related assignments or enrollments
    @DeleteMapping("/sections/{sectionno}")
    public void deleteSection(@PathVariable int sectionno) {
        Section s = sectionRepository.findById(sectionno).orElse(null);
        if (s != null) {
            sectionRepository.delete(s);
        }
    }


    // get Sections for a course with request params year, semester
    // example URL   /course/cst363/sections?year=2024&semester=Spring
    // also specify partial courseId   /course/cst/sections?year=2024&semester=Spring
    @GetMapping("/courses/{courseId}/sections")
    public List<SectionDTO> getSections(
            @PathVariable("courseId") String courseId,
            @RequestParam("year") int year ,
            @RequestParam("semester") String semester )  {


        List<Section> sections = sectionRepository.findByLikeCourseIdAndYearAndSemester(courseId+"%", year, semester);

        List<SectionDTO> dto_list = new ArrayList<>();
        for (Section s : sections) {
            Course course = s.getCourse();
            User instructor = null;
            if (s.getInstructorEmail()!=null) {
                instructor = userRepository.findByEmail(s.getInstructorEmail());
            }
            dto_list.add(new SectionDTO(
                    s.getSectionNo(),
                    s.getTerm().getYear(),
                    s.getTerm().getSemester(),
                    s.getCourse().getCourseId(),
                    s.getSecId(),
                    s.getBuilding(),
                    s.getRoom(),
                    s.getTimes(),
                    course.getTitle(),
                    (instructor!=null) ? instructor.getName() : "",
                    (instructor!=null) ? instructor.getEmail() : ""
            ));

        }
        return dto_list;
    }

    // get Sections for an instructor
    // example URL  /sections?instructorEmail=dwisneski@csumb.edu&year=2024&semester=Spring
    @GetMapping("/sections")
    public List<SectionDTO> getSectionsForInstructor(
            @RequestParam("email") String instructorEmail,
            @RequestParam("year") int year ,
            @RequestParam("semester") String semester )  {


        List<Section> sections = sectionRepository.findByInstructorEmailAndYearAndSemester(instructorEmail, year, semester);

        List<SectionDTO> dto_list = new ArrayList<>();
        for (Section s : sections) {
            Course course = s.getCourse();
            User instructor = null;
            if (s.getInstructorEmail()!=null) {
                instructor = userRepository.findByEmail(s.getInstructorEmail());
            }
            dto_list.add(new SectionDTO(
                    s.getSectionNo(),
                    s.getTerm().getYear(),
                    s.getTerm().getSemester(),
                    s.getCourse().getCourseId(),
                    s.getSecId(),
                    s.getBuilding(),
                    s.getRoom(),
                    s.getTimes(),
                    course.getTitle(),
                    (instructor!=null) ? instructor.getName() : "",
                    (instructor!=null) ? instructor.getEmail() : ""
            ));
        }
        return dto_list;
    }

    // List available Sections for Enrollment
    @GetMapping("/sections/open")
    public List<SectionDTO> getOpenSectionsForEnrollment() {

        List<Section> sections = sectionRepository.findByOpenOrderByCourseIdSectionId();

        List<SectionDTO> dlist = new ArrayList<>();
        for (Section s : sections) {
            Course course = s.getCourse();
            User instructor = userRepository.findByEmail(s.getInstructorEmail());
            dlist.add( new SectionDTO(
                    s.getSectionNo(),
                    s.getTerm().getYear(),
                    s.getTerm().getSemester(),
                    s.getCourse().getCourseId(),
                    s.getSecId(),
                    s.getBuilding(),
                    s.getRoom(),
                    s.getTimes(),
                    course.getTitle(),
                    (instructor!=null) ? instructor.getName() : "",
                    (instructor!=null) ? instructor.getEmail() : ""
            ));
        }
        return dlist;
    }
}
