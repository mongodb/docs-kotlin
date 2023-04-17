import com.mongodb.client.model.Filters
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.TextSearchOptions
import com.mongodb.kotlin.client.coroutine.MongoClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import kotlin.test.*
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

// :snippet-start: retrieve-data-model
data class Movies(
    @BsonId val id: Int,
    val title: String,
    val tags: List<String>
)
// :snippet-end:

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TextTest {

    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        val database = client.getDatabase("movies")
        val collection = database.getCollection<Movies>("fast_and_furious_movies")

        @BeforeAll
        @JvmStatic
        private fun beforeAll() {
            runBlocking {

                val fastAndFuriousMovies = listOf(
                    Movies(1, "2 Fast 2 Furious", listOf("undercover", "drug dealer")),
                    Movies(2, "Fast 5", listOf("bank robbery", "full team")),
                    Movies(3, "Furious 7", listOf("emotional")),
                    Movies(4, "The Fate of the Furious", listOf("betrayal"))
                )
                collection.insertMany(fastAndFuriousMovies)

            }
        }

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
    fun specifyOptionsTest() = runBlocking {
        // :snippet-start: text-index
        collection.createIndex(Indexes.text("title"))
        // :snippet-end:
        // Junit test for the above code
        val testFilter = Filters.and(Filters.gt("_id", 1))
        collection.find(testFilter).toList().forEach { println(it) }
        val expected = listOf(
            Movies(2, "Fast 5", listOf("bank robbery", "full team")),
            Movies(3, "Furious 7", listOf("emotional")),
            Movies(4, "The Fate of the Furious", listOf("betrayal"))
        )
        assertEquals(expected, collection.find(testFilter).toList() )

        // :snippet-start: specify-options
        val options: TextSearchOptions = TextSearchOptions().caseSensitive(true)
        val filter = Filters.text("some text", options)
        // :snippet-end:
    }

    @Test
    fun searchTermTest() = runBlocking {
        // :snippet-start: search-term
        val filter = Filters.text("fast")
        collection.find(filter).toList().forEach { println(it) }
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            Movies(2, "Fast 5", listOf("bank robbery", "full team")),
            Movies(1, "2 Fast 2 Furious", listOf("undercover", "drug dealer"))
        )
        assertEquals(expected, collection.find(filter).toList() )
    }

    @Test
    fun searchMultipleTermsTest() = runBlocking {
        // :snippet-start: search-multiple-terms
        val filter = Filters.text("fate 7")
        collection.find(filter).toList().forEach { println(it) }
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            Movies(3, "Furious 7", listOf("emotional")),
            Movies(4, "The Fate of the Furious", listOf("betrayal"))
        )
        assertEquals(expected, collection.find(filter).toList() )
    }

    @Test
    fun searchPhraseTest() = runBlocking {
        // :snippet-start: search-phrase
        val filter = Filters.text("\"fate of the furious\"")
        collection.find(filter).toList().forEach { println(it) }
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            Movies(4, "The Fate of the Furious", listOf("betrayal"))
        )
        assertEquals(expected, collection.find(filter).toList() )
    }

    @Test
    fun excludeTermTest() = runBlocking {
        // :snippet-start: exclude-term
        val filter = Filters.text("furious -fast")
        collection.find(filter).toList().forEach { println(it) }
        // :snippet-end:
        // Junit test for the above code
        val expected = listOf(
            Movies(4, "The Fate of the Furious", listOf("betrayal")),
            Movies(3, "Furious 7", listOf("emotional"))

        )
        assertEquals(expected, collection.find(filter).toList() )
    }

}