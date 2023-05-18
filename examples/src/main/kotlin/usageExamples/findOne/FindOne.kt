package usageExamples.findOne

// :replace-start: {
//    "terms": {
//       "CONNECTION_URI_PLACEHOLDER": "\"<connection string uri>\"",
//       "import io.github.cdimascio.dotenv.dotenv\n": ""
//    }
// }
// :snippet-start: find-one-usage-example
import com.mongodb.client.model.Filters.lt
import com.mongodb.client.model.Projections
import com.mongodb.client.model.Sorts
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

data class Movie(val title: String, val runtime: Int, val imdb: IMDB) {
    data class IMDB(val rating: Double)
}

fun main() = runBlocking {
    // :remove-start:
    val dotenv = dotenv()
    val CONNECTION_URI_PLACEHOLDER = dotenv["MONGODB_CONNECTION_URI"]
    // :remove-end:
    // Replace the uri string with your MongoDB deployment's connection string
    val uri = CONNECTION_URI_PLACEHOLDER
    val mongoClient = MongoClient.create(uri)
    val database = mongoClient.getDatabase("sample_mflix")
    val collection = database.getCollection<Movie>("movies")

    val projectionFields= Projections.fields(
        Projections.include(Movie::title.name, Movie::imdb.name),
        Projections.excludeId()
    )
    val resultsFlow = collection.find(lt(Movie::title.name, "The Room"))
        .projection(projectionFields)
        .sort(Sorts.descending("${Movie::imdb.name}.${Movie.IMDB::rating.name}"))
        .firstOrNull()

    println(resultsFlow)
    // :remove-start:
    // clean up
    database.drop()
    // :remove-end:
    mongoClient.close()
}
// :snippet-end:
// :replace-end: