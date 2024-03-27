package com.cst438.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface CourseRepository extends CrudRepository<Course, String> {
	
    List<Course> findAllByOrderByCourseIdAsc();

    @Query("select distinct s.course from Term t join t.sections s " +
            "where t.year=:year and t.semester=:semester order by s.course.courseId")
    List<Course> findByYearAndSemesterOrderByCourseId(int year, String semester);
}
