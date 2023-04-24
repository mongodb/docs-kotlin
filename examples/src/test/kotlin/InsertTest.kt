import com.mongodb.MongoBulkWriteException
import com.mongodb.client.model.Accumulators
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.collections.ArrayList
import kotlin.test.*


// :snippet-start: retrieve-data-model
data class PaintOrder(
    @BsonId val id: Int? = null,
    val qty: Int,
    val color: String
)
// :snippet-end:

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class InsertTest {

    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        val database = client.getDatabase("paint_store")
        val collection = database.getCollection<PaintOrder>("paint_order")

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
    fun insertManyErrorTest() = runBlocking {
        val paintOrders = listOf(
            PaintOrder(3,  5, "red"),
            PaintOrder(4, 10, "purple"),
            PaintOrder(3, 3, "yellow"),
            PaintOrder(6, 8, "blue")
        )

        // :snippet-start: insert-many-error
        val insertedIds = ArrayList<Int>()
        try {
            val result = collection.insertMany(paintOrders)
            result.insertedIds.values
                .forEach { doc -> insertedIds.add(doc.asInt32().value) }
            println("Inserted a document with the following ids: $insertedIds")
        } catch(exception: MongoBulkWriteException){
            exception.writeResult.inserts
                .forEach { doc -> insertedIds.add(doc.id.asInt32().value) }
            println(
                "A MongoBulkWriteException occurred, but there are " +
                "successfully processed documents with the following ids: $insertedIds"
            )
            println(collection.find().toList().forEach { println(it) })
        }
        // :snippet-end:
        //Junit test for the above code
        val filter = Filters.empty()
        val testResults = listOf(Aggregates.match(filter))
        collection.aggregate<Document>(testResults).toList().forEach { println(it.toJson()) }
        val expected = listOf(
            Document("_id", 3).append("qty", 5).append("color", "red"),
            Document("_id", 4).append("qty", 10).append("color", "purple")
        )
        assertEquals(expected, collection.aggregate<Document>(testResults).toList())
    }


    @Test
    fun insertOneTest() = runBlocking {
        // :snippet-start: insert-one
        val paintOrder = PaintOrder(qty = 5, color = "red")
        val result = collection.insertOne(paintOrder)

        val insertedId = result.insertedId?.asObjectId()?.value

        println("Inserted a document with the following id: $insertedId")
        // :snippet-end:
        // Junit test for the above code
        val filter = Filters.empty()
        val testResults = listOf(Aggregates.match(filter))
        collection.aggregate<Document>(testResults).toList().forEach { println(it.toJson()) }
        val expected = listOf(
            Document("_id", insertedId).append("qty", 5).append("color", "red")
        )
        assertEquals(expected, collection.aggregate<Document>(testResults).toList())
    }

    @Test
    fun insertManyTest() = runBlocking {
        // :snippet-start: insert-many
        val paintOrder = listOf(
            PaintOrder(qty = 5, color = "red"),
            PaintOrder(qty = 10, color = "purple"))
        val result = collection.insertMany(paintOrder)

        val insertedIds = ArrayList<ObjectId>()
        result.insertedIds.values
            .forEach { doc -> insertedIds.add(doc.asObjectId().value) }

        println("Inserted a document with the following ids: $insertedIds")
        // :snippet-end:
        // Junit test for the above code
        assertTrue(result.wasAcknowledged())
    }
}

