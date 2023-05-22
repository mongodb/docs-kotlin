
import com.mongodb.MongoBulkWriteException
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

// :snippet-start: bulk-data-model
data class Fruit(@BsonId val id: Int, val type: String)
// :snippet-end:

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BulkTest {
    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        val database = client.getDatabase("sample_db")
        val collection = database.getCollection<Fruit>("fruit")

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            runBlocking {
                val sampleDocuments = listOf(
                    Fruit(1, "apple"),
                    Fruit(2, "banana")
                )
                collection.insertMany(sampleDocuments)
            }
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            runBlocking {
                collection.drop()
                client.close()
            }
        }
    }

    @Test
    fun insertOperationTest() = runBlocking {
        // :snippet-start: insert-one
        val orange = InsertOneModel(Fruit(3, "orange"))
        val kiwi = InsertOneModel(Fruit(4, "kiwi"))
        // :snippet-end:
        // :snippet-start: bulk-write-exception
        val cherry = InsertOneModel(Fruit(1, "cherry"))
        val mango = InsertOneModel(Fruit(5, "mango"))
        try {
            val bulkOperations = listOf(cherry, mango)
            val bulkWrite = collection.bulkWrite(bulkOperations)
            assertFalse(bulkWrite.wasAcknowledged()) // :remove:
        } catch (e: MongoBulkWriteException) {
            println("A MongoBulkWriteException occurred with the following message: " + e.message)
        }
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            Fruit(1, "apple"),
            Fruit(2, "banana")
        )
        assertEquals(expected, collection.find().toList())
    }

    @Test
    fun replaceOneTest() = runBlocking {
        // :snippet-start: replace-one
        val filter = Filters.eq("_id", 1)
        val insert = Fruit(1, "blueberry")
        val doc = ReplaceOneModel(filter, insert)
        // :snippet-end:
        // Junit test for the above code
        val insertTest = collection.bulkWrite(listOf(doc))
        assertTrue(insertTest.wasAcknowledged())
    }

    @Test
    fun updateOneTest() = runBlocking {
        // :snippet-start: update-one
        val filter = Filters.eq("_id", 2)
        val update = Updates.set(Fruit::type.name, "peach")
        val doc = UpdateOneModel<Fruit>(filter, update)
        // :snippet-end:
        // Junit test for the above code
        val updateTest = collection.bulkWrite(listOf(doc))
        assertTrue(updateTest.wasAcknowledged())
    }

    @Test
    fun deleteOneTest() = runBlocking {
        // :snippet-start: delete
        val filter = Filters.eq("_id", 1)
        val doc = DeleteOneModel<Fruit>(filter)
        // :snippet-end:
        // Junit test for the above code
        val deleteTest = collection.bulkWrite(listOf(doc))
        assertTrue(deleteTest.wasAcknowledged())
        assertTrue(collection.find(filter).toList().isEmpty())
    }

    @Test
    fun orderOfOperationsTest() = runBlocking {
        // :snippet-start: ordered
        val insertDragonFruit = InsertOneModel(Fruit(3, "dragon fruit"))
        val replaceKumquat = ReplaceOneModel(
            Filters.eq("_id", 1),
            Fruit(1, "kumquat")
        )
        val updateDragonFruit = UpdateOneModel<Fruit>(
                Filters.eq("_id", 3),
                Updates.set(Fruit::type.name, "pitahaya")
            )
        val deleteApple = DeleteManyModel<Fruit>(Filters.eq(Fruit::type.name, "apple"))

        val bulkOperations = listOf(
            insertDragonFruit,
            replaceKumquat,
            updateDragonFruit,
            deleteApple
        )

        val update = collection.bulkWrite(bulkOperations)
        // :snippet-end:
        // Junit test for the above code
        assertTrue(update.wasAcknowledged())
    }

    @Test
    fun unorderedExecutionTest() = runBlocking {
        val insertWatermelon = InsertOneModel(Fruit(3, "watermelon"))
        val replaceCantaloupe = ReplaceOneModel(
            Filters.eq("_id", 1),
            Fruit(1, "cantaloupe")
        )
        val updateWatermelonToHoneydew = UpdateOneModel<Fruit>(
                Filters.eq("_id", 3),
                Updates.set(Fruit::type.name, "honeydew")
            )
        val deleteBanana = DeleteManyModel<Fruit>(Filters.eq(Fruit::type.name, "banana"))

        val bulkOperations = listOf(
            insertWatermelon,
            replaceCantaloupe,
            updateWatermelonToHoneydew,
            deleteBanana
        )
        // :snippet-start: unordered
        val options = BulkWriteOptions().ordered(false)
        val unorderedUpdate = collection.bulkWrite(bulkOperations, options)
        // :snippet-end:
        // Junit test for the above code
        assertTrue(unorderedUpdate.wasAcknowledged())
    }
}