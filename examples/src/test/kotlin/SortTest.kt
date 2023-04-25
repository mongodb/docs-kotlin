import com.mongodb.client.model.*
import com.mongodb.client.model.Sorts.*
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.conversions.Bson
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.*

// :snippet-start: retrieve-data-model
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
                collection.deleteMany(Filters.empty())
                client.close()

            }
        }

    }


    @Test
    fun basicSortTest() = runBlocking {
        // :snippet-start: basic-sort
        collection.find().sort(ascending("_id"))
        // :snippet-end:
        // Junit test for the above code
        val filter = Filters.empty()
        val expected = listOf(
            FoodOrder(1, "c", "coffee with milk"),
            FoodOrder(2, "a", "donuts and coffee"),
            FoodOrder(3, "a", "maple syrup"),
            FoodOrder(4, "b", "coffee with sugar"),
            FoodOrder(5, "a", "milk and cookies"),
            FoodOrder(6, "c", "maple donut"),

        )
        assertEquals(expected, collection.find(filter).sort((ascending("_id"))).toList() )
    }

    @Test
    fun aggregationSortTest() = runBlocking {
        // :snippet-start: aggregation-sort
        collection.aggregate(listOf(Aggregates.sort(ascending("_id"))))
        // :snippet-end:
        // Junit test for the above code
        val filter = Filters.empty()
        val test = listOf(
            Aggregates.match(filter),
            Aggregates.sort(ascending("_id"))
        )
        collection.aggregate<Document>(test).toList().forEach { println(it.toJson()) }
        val expected = listOf(
            Document("_id", 1).append("letter", "c").append("food", "coffee with milk"),
            Document("_id", 2).append("letter", "a").append("food", "donuts and coffee"),
            Document("_id", 3).append("letter", "a").append("food", "maple syrup"),
            Document("_id", 4).append("letter", "b").append("food", "coffee with sugar"),
            Document("_id", 5).append("letter", "a").append("food", "milk and cookies"),
            Document("_id", 6).append("letter", "c").append("food", "maple donut")
        )
        assertEquals(expected, collection.aggregate<Document>(test).toList())
    }

    @Test
    fun ascendingSortTest() = runBlocking {
        // :snippet-start: ascending-sort
        val results: List<Document> = ArrayList()
        collection.find().sort(ascending("_id"))
        for (result in results) {
            println(result.toJson())
        }
        // :snippet-end:
        // Junit test for the above code
        val filter = Filters.empty()
        val test = listOf(
            Aggregates.match(filter),
            Aggregates.sort(ascending("_id"))
        )
        collection.aggregate<Document>(results).toList().forEach { println(it.toJson()) }
        val expected = listOf(
            Document("_id", 1).append("letter", "c").append("food", "coffee with milk"),
            Document("_id", 2).append("letter", "a").append("food", "donuts and coffee"),
            Document("_id", 3).append("letter", "a").append("food", "maple syrup"),
            Document("_id", 4).append("letter", "b").append("food", "coffee with sugar"),
            Document("_id", 5).append("letter", "a").append("food", "milk and cookies"),
            Document("_id", 6).append("letter", "c").append("food", "maple donut")
        )
        assertEquals(expected, collection.aggregate<Document>(test).toList())
    }

    @Test
    fun descendingSortTest() = runBlocking {
        // :snippet-start: descending-sort
        collection.find().sort(descending("_id"))
        // :snippet-end:
        // Junit test for the above code
        val filter = Filters.empty()
        val expected = listOf(
            FoodOrder(6, "c", "maple donut"),
            FoodOrder(5, "a", "milk and cookies"),
            FoodOrder(4, "b", "coffee with sugar"),
            FoodOrder(3, "a", "maple syrup"),
            FoodOrder(2, "a", "donuts and coffee"),
            FoodOrder(1, "c", "coffee with milk")
            )
        assertEquals(expected, collection.find(filter).sort((descending("_id"))).toList() )
    }

    @Test
    fun handleTiesTest() = runBlocking {
        // :snippet-start: handle-ties-1
        collection.find().sort(ascending("letter"))
        // :snippet-end:
        // :snippet-start: handle-ties-2
        collection.find().sort(ascending("letter", "_id"))
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
        assertEquals(expected, collection.find(filter).sort((ascending("letter", "_id"))).toList() )
    }

    @Test
    fun combineSortTest() = runBlocking {
        // :snippet-start: combine-sort
        val orderBySort: Bson = orderBy(
            descending("letter"), ascending("_id")
        )
        collection.find().sort(orderBySort)
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
        assertEquals(expected, collection.find(filter).sort(orderBySort).toList() )
    }

    @Test
    fun textSearchTest() = runBlocking {
        // :snippet-start: text-search
        val results: List<Document> = ArrayList()

        collection.createIndex(Indexes.text("food"))
        val metaTextScoreSort: Bson = Sorts.metaTextScore("score")
        val metaTextScoreProj: Bson = Projections.metaTextScore("score")
        val searchTerm = "maple donut"
        val searchQuery = Filters.text(searchTerm)

        collection.find(searchQuery)
            .projection(metaTextScoreProj)
            .sort(metaTextScoreSort)
        for (result in results) {
            println(result)
        }
        // :snippet-end:
        // Junit test for the above code
        val pipeline = listOf(
            Aggregates.match(searchQuery),
            Aggregates.project(metaTextScoreProj),
            Aggregates.sort(metaTextScoreSort)
        )
        collection.aggregate<Document>(pipeline).toList().forEach { println(it.toJson()) }
        val expected = listOf(
            Document("_id", 6).append("score", 1.5),
            Document("_id", 2).append("score", 0.75),
            Document("_id", 3).append("score", 0.75)
        )
        assertEquals(expected, collection.aggregate<Document>(pipeline).toList())
    }

}