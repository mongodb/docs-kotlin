
import com.mongodb.client.model.*
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


// :snippet-start: retrieve-data-model
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

        if (id != other.id) return false
        if (!qty.contentEquals(other.qty)) return false
        if (color != other.color) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + qty.contentHashCode()
        result = 31 * result + color.hashCode()
        return result
    }
}


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UpdateArraysTest {

    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        val database = client.getDatabase("paint_store")
        val collection = database.getCollection<PaintOrder>("paint_order")


        @BeforeAll
        @JvmStatic
        private fun beforeAll() {
            runBlocking {
                val paintOrders =
                    PaintOrder(1, intArrayOf(8, 12, 18), "green")
                collection.insertOne(paintOrders)
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
    fun updateTest() = runBlocking {
        // :snippet-start: specify-update
        val filter = Filters.eq("_id", 1)
        val update = Updates.push("qty", 17)
        val options = FindOneAndUpdateOptions()
            .returnDocument(ReturnDocument.AFTER)
        val result = collection.findOneAndUpdate(filter, update, options)
        collection.find().collect { println(it) }
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            PaintOrder(1, intArrayOf(8, 12, 18, 17), "green"))
        assertEquals(expected, collection.find().toList())
    }

    @Test
    fun updateManyTest() = runBlocking {
        // :snippet-start: update-many
        val filter = Filters.eq("qty", 18)
        val update = Updates.inc("qty.$", -3)
        val options = FindOneAndUpdateOptions()
            .returnDocument(ReturnDocument.AFTER)
        val result = collection.findOneAndUpdate(filter, update, options)
        collection.find().collect { println(it) }
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
        val update = Updates.mul("qty.$[]", 2)
        val options = FindOneAndUpdateOptions()
            .returnDocument(ReturnDocument.AFTER)
        val result = collection.findOneAndUpdate(filter, update, options)
        collection.find().collect { println(it) }
    // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            PaintOrder(1, intArrayOf(16, 24, 36), "green"))
        assertEquals(expected, collection.find().toList())
    }
    @Test
    fun matchMultipleTest() = runBlocking {
        // :snippet-start: match
        val filter = Filters.eq("_id", 1)
        val smallerFilter = Filters.lt("smaller", 15)
        val options = FindOneAndUpdateOptions()
            .returnDocument(ReturnDocument.AFTER)
            .arrayFilters(listOf(smallerFilter))
        val update = Updates.inc("qty.$[smaller]", 5)
        val result = collection.findOneAndUpdate(filter, update, options)
        collection.find().collect { println(it) }
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            PaintOrder(1, intArrayOf(13, 17, 18), "green"))
        assertEquals(expected, collection.find().toList())
    }
}
