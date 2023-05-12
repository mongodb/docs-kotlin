
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.bitsAllSet
import com.mongodb.client.model.Filters.geoWithin
import com.mongodb.client.model.geojson.Polygon
import com.mongodb.client.model.geojson.Position
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class FiltersBuildersTest {
    // :snippet-start: paint-order-data-class
    data class PaintOrder(
        @BsonId val id: ObjectId? = null,
        val qty: Int,
        val color: String,
        val vendors: List<String> = mutableListOf()
    )
    // :snippet-end:

    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        val database = client.getDatabase("marketing")
        val collection = database.getCollection<PaintOrder>("users")

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            runBlocking {
                val paintOrders = listOf(
                    PaintOrder(ObjectId(), 5, "red"),
                    PaintOrder(ObjectId(), 10, "purple"),
                    PaintOrder(ObjectId(), 3, "yellow"),
                    PaintOrder(ObjectId(), 8, "blue"),
                    PaintOrder(ObjectId(), 1, "pink", listOf("A", "B", "C", "D"))
                )
                collection.insertMany(paintOrders)
            }
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            runBlocking {
                database.drop()
                client.close()
            }
        }
    }

    @Test
    fun equalComparisonTest() = runBlocking {
        // :snippet-start: equalComparison
        val equalComparison = Filters.eq("qty", 5)
        val resultsFlow = collection.find(equalComparison)
        resultsFlow.collect { println(it) }
        // :snippet-end:
        assertEquals(1, resultsFlow.toList().size)
    }

    @Test
    fun gteComparisonTest() = runBlocking {
        // :snippet-start: gteComparison
        val gteComparison = Filters.gte("qty", 10)
        val resultsFlow = collection.find(gteComparison)
        resultsFlow.collect { println(it) }
        // :snippet-end:
        assertEquals(1, resultsFlow.toList().size)
    }

    @Test
    fun orComparisonTest() = runBlocking {
        // :snippet-start: orComparison
        val orComparison = Filters.or(
            Filters.gt("qty", 8),
            Filters.eq("color", "pink")
        )
        val resultsFlow = collection.find(orComparison)
        resultsFlow.collect { println(it) }
        // :snippet-end:
        assertEquals(2, resultsFlow.toList().size)
    }

    @Test
    fun emptyComparisonTest() = runBlocking {
        // :snippet-start: emptyComparison
        val emptyComparison = Filters.empty()
        val resultsFlow = collection.find(emptyComparison)
        resultsFlow.collect { println(it) }
        // :snippet-end:
        assertEquals(5, resultsFlow.toList().size)
    }

    // TODO: failing..think need to create a search index?
    @Test
    fun allComparisonTest() = runBlocking {
        // :snippet-start: allComparison
        val search = listOf("A", "D")
        val allComparison = Filters.all("vendor", search)
        val resultsFlow = collection.find(allComparison)
        resultsFlow.collect { println(it) }
        // :snippet-end:
        assertEquals(1, resultsFlow.toList().size)
    }

    @Test
    fun existsComparisonTest() = runBlocking {
        // :snippet-start: existsComparison
        val existsComparison = Filters.and(Filters.exists("qty"), Filters.nin("qty", 5, 8))
        val resultsFlow = collection.find(existsComparison)
        resultsFlow.collect { println(it) }
        // :snippet-end:
        assertEquals(3, resultsFlow.toList().size)
    }

    @Test
    fun regexComparisonTest() = runBlocking {
        // :snippet-start: regexComparison
        val regexComparison = Filters.regex("color", "^p")
        val resultsFlow = collection.find(regexComparison)
        resultsFlow.collect { println(it) }
        // :snippet-end:
        assertEquals(2, resultsFlow.toList().size)
    }

    @Test
    fun bitsComparisonTest() = runBlocking {
        // :snippet-start: bitsComparison
        val bitsComparison = bitsAllSet("a", 34)
        val resultsFlow = collection.find(bitsComparison)
        resultsFlow.collect { println(it) }
        // :snippet-end:
        // TODO: i really don't understand this operator currently
    }

    // TODO: nasty error...review how to do geo queries
    @Test
    fun geoWithinComparisonTest() = runBlocking {
        // :snippet-start: geoWithinComparison
        data class Store(
            @BsonId val id: ObjectId? = null,
            val name: String,
            val coordinates: Position
        )
        // :remove-start:
        val collection = database.getCollection<Store>("stores")
        collection.insertMany(
            listOf(
                Store(
                    ObjectId(),
                    "Store 1",
                    Position(2.0, 2.0)
                )
            )
        )
        // :remove-end:

        val square = Polygon(listOf(
            Position(0.0, 0.0),
            Position(4.0, 0.0),
            Position(4.0, 4.0),
            Position(0.0, 4.0),
            Position(0.0, 0.0)))

        val geoWithinComparison = geoWithin("coordinates", square)
        val resultsFlow = collection.find(geoWithinComparison)
        resultsFlow.collect { println(it) }
        // :snippet-end:
        assertEquals(1, resultsFlow.toList().size)
    }
}