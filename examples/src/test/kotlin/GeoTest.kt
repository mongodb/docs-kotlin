import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.geoWithin
import com.mongodb.client.model.Filters.near
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.Projections.*
import com.mongodb.client.model.Sorts
import com.mongodb.client.model.geojson.Point
import com.mongodb.client.model.geojson.Polygon
import com.mongodb.client.model.geojson.Position
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance
import java.util.*
import java.util.stream.Collectors.toList
import kotlin.test.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SearchGeospatialTest {

    companion object {
        val dotenv = dotenv()
        val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        val database = client.getDatabase("sample_mflix")
        val collection = database.getCollection<Document>("theaters")

        @AfterAll
        @JvmStatic
        private fun afterAll() {
            runBlocking {
                collection.deleteMany(Filters.empty())
                client.close()

            }
        }
    }
    @Ignore
    fun indexTest() = runBlocking {
        // :snippet-start: geo2dsphere-index
        collection.createIndex((Indexes.geo2dsphere("location.geo")))
        // :snippet-end:
        // :snippet-start: geo2d-index
        collection.createIndex((Indexes.geo2d("coordinates")))
        // :snippet-end:
    }

    @Test
    fun geospatialQueryTest() = runBlocking {
        // :snippet-start: proximity-query
        val database = client.getDatabase("sample_mflix")
        val collection: MongoCollection<Document> = database.getCollection("theaters")
        val centralPark = Point(Position(-73.9667, 40.78))
        val query = near("location.geo", centralPark, 10000.0, 5000.0)
        val projection = fields(include("location.address.city"), excludeId())
        collection.find(query)
            .projection(projection)
            .toList().forEach { println(it) }
        // :snippet-end:
        collection.aggregate<Document>(listOf(query)).toList().forEach { println(it.toJson()) }
//        val expectedDocuments = listOf(
//            Document("location", Document("address", Document("city", "Bronx"))),
//            Document("location", Document("address", Document("city", "New York"))),
//            Document("location", Document("address", Document("city", "New York"))),
//            Document("location", Document("address", Document("city", "Long Island City"))),
//            Document("location", Document("address", Document("city", "New York"))),
//            Document("location", Document("address", Document("city", "Secaucus"))),
//            Document("location", Document("address", Document("city", "Jersey City"))),
//            Document("location", Document("address", Document("city", "Elmhurst"))),
//            Document("location", Document("address", Document("city", "Flushing"))),
//            Document("location", Document("address", Document("city", "Flushing"))),
//            Document("location", Document("address", Document("city", "Flushing"))),
//            Document("location", Document("address", Document("city", "Elmhurst")))
//        )
//        val actualDocuments = collection.find<Document>(geoWithinComparison)
//            .projection(projection)
//            .toList()
//        assertEquals(expectedDocuments, actualDocuments)
    }

    @Test
    fun queryRangeTest() = runBlocking {
        val database = client.getDatabase("sample_mflix")
        val collection: MongoCollection<Document> = database.getCollection("theaters")
        // :snippet-start: query-range

        val longIslandTriangle = Polygon(
            listOf(
                Position(-72.0, 40.0),
                Position(-74.0, 41.0),
                Position(-72.0, 39.0),
                Position(-72.0, 40.0)
            )
        )
        val projection = fields(
            include("location.address.city"),
            excludeId()
        )
        val geoWithinComparison = geoWithin("location.geo", longIslandTriangle)
        collection.find<Document>(geoWithinComparison)
            .projection(projection)
            .toList().forEach { println(it.toJson()) }
        // :snippet-end:
        val expectedDocuments = listOf(
            Document("location", Document("address", Document("city", "Baldwin"))),
            Document("location", Document("address", Document("city", "Levittown"))),
            Document("location", Document("address", Document("city", "Westbury"))),
            Document("location", Document("address", Document("city", "Mount Vernon"))),
            Document("location", Document("address", Document("city", "Massapequa"))),
        )
        val actualDocuments = collection.find<Document>(geoWithinComparison)
            .projection(projection)
            .toList().forEach { println(it.toJson()) }
        assertEquals(expectedDocuments, actualDocuments)
    }
}