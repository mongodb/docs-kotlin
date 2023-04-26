import com.mongodb.client.model.Sorts.ascending
import com.mongodb.client.model.Sorts.descending
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class LimitTest {

    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        val database = client.getDatabase("library")
        val collection = database.getCollection<Document>("books")

        @BeforeAll
        @JvmStatic
        private fun beforeAll() {
            runBlocking {
                // :snippet-start: insert-many
                val books = listOf(
                    Document().append("_id", 1).append("title", "The Brothers Karamazov").append("length", 824)
                        .append("author", "Dostoyevsky"),
                    Document().append("_id", 2)
                        .append("title", "Les Misérables").append("length", 1462).append("author", "Hugo"),
                    Document().append("_id", 3)
                        .append("title", "Atlas Shrugged").append("length", 1088).append("author", "Rand"),
                    Document().append("_id", 4)
                        .append("title", "Infinite Jest").append("length", 1104).append("author", "Wallace"),
                    Document().append("_id", 5)
                        .append("title", "Cryptonomicon").append("length", 918).append("author", "Stephenson"),
                    Document().append("_id", 6)
                        .append("title", "A Dance with Dragons").append("length", 1104)
                        .append("author", "Martin")
                )
                collection.insertMany(books)
                // :snippet-end:
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
    fun specifyLimitTest() = runBlocking {
        // :snippet-start: specify-limit
        collection.find()
            .sort(descending("length"))
            .limit(3)
            .skip(3)
            .toList().forEach { println(it) }
        // :snippet-end:
        // Junit test for the above code
        val expectation = listOf(
            Document().append("_id", 2)
                .append("title", "Les Misérables").append("length", 1462).append("author", "Hugo"),
            Document().append("_id", 6)
                .append("title", "A Dance with Dragons").append("length", 1104)
                .append("author", "Martin"),
            Document().append("_id", 4)
                .append("title", "Infinite Jest").append("length", 1104).append("author", "Wallace")
        )
        assertEquals(expectation, collection.find().sort(descending("length")).limit(3).toList())
    }

    @Test
    fun combineSkipLimitTest() = runBlocking {
        // :snippet-start: skip-limit
        collection.find()
            .sort(ascending("length"))
            .limit(3)
            .skip(3)
            .toList().forEach { println(it) }
        // :snippet-end:
        // Junit test for the above code
        val expectation = listOf(
            Document().append("_id", 3)
                .append("title", "Atlas Shrugged").append("length", 1088).append("author", "Rand"),
            Document().append("_id", 5)
                .append("title", "Cryptonomicon").append("length", 918).append("author", "Stephenson"),
            Document().append("_id", 1).append("title", "The Brothers Karamazov").append("length", 824)
                .append("author", "Dostoyevsky")
        )
        assertEquals(expectation, collection.find().sort(descending("length")).limit(3).skip(3).toList())
    }


}