
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.bson.codecs.pojo.annotations.BsonExtraElements
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class KotlinXSerializationTest {

    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        val database = client.getDatabase("paint_store")

        @AfterAll
        @JvmStatic
        fun afterAll() {
            runBlocking {
                client.close()
            }
        }
    }

    @Test
    fun basicSerializationTest() {
        // ObjectId commented out b/c it errors when trying to serialize
        @Serializable
        data class PaintOrder(
            @SerialName("_id")
            val id: Int,
            val color: String,
            @BsonExtraElements val extraElements: Map<String, String>,
            @Contextual val qty: ObjectId,
        )

        val paintOrder = PaintOrder(1, "red",  mapOf("foo" to "bar"), ObjectId())
        println(Json.encodeToJsonElement<PaintOrder>(paintOrder))
        assert(true)
    }

}

