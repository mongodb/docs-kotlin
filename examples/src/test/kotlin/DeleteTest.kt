import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts.ascending
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.*

// :snippet-start: retrieve-data-model
data class PaintOrder(
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
        val collection = database.getCollection<PaintOrder>("paint_order")

        @BeforeAll
        @JvmStatic
        private fun beforeAll() {
            runBlocking {

                val paintOrders = listOf(
                    PaintOrder(1, 5, "red"),
                    PaintOrder(2, 8, "purple"),
                    PaintOrder(3, 0, "blue"),
                    PaintOrder(4, 0, "white"),
                    PaintOrder(5, 6, "yellow"),
                    PaintOrder(6, 0, "pink"),
                    PaintOrder(7, 0, "green"),
                    PaintOrder(8, 8, "black")
                )
                collection.insertMany(paintOrders)

            }
        }


        @AfterAll
        @JvmStatic
        private fun afterAll() {
            runBlocking {
                collection.deleteMany(Filters.empty())
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
        collection.find().sort(ascending("_id"))
            .toList().forEach { println(it) }
        val expected = listOf(
            PaintOrder(1, 5, "red"),
            PaintOrder(2, 8, "purple"),
            PaintOrder(5, 6, "yellow"),
            PaintOrder(8, 8, "black")
        )
        assertEquals(expected, collection.find().sort(ascending("_id")).toList())
    }

    @Test
    fun deleteOneTest() = runBlocking {
        // delete documents from previous steps
        val previousDelete = Filters.eq("qty", 0)
        collection.deleteMany(previousDelete)
        // :snippet-start: delete-one
        val filter = Filters.eq("color", "yellow")
        collection.deleteOne(filter)
        // :snippet-end:
        // Junit test for the above code
        assertTrue(collection.find(filter).toList().isEmpty() )
        // confirm docs remaining in collection
        val testFilter = Filters.empty()
        collection.find(testFilter).sort(ascending("_id"))
            .toList().forEach { println(it) }
        val expected = listOf(
            PaintOrder(1, 5, "red"),
            PaintOrder(2, 8, "purple"),
            PaintOrder(8, 8, "black")
        )
        assertEquals(expected, collection.find(testFilter).toList())
    }

    @Test
    fun findAndDeleteTest() = runBlocking {
        // delete documents from previous steps
        collection.deleteMany(Filters.or(Filters.eq("qty", 0), Filters.eq("color", "yellow")))
        // :snippet-start: find-one-and-delete
        val filter = Filters.eq("color", "purple")
        println(collection.findOneAndDelete(filter))
        // :snippet-end:
        // Junit test for the above code
        assertTrue(collection.find(filter).toList().isEmpty())
        // confirm docs remaining in collection
        val testFilter = Filters.empty()
        collection.find(testFilter).sort(ascending("_id"))
            .toList().forEach { println(it) }
        val expected = listOf(
            PaintOrder(1, 5, "red"),
            PaintOrder(8, 8, "black")
        )
        assertEquals(expected, collection.find(testFilter).toList())
    }
}