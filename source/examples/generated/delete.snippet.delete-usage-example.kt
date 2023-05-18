
import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.result.DeleteResult
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId


data class Movie(@BsonId val id: Int, val title: String)

fun main() = runBlocking {
    // Replace the uri string with your MongoDB deployment's connection string
    val uri = "<connection string uri>"
    val mongoClient = MongoClient.create(uri)
    val database = mongoClient.getDatabase("sample_mflix")
    val collection = database.getCollection<Movie>("movies")

    val query = Filters.eq(Movie::title.name, "The Garbage Pail Kids Movie")

    try {
        val result: DeleteResult = collection.deleteOne(query)
        println("Deleted document count: " + result.deletedCount)
    } catch (e: MongoException) {
        System.err.println("Unable to delete due to an error: $e")
    }
    mongoClient.close()
}
