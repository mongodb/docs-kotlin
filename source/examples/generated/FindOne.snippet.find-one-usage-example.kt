import com.mongodb.client.model.Filters.lt
import com.mongodb.client.model.Projections
import com.mongodb.client.model.Sorts
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

data class Movie(val title: String, val imdb: IMDB) {
    data class IMDB(val rating: Double)
}

fun main() = runBlocking {
    // Replace the uri string with your MongoDB deployment's connection string
    val uri = "<connection string uri>"
    val mongoClient = MongoClient.create(uri)
    val database = mongoClient.getDatabase("sample_mflix")
    val collection = database.getCollection<Movie>("movies")

    val projectionFields= Projections.fields(
        Projections.include("title", "imdb"),
        Projections.excludeId()
    )
    val resultsFlow = collection.find(lt("title", "The Room"))
        .projection(projectionFields)
        .sort(Sorts.descending("imdb.rating"))
        .firstOrNull()

    println(resultsFlow)
    mongoClient.close()
}
