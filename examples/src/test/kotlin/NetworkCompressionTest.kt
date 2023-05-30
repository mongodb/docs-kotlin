
import com.mongodb.*
import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.Projections.*
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.*

// :replace-start: {
//    "terms": {
//       "CONNECTION_STRING_CONNECTION_URI_PLACEHOLDER": "\"mongodb+srv://<user>:<password>@<cluster-url>/?compressors=snappy,zlib,zstd\""
//    }
// }

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class NetworkCompressionTest {

    companion object {

        val dotenv = dotenv()
        var CONNECTION_URI_PLACEHOLDER = dotenv["MONGODB_CONNECTION_URI"]

        @AfterAll
        @JvmStatic
        fun afterAll() {
            runBlocking {
                // TODO
            }
        }
    }

    @Ignore // Ignoring b/c couldn't find a good way to assert that compressors applied
    @Test
    fun connectionStringTest() {
        val CONNECTION_STRING_CONNECTION_URI_PLACEHOLDER = "$CONNECTION_URI_PLACEHOLDER&compressors=snappy,zlib,zstd"
        // :snippet-start: connection-string
        val connectionString =
            ConnectionString(CONNECTION_STRING_CONNECTION_URI_PLACEHOLDER)
        val mongoClient = MongoClient.create(connectionString)
        // :snippet-end:
        // clean up
        mongoClient.close()
    }

    @Ignore
    @Test
    fun builderTest() {
        
    }
}
// :replace-end: