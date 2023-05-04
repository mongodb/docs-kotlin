
import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ValidationOptions
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DatabaseCollectionsTest {
    // :snippet-start: test-data-class
    data class TestDataClass(
        @BsonId val id: ObjectId = ObjectId(),
    )
    // :snippet-end:
    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        // :snippet-start: access-database
        val database = client.getDatabase("testDatabase")
        // :snippet-end:
        // :snippet-start: access-collection
        val collection = database.getCollection<TestDataClass>("testCollection")
        // :snippet-end:


        @JvmStatic
        @AfterAll
        fun afterAll(): Unit = runBlocking {
            database.drop()
            client.close()
        }
    }

    @Test
    fun createExampleCollectionTest() = runBlocking {
        // :snippet-start: create-collection
        database.createCollection("exampleCollection")
        // :snippet-end:
    }

    @Test
    fun listCollectionTest() = runBlocking {
        // :snippet-start: drop-collections
        val collection = database.getCollection<TestDataClass>("bass")
        collection.drop()
        // :snippet-end:
        // :snippet-start: get-collections
        val collectionList = database.listCollectionNames()
        println(collectionList)
        // :snippet-end:
    }

    @Test
    fun validationTest() = runBlocking {
        // :snippet-start: validation
        val collOptions: ValidationOptions = ValidationOptions().validator(
            Filters.or(Filters.exists("commander"), Filters.exists("first officer"))
        )
        database.createCollection(
            "ships",
            CreateCollectionOptions().validationOptions(collOptions)
        )
        // :snippet-end:
    }
}
