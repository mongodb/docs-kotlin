val graduationYear = current().getString("graduationYear")

listOf(addFields(
    Field("reunionYear",
        graduationYear
            .parseInteger()
            .add(5))
))
