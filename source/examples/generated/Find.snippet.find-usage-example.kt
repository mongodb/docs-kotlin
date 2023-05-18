import com.mongodb.client.model.Filters.lt
import com.mongodb.client.model.Projections
import com.mongodb.client.model.Sorts
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.runBlocking

data class Movie(val title: String, val imdb: IMDB){
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
    val resultsFlow = collection.find(lt("runtime", 15))
        .projection(projectionFields)
        .sort(Sorts.descending("title"))
    resultsFlow.collect { println(it) }

    mongoClient.close()
}
