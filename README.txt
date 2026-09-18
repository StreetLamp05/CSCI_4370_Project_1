# CSCI 4370 - Project 1
## Group 7

David Kan

Jasmine Nguyen

Synah Khurana

Anvita Yerramsetty

Nikita Neeli


###  Contributions
#### Jasmine Nguyen
- Implemented select, project, and rename
- Added private helper methods:
  - emptyRelationWithSchema
  - resolveAttrIndices
  - extractCells.
- Set up the Driver class
- Exported the uni_in_class tables to CSV
  (data/course.csv, data/department.csv, data/instructor.csv, data/teaches.csv).
- Query 1: Find the name, department building, and department budget of every
  instructor in the Computer Science department, together with the title,
  semester, and year of every course section that instructor has taught.

#### David Kan
- Set up the project repository with the starter code
- Implemented the natural join: join(Relation, Relation)
- Implemented the predicate join: join(Relation, Relation, Predicate)
- Added the private helpers:
  - commonAttrs
  - attrsOnlyIn
  - attrTypesAt
  - groupRowsByKey
  - concatCells.
- Query 2: Find every instructor who has taught a section since 2005 of a course
that is owned by a department other than their own, together with the course
title, the department that owns the course, and that department's building.

#### Synah Khurana
- Query 3: Find instructors who taught a four-credit course offered by their own
department, including the course title, semester, year, and department
building.

#### Nikita Neeli
- Query 4: Find every instructor who has taught a section in Fall 2006 of a
course offered by the Physics department, with that instructor's own
department, course title, section semester and year, and budget of the
department that offers the course.

#### Anvita Yerramsetty
- Implemented union, intersect, diff, and cartesianProduct
- Query 5: Find the names and salaries of instructors in departments that offer
three-credit courses, along with the course title and department building.
