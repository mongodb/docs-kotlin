import com.mongodb.*
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import org.bson.BsonInt64
import org.bson.Document
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.*

// :replace-start: {
//    "terms": {
//       "CONNECTION_URI_PLACEHOLDER": "\"<connection string>\"",
//       "${uri}&": "mongodb+srv://<user>:<password>@<cluster-url>/?"
//    }
// }

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class NetworkCompressionTest {

    companion object {
        private val dotenv = dotenv()
        val CONNECTION_URI_PLACEHOLDER = dotenv["MONGODB_CONNECTION_URI"]
        var higherScopedClient: MongoClient? = null

        @AfterAll
        @JvmStatic
        fun afterAll() {
            runBlocking {
                higherScopedClient?.close()
            }
        }

    }

    @Test
    fun connectionStringCompressionTest() = runBlocking {
        // :snippet-start: connection-string-compression-example
        // Replace the placeholders with values from your connection string
        val uri = CONNECTION_URI_PLACEHOLDER // :remove:
        val connectionString = ConnectionString("${uri}&compressors=snappy,zstd")

        lateinit var higherScopedCommandResult: Document // :remove:
        // Create a new client with your settings
        val mongoClient = MongoClient.create(connectionString)
        // :snippet-end:

        val database = mongoClient.getDatabase("admin")
        try {
            // Send a ping to confirm a successful connection
            val command = Document("ping", BsonInt64(1))
            val commandResult = database.runCommand(command)
            println("Pinged your deployment. You successfully connected to MongoDB!")
            higherScopedCommandResult = commandResult // :remove:
        } catch (me: MongoException) {
            System.err.println(me)
        }

        higherScopedClient = mongoClient
        assertEquals(1, higherScopedCommandResult["ok"])
    }
    @Test
    fun mongoClientSettingsCompressionTest() = runBlocking {
        // :snippet-start: mongoclientsettings-compression-example
        // Replace the placeholder with your Atlas connection string
        val uri = CONNECTION_URI_PLACEHOLDER

        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(uri))
            .compressorList(
                listOf(
                    MongoCompressor.createSnappyCompressor(),
                    MongoCompressor.createZstdCompressor())
            )
            .build()

        lateinit var higherScopedCommandResult: Document // :remove:
        // Create a new client with your settings
        val mongoClient = MongoClient.create(settings)
        // :snippet-end:

        val database = mongoClient.getDatabase("admin")
        try {
            // Send a ping to confirm a successful connection
            val command = Document("ping", BsonInt64(1))
            val commandResult = database.runCommand(command)
            println("Pinged your deployment. You successfully connected to MongoDB!")
            higherScopedCommandResult = commandResult // :remove:
        } catch (me: MongoException) {
            System.err.println(me)
        }

        higherScopedClient = mongoClient
        assertEquals(1, higherScopedCommandResult["ok"])
    }
}
// :replace-end: