
import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.result.DeleteResult
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DeleteUsageExamplesTest {
    data class Movie(@BsonId val id: Int, val title: String, val year: Int, val imdb: IMDB){
        data class IMDB(val rating: Double)
    }

    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        val database = client.getDatabase("delete")
        val collection = database.getCollection<Movie>("movies")

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            runBlocking {
                val movies = listOf(
                    Movie(1, "The Princess Bride", 1987, Movie.IMDB(8.1)),
                    Movie(2, "The Shawshank Redemption", 1994, Movie.IMDB(9.3)),
                    Movie(3, "The Godfather", 1972, Movie.IMDB(9.2)),
                    Movie(4, "The Garbage Pail Kids Movie", 1987, Movie.IMDB(1.0)),
                )
                collection.insertMany(movies)
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
    fun delete() = runBlocking {
        // Delete Usage Example code tests
        val query = Filters.eq("title", "The Garbage Pail Kids Movie")

        try {
            val result: DeleteResult = collection.deleteOne(query)
            println("Deleted document count: " + result.deletedCount)
            assertEquals(1, result.deletedCount)
        } catch (me: MongoException) {
            System.err.println("Unable to delete due to an error: $me")
        }
        val result2: DeleteResult = collection.deleteOne(query)
        assertEquals(0, result2.deletedCount)
        }



    @Test
    fun deleteManyTest() = runBlocking {
        val query = Filters.lt("imdb.rating", 1.9)
        try {
            val result = collection.deleteMany(query)
            println("Deleted document count: " + result.deletedCount)
            assertEquals(1, result.deletedCount)
        } catch (me: MongoException) {
            System.err.println("Unable to delete due to an error: $me")
        }
        val result2: DeleteResult = collection.deleteOne(query)
        assertEquals(0, result2.deletedCount)
}
}