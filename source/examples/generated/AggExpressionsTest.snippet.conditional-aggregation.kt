val member = current().getField("member")

listOf(project(fields(
    computed("membershipLevel",
        member.switchOn{field -> field
            .isString{s-> s}
            .isBoolean{b -> b.cond(of("Gold"), of("Guest"))}
            .isArray { a -> a.last()}
            .defaults{ d -> of("Guest")}})
)))
