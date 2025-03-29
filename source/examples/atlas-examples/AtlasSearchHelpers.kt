import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Projections
import com.mongodb.client.model.search.SearchOperator
import com.mongodb.client.model.search.SearchPath.fieldPath
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.conversions.Bson
import java.io.IO.println
import kotlin.collections.List

const val URI = "<connection-string>"

// Create data class to represent a MongoDB document
data class Movie(val title: String, val year: Int, val cast: List<String>)

fun main() {

    // Replace the placeholder with your MongoDB deployment's connection string
    val uri = URI

    val mongoClient = MongoClient.create(uri)
    val database = mongoClient.getDatabase("sample_mflix")
    // Get a collection of documents of type Movie
    val collection = database.getCollection<Movie>("movies")

    // start atlasHelperMethods
    runBlocking {
        val searchStage: Bson = Aggregates.search(
            SearchOperator.compound()
                .filter(
                    listOf(
                        SearchOperator.text(fieldPath("genres"), "Drama"),
                        SearchOperator.phrase(fieldPath("cast"), "sylvester stallone"),
                        SearchOperator.numberRange(fieldPath("year")).gtLt(1980, 1989),
                        SearchOperator.wildcard(fieldPath("title"), "Rocky *")
                    )
                )
        )

        val projection = Projections.fields(
            Projections.include("title", "year", "genres", "cast")
        )

        val aggregatePipelineStages: List<Bson> = listOf(searchStage, Aggregates.project(projection))
        val results = collection.aggregate<Document>(aggregatePipelineStages)

        results.collect { println(it) }
    }
    // end atlasHelperMethods

    mongoClient.close()
}

