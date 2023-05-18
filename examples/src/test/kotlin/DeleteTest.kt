import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts.ascending
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.*

// :snippet-start: delete-data-model
data class Movie(
    @BsonId val id: Int,
    val qty: Int,
    val color: String
)
// :snippet-end:

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DeleteTest {

    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        val database = client.getDatabase("paint_store")
        val collection = database.getCollection<Movie>("paint_order")

        @BeforeAll
        @JvmStatic
        private fun beforeAll() {
            runBlocking {
                val paintOrders = listOf(
                    Movie(1, 5, "red"),
                    Movie(2, 8, "purple"),
                    Movie(3, 0, "blue"),
                    Movie(4, 0, "white"),
                    Movie(5, 6, "yellow"),
                    Movie(6, 0, "pink"),
                    Movie(7, 0, "green"),
                    Movie(8, 8, "black")
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
    fun deleteManyTest() = runBlocking {
        // :snippet-start: delete-many
        val filter = Filters.eq("qty", 0)
        collection.deleteMany(filter)
        // :snippet-end:
        // Junit test for the above code
        assertTrue(collection.find(filter).toList().isEmpty() )
        // confirm docs remaining in collection
        val results = collection.find().sort(ascending("_id")).toList()
        val expected = listOf(
            Movie(1, 5, "red"),
            Movie(2, 8, "purple"),
            Movie(5, 6, "yellow"),
            Movie(8, 8, "black")
        )
        assertEquals(expected, results)
    }

    @Test
    fun deleteOneTest() = runBlocking {
        // :snippet-start: delete-one
        val filter = Filters.eq("color", "yellow")
        collection.deleteOne(filter)
        // :snippet-end:
        // Junit test for the above code
        assertTrue(collection.find(filter).toList().isEmpty() )
        // confirm docs remaining in collection
        val testFilter = Filters.empty()
        val results = collection.find(testFilter).sort(ascending("_id")).toList()
        val expected = listOf(
            Movie(1, 5, "red"),
            Movie(2, 8, "purple"),
            Movie(8, 8, "black")
        )
        assertEquals(expected, results)
    }

    @Test
    fun findAndDeleteTest() = runBlocking {
        // :snippet-start: find-one-and-delete
        val filter = Filters.eq("color", "purple")
        val result = collection.findOneAndDelete(filter)
        println("The following was deleted: $result")
        // :snippet-end:
        // Junit test for the above code
        assertTrue(collection.find(filter).toList().isEmpty())
        // confirm docs remaining in collection
        val testFilter = Filters.empty()
        val results = collection.find(testFilter).sort(ascending("_id")).toList()
        val expected = listOf(
            Movie(1, 5, "red"),
            Movie(8, 8, "black")
        )
        assertEquals(expected, results)
    }
}