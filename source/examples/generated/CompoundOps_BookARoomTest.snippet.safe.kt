fun bookARoomSafe() = runBlocking {
    val update = Updates.combine(
        Updates.set(HotelRoom::reserved.name, true),
        Updates.set(HotelRoom::guest.name, guest)
    )
    val filter = Filters.eq("reserved", false)
    val myRoom = collection.findOneAndUpdate(filter, update)
    if (myRoom == null) {
        println("Sorry, we are booked, $guest")
        return@runBlocking
    }

    val myRoomName = myRoom.room
    println("You got the $myRoomName, $guest")
}
