fun bookARoomUnsafe() = runBlocking {
    val filter = Filters.eq("reserved", false)
    val myRoom = collection.find(filter).firstOrNull()
    if (myRoom == null) {
        println("Sorry, we are booked, $guest")
        return@runBlocking
    }

    val myRoomName = myRoom.room
    println("You got the $myRoomName, $guest")

    val update = Updates.combine(Updates.set("reserved", true), Updates.set("guest", guest))
    val roomFilter = Filters.eq("_id", myRoom.id)
    collection.updateOne(roomFilter, update)
}
