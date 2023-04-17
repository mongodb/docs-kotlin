import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Aggregates.skip
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
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

// :replace-start: {
    //    "terms": {
        //       "queryFilter": "filter"
        //    }
    // }

// :snippet-start: retrieve-data-model
data class PaintOrder(
    @BsonId val id: Int,
    val qty: Int,
    val color: String
)
// :snippet-end:

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SkipTest {

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
                    PaintOrder(2, 10, "purple"),
                    PaintOrder(3, 9, "blue"),
                    PaintOrder(4, 6, "white"),
                    PaintOrder(5, 11, "yellow"),
                    PaintOrder(6, 3, "pink"),
                    PaintOrder(7, 8, "green"),
                    PaintOrder(8, 7, "orange")
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
    fun basicSkipTest() = runBlocking {
        // :snippet-start: basic-skip
        collection.find().skip(2)
        // :snippet-end:
       val queryFilter = Filters.empty()
        // :snippet-start: aggregates-skip
        collection.aggregate(listOf(Aggregates.match(queryFilter), skip(2)))
        // :snippet-end:
        // :snippet-start: find-iterable-example
        val filter = Filters.empty()
        collection.find(filter)
            .sort(Sorts.descending("qty"))
            .skip(5)
            .toList().forEach { println(it) }
        // :snippet-end:
    }

    @Test
    fun basicAggregationTest() = runBlocking {
        // :snippet-start: aggregation-example
        val filter = Filters.empty()
        val aggregate = listOf(
            Aggregates.match(filter),
            Aggregates.sort(Sorts.descending("qty")),
            skip(5)
        )
        collection.aggregate<Document>(aggregate).toList().forEach { println(it.toJson()) }
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            PaintOrder(4, 6, "white"),
            PaintOrder(1, 5, "red"),
            PaintOrder(6, 3, "pink")
        )
        assertEquals(expected, collection.aggregate(aggregate).toList())
    }

    @Test
    fun noResultsTest() = runBlocking {
        // :snippet-start: no-results-example
        val filter = Filters.empty()
        collection.find(filter)
            .sort(Sorts.descending("qty"))
            .skip(9)
            .toList().forEach { println(it) }
        // :snippet-end:
        // Junit test for the above code
        val emptyQuery = listOf(
            Aggregates.match(filter),
            Aggregates.sort(Sorts.descending("qty")),
            skip(9)
        )
        assertTrue(collection.aggregate(emptyQuery).toList().isEmpty())
    }
}

// :replace-end:
