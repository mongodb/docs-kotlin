
import com.mongodb.client.model.*
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import org.bson.BsonDocument
import org.bson.codecs.pojo.annotations.BsonId
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CountTest {
    data class Movie(@BsonId val id: Int, val title: String)

    companion object {
        private val dotenv = dotenv()
        private val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        private val database = client.getDatabase("sample_mflix")
        val collection = database.getCollection<Movie>("movies")

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            runBlocking {
                val movies = listOf(
                    Movie(1, "The Shawshank Redemption"),
                    Movie(2, "The Godfather"),
                    Movie(3, "The Godfather: Part II")
                )
                collection.insertMany(movies)
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
    fun hintTest() = runBlocking {
        // :snippet-start: hint
        val options = CountOptions().hintString("_id_")
        val numDocuments = collection.countDocuments(BsonDocument(), options)
        // :snippet-end:
        assertEquals(3, numDocuments)
    }
}
