import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class DistinctTest {

    // :snippet-start: example-data-class
    data class Movie(
        val type: String,
        val languages: List<String>,
        val countries: List<String>,
        val awards: Awards){
            data class Awards(val wins: Int)
        }
    // :snippet-end:

    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        val database = client.getDatabase("sample_mflix")
        val collection = database.getCollection<Movie>("movies")

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            runBlocking {
                val movies = listOf(
                    Movie("movie", listOf("English", "French"), listOf("USA", "France"), Movie.Awards(1)),
                    Movie("movie", listOf("English", "German"), listOf("USA", "Germany"), Movie.Awards(2)),
                    Movie("movie", listOf("English"), listOf("USA", "Australia"), Movie.Awards(3)),
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
    fun distinctCountriesTest() = runBlocking {
        val distinct =
            // :snippet-start: countries
            collection.distinct<String>(Movie::countries.name)
            // :snippet-end:
                .toList()
        assertEquals(4, distinct.size)
    }

    @Test
    fun distinctAwardsTest() = runBlocking {
        val distinct =
            // :snippet-start: awards
            collection.distinct<Int>("${Movie::awards.name}.${Movie.Awards::wins.name}")
            // :snippet-end:
                .toList()
        assertEquals(3, distinct.size)
    }

    @Test
    fun distinctFilterTest() = runBlocking {
        val distinct =
            // :snippet-start: filter
            collection.distinct<String>(Movie::type.name, Filters.eq(Movie::languages.name, "French"))
            // :snippet-end:
                .toList()
        assertEquals(1, distinct.size)
    }
}