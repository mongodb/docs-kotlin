import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BookARoomTest {
    // :snippet-start: room-data-class
    data class HotelRoom(
        @BsonId val id: Int,
        val guest: String? = null,
        val room: String,
        val reserved: Boolean = false
    )
    // :snippet-end:

    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        val database = client.getDatabase("hotel")
        val collection = database.getCollection<HotelRoom>("rooms")

        val guest = "Pat"

        @AfterAll
        @JvmStatic
        fun afterAll() {
            runBlocking {
                client.close()
            }
        }
    }

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            collection.drop()
            val room =
                HotelRoom(1, null, "Blue Room")
            collection.insertOne(room)
        }
    }

    @AfterEach
    fun afterEach() {
        runBlocking {
            collection.drop()
        }
    }

    @Test
    // :snippet-start: unsafe
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
        // :remove-start:
        val results = collection.find(roomFilter)
        Assertions.assertTrue(results.first().reserved)
        // :remove-end:
    }
    // :snippet-end:
    @Test
    // :snippet-start: safe
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
        // :remove-start:
        val results = collection.find(Filters.eq("_id", myRoom.id))
        Assertions.assertTrue(results.first().reserved)
        // :remove-end:
    }
    // :snippet-end:
}