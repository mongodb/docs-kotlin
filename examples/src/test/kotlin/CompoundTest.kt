
import com.mongodb.client.model.*
import com.mongodb.client.model.Sorts.*
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.test.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CompoundOperationsTest {
    // :snippet-start: compound-data-model
    data class FoodOrder(
        @BsonId val id: Int,
        val food: String,
        val color: String
    )
    // :snippet-end:
    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        val database = client.getDatabase("compound_operations")
        val collection = database.getCollection<FoodOrder>("example")

        @AfterAll
        @JvmStatic
        fun afterAll() {
            runBlocking {
                client.close()
            }
        }
    }
    @AfterEach
    fun afterEach() {
        runBlocking {
            collection.drop()
        }
    }

    @Test
    fun findOneUpdateTest() = runBlocking {
        val foodOrders = FoodOrder(1, "donut", "green")
        collection.insertOne(foodOrders)
        // :snippet-start: find-one-update
        data class Results(val food: String, val color: String)

        val projection = Projections.excludeId()
        val filter = Filters.eq(FoodOrder::color.name, "green")
        val update = Updates.set(FoodOrder::food.name, "pizza")
        val options = FindOneAndUpdateOptions()
            .projection(projection)
            .upsert(true)
            .maxTime(5, TimeUnit.SECONDS)
        /* The result variable contains your document in the
            state before your update operation is performed. */
        val resultsCollection = database.getCollection<Results>("example")
        val result = resultsCollection.findOneAndUpdate(filter, update, options)
        println(result)
        // :snippet-end:
        // Junit test for the above code
        val actual = collection.find(filter)
        val expected =
            listOf(FoodOrder(1, "pizza", "green"))
        assertEquals(expected, actual.toList())
    }

    @Test
    fun findOneReplaceTest() = runBlocking {
        val foodOrders = FoodOrder(1, "pizza", "green")
        collection.insertOne(foodOrders)
        // :snippet-start: find-one-replace
        data class Music(
            @BsonId val id: Int,
            val music: String,
            val color: String
        )

        val filter = Filters.eq(FoodOrder::color.name, "green")
        val replace = Music(1, "classical", "green")
        val options = FindOneAndReplaceOptions()
            .returnDocument(ReturnDocument.AFTER)
        val musicCollection = database.getCollection<Music>("example")
        val result = musicCollection.findOneAndReplace(filter, replace, options)
        println(result)
        // :snippet-end:
        val expectedFilter = Filters.eq("_id", 1)
        assertEquals(replace, musicCollection.find(expectedFilter).first())
    }

    @Test
    fun findOneDeleteTest() = runBlocking {
        val foodOrders = listOf(
            FoodOrder(1, "pizza", "green"),
            FoodOrder(2, "pear", "yellow")
        )
        collection.insertMany(foodOrders)
        // :snippet-start: find-one-delete
        val sort = descending("_id")
        val filter = Filters.empty()
        val options = FindOneAndDeleteOptions().sort(sort)
        val result = collection.findOneAndDelete(filter, options)
        // Returns the deleted document
        println(result)
        // :snippet-end:
        // Junit test for the above code
        val actual = collection.find(filter)
        val expected =
            listOf(FoodOrder(1, "pizza", "green"))
        assertEquals(expected, actual.toList())
    }
}