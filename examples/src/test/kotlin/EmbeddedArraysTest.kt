
import com.mongodb.client.model.*
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UpdateArraysTest {
    // :snippet-start: array-data-model
    data class PaintOrder(
        @BsonId val id: Int,
        val qty: IntArray,
        val color: String
    )
    // :snippet-end:

    {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as PaintOrder
            if (!qty.contentEquals(other.qty)) return false
            return true
        }

        override fun hashCode(): Int {
            return qty.contentHashCode()
        }
    }

    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        val database = client.getDatabase("paint_store")
        val collection = database.getCollection<PaintOrder>("paint_order")

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
            val paintOrders =
                PaintOrder(1, intArrayOf(8, 12, 18), "green")
            collection.insertOne(paintOrders)
        }
    }

    @AfterEach
    fun afterEach() {
        runBlocking {
            collection.drop()
        }
    }

    @Test
    fun updateTest() = runBlocking {
        // :snippet-start: specify-update
        val filter = Filters.eq("_id", 1)
        val update = Updates.push(PaintOrder::qty.name, 17)
        val options = FindOneAndUpdateOptions()
            .returnDocument(ReturnDocument.AFTER)
        val result = collection.findOneAndUpdate(filter, update, options)
        print(result)
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            PaintOrder(1, intArrayOf(8, 12, 18, 17), "green"))
        assertEquals(expected, collection.find(filter).toList())
    }

    @Test
    fun updateManyTest() = runBlocking {
        // :snippet-start: update-many
        val filter = Filters.eq(PaintOrder::qty.name, 18)
        val update = Updates.inc("${PaintOrder::qty.name}.$", -3)
        val options = FindOneAndUpdateOptions()
            .returnDocument(ReturnDocument.AFTER)
        val result = collection.findOneAndUpdate(filter, update, options)
        print(result)
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            PaintOrder(1, intArrayOf(8, 12, 15), "green"))
        assertEquals(expected, collection.find().toList())
    }

    @Test
    fun updateAllTest() = runBlocking {
        // :snippet-start: update-all
        val filter = Filters.eq("_id", 1)
        val update = Updates.mul("${PaintOrder::qty.name}.$[]", 2)
        val options = FindOneAndUpdateOptions()
            .returnDocument(ReturnDocument.AFTER)
        val result = collection.findOneAndUpdate(filter, update, options)
        println(result)
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            PaintOrder(1, intArrayOf(16, 24, 36), "green"))
        assertEquals(expected, collection.find(filter).toList())
    }
    
    @Test
    fun matchMultipleTest() = runBlocking {
        // :snippet-start: match
        val filter = Filters.eq("_id", 1)
        val smallerFilter = Filters.lt("smaller", 15)
        val options = FindOneAndUpdateOptions()
            .returnDocument(ReturnDocument.AFTER)
            .arrayFilters(listOf(smallerFilter))
        val update = Updates.inc("${PaintOrder::qty.name}.$[smaller]", 5)
        val result = collection.findOneAndUpdate(filter, update, options)
        println(result)
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            PaintOrder(1, intArrayOf(13, 17, 18), "green"))
        assertEquals(expected, collection.find(filter).toList())
    }
}
