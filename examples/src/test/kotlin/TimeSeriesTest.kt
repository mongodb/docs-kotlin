import com.mongodb.*
import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.Projections.*
import com.mongodb.client.model.TimeSeriesOptions
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


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TimeSeriesTest {
    
    companion object {
        val dotenv = dotenv()
        val mongoClient = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])

        @AfterAll
        @JvmStatic
        fun afterAll() {
            runBlocking {
                mongoClient.close()
            }
        }
    }

    @Test
    fun timeSeriesTest() = runBlocking {
        // :snippet-start: create-time-series-collection
        val database = mongoClient.getDatabase("fall_weather")
        val tsOptions = TimeSeriesOptions("temperature")
        val collOptions = CreateCollectionOptions().timeSeriesOptions(tsOptions)

        database.createCollection("september2021", collOptions)
        // :snippet-end:
        // :snippet-start: check-time-series-collection-created
        val commandResult = database.runCommand(Document("listCollections", BsonInt64(1)))

        println(commandResult.toJson())
        // :snippet-end:
        // clean up
        database.drop()
        // access the commandResult.cursor.firstBatch[0].type
        val type = ((commandResult["cursor"] as Document )[ "firstBatch" ] as List<Document>)[0].getString("type")
        assertEquals("timeseries", type )
    }


}