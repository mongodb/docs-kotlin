
import com.mongodb.client.model.*
import com.mongodb.client.model.Sorts.*
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

// :snippet-start: sort-data-model
data class FoodOrder(
    @BsonId val id: Int,
    val letter: String,
    val food: String
)
// :snippet-end:

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SortTest {

    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        val database = client.getDatabase("paint_store")
        val collection = database.getCollection<FoodOrder>("paint_order")

        @BeforeAll
        @JvmStatic
        private fun beforeAll() {
            runBlocking {
                val foodOrders = listOf(
                    FoodOrder(1, "c", "coffee with milk"),
                    FoodOrder(3, "a", "maple syrup"),
                    FoodOrder(4, "b", "coffee with sugar"),
                    FoodOrder(5, "a", "milk and cookies"),
                    FoodOrder(2, "a", "donuts and coffee"),
                    FoodOrder(6, "c", "maple donut")
                )
                collection.insertMany(foodOrders)
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
    fun basicSortTest() = runBlocking {
        // :snippet-start: basic-sort
        collection.find().sort(Sorts.ascending("_id"))
        // :snippet-end:
        // Junit test for the above code
        val filter = Filters.empty()
        val expected = listOf(
            FoodOrder(1, "c", "coffee with milk"),
            FoodOrder(2, "a", "donuts and coffee"),
            FoodOrder(3, "a", "maple syrup"),
            FoodOrder(4, "b", "coffee with sugar"),
            FoodOrder(5, "a", "milk and cookies"),
            FoodOrder(6, "c", "maple donut")

        )
        assertEquals(expected, collection.find(filter).sort((ascending("_id"))).toList() )
    }

    @Test
    fun aggregationSortTest() = runBlocking {
        // :snippet-start: aggregation-sort
        val resultsFlow = collection.aggregate(listOf(
            Aggregates.sort(Sorts.ascending("_id"))
        ))
        resultsFlow.collect { println(it) }
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            FoodOrder(1, "c", "coffee with milk"),
            FoodOrder(2, "a", "donuts and coffee"),
            FoodOrder(3, "a", "maple syrup"),
            FoodOrder(4, "b", "coffee with sugar"),
            FoodOrder(5, "a", "milk and cookies"),
            FoodOrder(6, "c", "maple donut")
        )
        assertEquals(expected, resultsFlow.toList())
    }

    @Test
    fun ascendingSortTest() = runBlocking {
        // :snippet-start: ascending-sort
        val resultsFlow = collection.find()
            .sort(Sorts.ascending("_id"))
        resultsFlow.collect { println(it) }
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            FoodOrder(1, "c", "coffee with milk"),
            FoodOrder(2, "a", "donuts and coffee"),
            FoodOrder(3, "a", "maple syrup"),
            FoodOrder(4, "b", "coffee with sugar"),
            FoodOrder(5, "a", "milk and cookies"),
            FoodOrder(6, "c", "maple donut")
        )
        assertEquals(expected, resultsFlow.toList())
    }

    @Test
    fun descendingSortTest() = runBlocking {
        val results =
        // :snippet-start: descending-sort
        collection.find().sort(Sorts.descending("_id"))
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            FoodOrder(6, "c", "maple donut"),
            FoodOrder(5, "a", "milk and cookies"),
            FoodOrder(4, "b", "coffee with sugar"),
            FoodOrder(3, "a", "maple syrup"),
            FoodOrder(2, "a", "donuts and coffee"),
            FoodOrder(1, "c", "coffee with milk")
            )
        assertEquals(expected, results.toList())
    }

    @Test
    fun handleTiesTest() = runBlocking {
        // :snippet-start: handle-ties-1
        collection.find().sort(Sorts.ascending("letter"))
        // :snippet-end:
        val results =
        // :snippet-start: handle-ties-2
        collection.find().sort(Sorts.ascending("letter", "_id"))
        // :snippet-end:
        // Junit test for the above code
        val filter = Filters.empty()
        val expected = listOf(
            FoodOrder(2, "a", "donuts and coffee"),
            FoodOrder(3, "a", "maple syrup"),
            FoodOrder(5, "a", "milk and cookies"),
            FoodOrder(4, "b", "coffee with sugar"),
            FoodOrder(1, "c", "coffee with milk"),
            FoodOrder(6, "c", "maple donut")
        )
        assertEquals(expected, results.toList() )
    }

    @Test
    fun combineSortTest() = runBlocking {
        // :snippet-start: combine-sort
        val orderBySort = orderBy(
            Sorts.descending("letter"), ascending("_id")
        )
        val results = collection.find().sort(orderBySort)
        // :snippet-end:
        // Junit test for the above code
        val filter = Filters.empty()
        val expected = listOf(
            FoodOrder(1, "c", "coffee with milk"),
            FoodOrder(6, "c", "maple donut"),
            FoodOrder(4, "b", "coffee with sugar"),
            FoodOrder(2, "a", "donuts and coffee"),
            FoodOrder(3, "a", "maple syrup"),
            FoodOrder(5, "a", "milk and cookies")
        )
        assertEquals(expected, results.toList() )
    }

    @Test
    fun textSearchTest() = runBlocking {
        data class FoodOrderScore(
           @BsonId val id: Int,
            val letter: String,
            val food: String,
            val score: Double
        )
        // :snippet-start: text-search
        collection.createIndex(Indexes.text("food"))
        val metaTextScoreSort = Sorts.metaTextScore("score")
        val metaTextScoreProj = Projections.metaTextScore("score")
        val searchTerm = "maple donut"
        val searchQuery = Filters.text(searchTerm)

        val results = collection.find<FoodOrderScore>(searchQuery)
            .projection(metaTextScoreProj)
            .sort(metaTextScoreSort)

        results.collect { println(it) }
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            FoodOrderScore(6, "c", "maple donut",1.5),
            FoodOrderScore(2, "a", "donuts and coffee", 0.75),
            FoodOrderScore(3, "a", "maple syrup", 0.75)
        )
        assertEquals(expected, results.toList())
    }
}