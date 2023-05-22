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
        val docs =
            // :snippet-start: countries
            collection.distinct(Movie::countries.name, Filters.empty(), String::class.java)
            // :snippet-end:
                .toList()
        assertEquals(4, docs.size)
    }

    @Test
    fun distinctAwardsTest() = runBlocking {
        val docs =
            // :snippet-start: awards
            collection.distinct(
                "${Movie::awards.name}.${Movie.Awards::wins.name}", Filters.empty(), Integer::class.java
            )
            // :snippet-end:
                .toList()
        assertEquals(3, docs.size)
    }

    @Test
    fun distinctFilterTest() = runBlocking {
        val docs =
            // :snippet-start: filter
            collection.distinct(
                Movie::type.name, Filters.eq(Movie::languages.name, "French"), String::class.java
            )
            // :snippet-end:
                .toList()
        assertEquals(1, docs.size)
    }
}