

import com.mongodb.MongoBulkWriteException
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.BsonObjectId
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.*

// :replace-start: {
//    "terms": {
//       "PaintOrder2": "PaintOrder",
//       "paintOrder2": "paintOrder",
//       "collection2": "collection"
//    }
// }

// :snippet-start: retrieve-data-model

data class PaintOrder(
    @BsonId val id: Int,
    val qty: Int,
    val color: String
)
// :snippet-end:

// :snippet-start: retrieve-data-model-2
data class PaintOrder2(
    @BsonId val id: ObjectId? = null,
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
        val collection2 = database.getCollection<PaintOrder2>("paint_order2")


        @AfterAll
        @JvmStatic
        private fun afterAll() {
            runBlocking {
                collection.drop()
                collection2.drop()
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
        try {
            val result = collection.insertMany(paintOrders)
            result.insertedIds.values
                .forEach { doc -> doc.asInt32().value }
            println("Inserted a document with the following ids: ${result.insertedIds}")
        } catch(e: MongoBulkWriteException){
            e.writeResult.inserts
                .forEach { doc -> doc.id.asInt32().value }
            println(
                "A MongoBulkWriteException occurred, but there are " +
                "successfully processed documents with the following ids: ${e.writeResult}"
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
        val paintOrder = PaintOrder2(qty = 5, color = "red")
        val result = collection2.insertOne(paintOrder)

        val insertedId = result.insertedId?.asObjectId()?.value

        println("Inserted a document with the following id: $insertedId")
        // :snippet-end:
        // Junit test for the above code
        val filter = Filters.empty()
        val testResults = listOf(Aggregates.match(filter))
        collection2.aggregate<Document>(testResults).toList().forEach { println(it.toJson()) }
        val expected = listOf(
            Document("_id", insertedId).append("qty", 5).append("color", "red")
        )
        assertEquals(expected, collection2.aggregate<Document>(testResults).toList())
    }

    @Test
    fun insertManyTest() = runBlocking {
        // :snippet-start: insert-many
        val paintOrder2 = listOf(
            PaintOrder2(qty = 5, color = "red"),
            PaintOrder2(qty = 10, color = "purple"))
        val result = collection2.insertMany(paintOrder2)

        result.insertedIds.values
            .forEach { doc -> doc.asObjectId().value }

        println("Inserted a document with the following ids: ${result.insertedIds}")
        // :snippet-end:
        // Junit test for the above code
        assertTrue(result.wasAcknowledged())
    }
}
// :replace-end:

