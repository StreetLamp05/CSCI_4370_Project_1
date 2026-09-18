/**
 * Copyright (c) 2025 Sami Menik, PhD. All rights reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 * This software is provided "as is," without warranty of any kind.
 */
package uga.csx370.mydbimpl;

import java.io.File;
import java.util.List;

import uga.csx370.mydb.RA;
import uga.csx370.mydb.Relation;
import uga.csx370.mydb.RelationBuilder;
import uga.csx370.mydb.Type;

public class Driver {

    public static void main(String[] args) {
        String dataDir = findDataDir();

        Relation department = new RelationBuilder()
                .attributeNames(List.of("dept_name", "building", "budget"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        department.loadData(dataDir + "/department.csv");

        Relation instructor = new RelationBuilder()
                .attributeNames(List.of("ID", "name", "dept_name", "salary"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        instructor.loadData(dataDir + "/instructor.csv");

        Relation course = new RelationBuilder()
                .attributeNames(List.of("course_id", "title", "dept_name", "credits"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        course.loadData(dataDir + "/course.csv");

        Relation teaches = new RelationBuilder()
                .attributeNames(List.of("ID", "course_id", "sec_id", "semester", "year"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        teaches.loadData(dataDir + "/teaches.csv");

        RA ra = new RAImpl();

        System.out.println("Jasmines Query: Find the name, department building, and department budget of every "
                + "instructor in the Computer Science department, together with the title, semester, "
                + "and year of every course section that instructor has taught.");
        System.out.println();

        Relation csDept = ra.select(department,
                row -> row.get(0).getAsString().equals("Comp. Sci."));

        Relation csInstructor = ra.select(instructor,
                row -> row.get(2).getAsString().equals("Comp. Sci."));
        Relation csInstructorTrimmed = ra.project(csInstructor, List.of("ID", "name", "dept_name"));

        Relation instructorWithDept = ra.join(csInstructorTrimmed, csDept);
        Relation instructorWithDeptTrimmed = ra.project(instructorWithDept,
                List.of("ID", "name", "building", "budget"));

        Relation instructorTeaches = ra.join(instructorWithDeptTrimmed, teaches);
        Relation instructorTeachesCourse = ra.join(instructorTeaches, course);

        Relation projected = ra.project(instructorTeachesCourse,
                List.of("name", "title", "semester", "year", "building", "budget"));
        Relation result = ra.rename(projected,
                List.of("name", "title", "building", "budget"),
                List.of("instructor_name", "course_title", "dept_building", "dept_budget"));

        result.print();

        System.out.println();
        System.out.println("Davids Query: Find every instructor who has taught a section since 2005 of a "
                + "course that is owned by a department other than their own, with the course "
                + "title, the department that owns the course, and that department's building");
        System.out.println();

        Relation recentTeaches = ra.select(teaches, row -> row.get(4).getAsInt() >= 2005);

        Relation taughtBy = ra.join(instructor, recentTeaches);
        Relation taughtByTrimmed = ra.project(taughtBy, List.of("name", "dept_name", "course_id"));
        Relation taughtByRenamed = ra.rename(taughtByTrimmed,
                List.of("name", "dept_name", "course_id"),
                List.of("instructor_name", "instructor_dept", "taught_course_id"));

        Relation courseRenamed = ra.rename(course,
                List.of("course_id", "title", "dept_name", "credits"),
                List.of("offered_course_id", "course_title", "course_dept", "course_credits"));

        Relation crossDept = ra.join(taughtByRenamed, courseRenamed,
                row -> row.get(2).getAsString().equals(row.get(3).getAsString())
                        && !row.get(1).getAsString().equals(row.get(5).getAsString()));
        Relation crossDeptTrimmed = ra.project(crossDept,
                List.of("instructor_name", "instructor_dept", "course_title", "course_dept"));
        Relation crossDeptReady = ra.rename(crossDeptTrimmed,
                List.of("course_dept"), List.of("dept_name"));

        Relation crossDeptWithBuilding = ra.join(crossDeptReady, department);
        Relation crossDeptProjected = ra.project(crossDeptWithBuilding,
                List.of("instructor_name", "instructor_dept", "course_title", "dept_name", "building"));
        Relation crossDeptResult = ra.rename(crossDeptProjected,
                List.of("dept_name", "building"),
                List.of("course_dept", "course_dept_building"));

        crossDeptResult.print();

        System.out.println();
        System.out.println("Synah's Query: Find instructors who taught a four-credit course offered by "
                + "their own department, including the course title, semester, year, and "
                + "department building.");
        System.out.println();

        Relation fourCreditCourses = ra.select(course,
                row -> row.get(3).getAsInt() == 4);

        Relation instructorsAndSections = ra.join(instructor, teaches);
        Relation matchingCourses = ra.join(instructorsAndSections, fourCreditCourses);
        Relation withDepartmentBuilding = ra.join(matchingCourses, department);

        Relation synahProjected = ra.project(withDepartmentBuilding,
                List.of("name", "title", "semester", "year", "dept_name", "building"));

        Relation synahResult = ra.rename(synahProjected,
                List.of("name", "title", "dept_name", "building"),
                List.of("instructor_name", "course_title", "department", "dept_building"));

        synahResult.print();

        System.out.println();
        System.out.println("Anvita's Query: Find the names and salaries of instructors in departments "
                + "that offer three-credit courses, along with the course title and department building.");
        System.out.println();

        Relation threeCreditCourses = ra.select(course,
                row -> row.get(3).getAsInt() == 3);

        Relation instructorCourses = ra.join(instructor, threeCreditCourses);

        Relation instructorCoursesDept = ra.join(instructorCourses, department);

        Relation anvitaResult = ra.project(instructorCoursesDept,
                List.of("name", "salary", "title", "building"));

        anvitaResult.print();


    }

    private static String findDataDir() {
        String[] candidates = {
            "data",
            "CSCI_4370_Project_1/data",
            "../CSCI_4370_Project_1/data",
        };
        for (String candidate : candidates) {
            if (new File(candidate, "department.csv").isFile()) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not locate the data directory. Run the Driver "
                + "with the working directory set to the project root (CSCI_4370_Project_1), "
                + "which contains the data/ folder.");
    }

}
