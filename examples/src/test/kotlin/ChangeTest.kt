import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class InsertTest {

    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        val database = client.getDatabase("paint_store")
        val collection = database.getCollection<Document>("paint_order")


        @BeforeAll
        @JvmStatic
        private fun beforeAll() {
            runBlocking {

                val paintOrders = listOf(
                    Document("_id", 1).append("color", "red").append("qty", 5),
                    Document("_id", 2).append("color", "purple").append("qty", 8),
                    Document("_id", 3).append("color", "yellow").append("qty", 0),
                    Document("_id", 4).append("color", "green").append("qty", 6),
                    Document("_id", 5).append("color", "pink").append("qty", 0)
                )
                collection.insertMany(paintOrders)

            }
        }
        @AfterAll
        @JvmStatic
        private fun afterAll() {
            runBlocking {
                collection.drop()
                client.close()
            }
        }
    }

    @Test
    fun updateManyTest() = runBlocking {
        // :snippet-start: update-many
        val filter = Filters.empty()
        val update = Updates.inc("qty", 20)
        val result = collection.updateMany(filter, update)
        println("Matched document count: $result.matchedCount")
        println("Modified document count: $result.modifiedCount")
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
        Document("_id", 1).append("color", "red").append("qty", 25),
        Document("_id", 2).append("color", "purple").append("qty", 28),
        Document("_id", 3).append("color", "yellow").append("qty", 20),
        Document("_id", 4).append("color", "green").append("qty", 26),
        Document("_id", 5).append("color", "orange").append("qty", 45)
       )
        assertEquals(expected, collection.find().toList())
    }


    @Test
    fun replaceOneTest() = runBlocking {
        // :snippet-start: replace-one
        val filter = Filters.eq("color", "pink")
        val document = Document("color", "orange").append("qty", 25)
        val result = collection.replaceOne(filter, document)
        println("Matched document count: $result.matchedCount")
        println("Modified document count: $result.modifiedCount")
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            Document("_id", 5).append("color", "orange").append("qty", 25)
        )
        assertEquals(expected, collection.find(document).toList())
    }
}
