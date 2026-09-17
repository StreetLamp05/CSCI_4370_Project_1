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
