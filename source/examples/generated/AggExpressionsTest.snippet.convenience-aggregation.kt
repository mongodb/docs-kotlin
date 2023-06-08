val students = current().getArray<MqlDocument>("students")

listOf(project(fields(
    computed("evaluation", students
        .passArrayTo { students -> gradeAverage(students, "finalGrade") }
        .passNumberTo { grade -> evaluate(grade, of(70), of(85)) })
)))
