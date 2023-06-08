val lastName = current().getString("lastName")
val employeeID = current().getString("employeeID")

listOf(project(fields(
    computed("username", lastName
        .append(employeeID)
        .toLower())
)))
