
import com.mongodb.client.model.*
import com.mongodb.client.model.Accumulators.avg
import com.mongodb.client.model.Accumulators.sum
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.BsonDouble
import org.bson.BsonString
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

class AggregatesBuilderTest {

    // :snippet-start: data-classes
    // Data class for the movies movieCollection
    data class Movie(
        @BsonId val id: Int,
        val title: String,
        val year: Int,
        val cast: List<String>,
        val genres: List<String>,
        val type: String,
        val rated: String,
        val plot: String,
        val fullplot: String,
        val imdb: IMDB
    ){
        data class IMDB(
            val rating: Double
        )
    }
    // :snippet-end:

    data class Comment(val movie_id: Int, val movie: String)

    companion object {
        val dotenv = dotenv()
        private val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        private val database = client.getDatabase("aggregation")
        val movieCollection = database.getCollection<Movie>("movies")
        val commentsCollection = database.getCollection<Comment>("comments")

        @AfterAll
        @JvmStatic
        fun afterAll() {
            runBlocking {
                movieCollection.drop()
                commentsCollection.drop()
                client.close()
            }
        }
    }

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            val movies = listOf(
                Movie(
                    id = 1,
                    title = "The Shawshank Redemption",
                    year = 1994,
                    cast = listOf("Tim Robbins", "Morgan Freeman", "Bob Gunton"),
                    genres = listOf("Drama"),
                    type = "movie",
                    rated = "R",
                    plot = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                    fullplot = "Andy Dufresne is sent to Shawshank Prison for the murder of his wife and her secret lover. He is very isolated and lonely at first, but realizes there is something deep inside your body that people can't touch or get to....'HOPE'. Andy becomes friends with prison 'fixer' Red, and Andy epitomizes why it is crucial to have dreams. His spirit and determination lead us into a world full of imagination, one filled with courage and desire. Will Andy ever realize his dreams?",
                    imdb = Movie.IMDB(rating = 7.1)
                ),
                Movie(
                    id = 2,
                    title = "The Godfather",
                    year = 1972,
                    cast = listOf("Marlon Brando", "Al Pacino", "James Caan"),
                    genres = listOf("Crime", "Drama"),
                    type = "movie",
                    rated = "R",
                    plot = "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
                    fullplot = "When the aging head of a famous crime family decides to transfer his position to one of his subalterns, a series of unfortunate events start happening to the family, and a war begins between all the well-known families leading to insolence, deportation, murder and revenge, and ends with the favorable successor being finally chosen.",
                    imdb = Movie.IMDB(rating = 8.9)
                ),
                Movie(
                    id = 3,
                    title = "Pulp Fiction",
                    year = 1994,
                    cast = listOf("John Travolta", "Samuel L. Jackson", "Uma Thurman"),
                    genres = listOf("Crime", "Drama"),
                    type = "movie",
                    rated = "R",
                    plot = "The lives of two mob hitmen, a boxer, a gangster's wife, and a pair of diner bandits intertwine in four tales of violence and redemption.",
                    fullplot = "Jules Winnfield and Vincent Vega are two hitmen who are out to retrieve a suitcase stolen from their employer, mob boss Marsellus Wallace. Wallace has also asked Vincent to take his wife Mia out a few days later when Wallace himself will be out of town. Butch Coolidge is an aging boxer who is paid by Wallace to lose his fight. The lives of these seemingly unrelated people are woven together comprising of a series of funny, bizarre and uncalled-for incidents.",
                    imdb = Movie.IMDB(rating = 5.3)
                ),
                Movie(
                    id = 4,
                    title = "The Dark Knight",
                    year = 2008,
                    cast = listOf("Christian Bale", "Heath Ledger", "Aaron Eckhart"),
                    genres = listOf("Action", "Crime", "Drama"),
                    type = "movie",
                    rated = "PG-13",
                    plot = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
                    fullplot = "Set within a year after the events of Batman Begins, Batman, Lieutenant James Gordon, and new district attorney Harvey Dent successfully begin to round up the criminals that plague Gotham City until a mysterious and sadistic criminal mastermind known only as the Joker appears in Gotham, creating a new wave of chaos. Batman's struggle against the Joker becomes deeply personal, forcing him to 'confront everything he believes' and improve his technology to stop him. A love triangle develops between Bruce Wayne, Dent, and Rachel Dawes.",
                    imdb = Movie.IMDB(rating = 5.2)
                ),
                Movie(
                    id = 5,
                    title = "Forrest Gump",
                    year = 1994,
                    cast = listOf("Tom Hanks", "Robin Wright", "Gary Sinise"),
                    genres = listOf("Drama", "Romance"),
                    type = "movie",
                    rated = "PG-13",
                    plot = "The presidencies of Kennedy and Johnson, the Vietnam War, the Watergate scandal and other historical events unfold from the perspective of an Alabama man with an IQ of 75, whose only desire is to be reunited with his childhood sweetheart.",
                    fullplot = "Forrest Gump is a simple man with a low IQ but good intentions. He is running through childhood with his best and only friend Jenny. His 'mama' teaches him the ways of life and leaves him to choose his destiny. Forrest joins the army for service in Vietnam, finding new friends called Dan and Bubba, he wins medals, creates a famous shrimp fishing fleet, inspires people to jog, starts a ping-pong craze, creates the smiley, writes bumper stickers and songs, donates to people and meets the president several times. However, this is all irrelevant to Forrest who can only think of his childhood sweetheart Jenny. Who has messed up her life. Although in the end, all he wants to prove is that anyone can love anyone.",
                    imdb = Movie.IMDB(rating = 6.1)
                ),
                Movie(
                    id = 6,
                    title = "The Matrix",
                    year = 1999,
                    cast = listOf("Keanu Reeves", "Laurence Fishburne", "Carrie-Anne Moss"),
                    genres = listOf("Action", "Sci-Fi"),
                    type = "movie",
                    rated = "R",
                    plot = "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.",
                    fullplot = "Thomas A. Anderson is a man living two lives. By day he is an average computer programmer and by night a hacker known as Neo. Neo has always questioned his reality, but the truth is far beyond his imagination. Neo finds himself targeted by the police when he is contacted by Morpheus, a legendary computer hacker branded a terrorist by the government. Morpheus awakens Neo to the real world, a ravaged wasteland where most of humanity have been captured by a race of machines that live off of the humans' body heat and electrochemical energy and who imprison their minds within an artificial reality known as the Matrix. As a rebel against the machines, Neo must return to the Matrix and confront the agents: super-powerful computer programs devoted to snuffing out Neo and the entire human rebellion.",
                    imdb = Movie.IMDB(rating = 8.7)
                ),
                Movie(
                    id = 7,
                    title = "Fight Club",
                    year = 1999,
                    cast = listOf("Brad Pitt", "Edward Norton", "Meat Loaf"),
                    genres = listOf("Drama"),
                    type = "movie",
                    rated = "R",
                    plot = "An insomniac office worker and a devil-may-care soapmaker form an underground fight club that evolves into something much, much more.",
                    fullplot = "A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground 'fight clubs' forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.",
                    imdb = Movie.IMDB(rating = 8.8)
                ),
                Movie(
                    id = 8,
                    title = "American Beauty",
                    year = 1999,
                    cast = listOf("Kevin Spacey", "Annette Bening", "Thora Birch"),
                    genres = listOf("Drama"),
                    type = "movie",
                    rated = "R",
                    plot = "A sexually frustrated suburban father has a mid-life crisis after becoming infatuated with his daughter's best friend.",
                    fullplot = "Lester Burnham, a depressed suburban father in a mid-life crisis, decides to turn his hectic life around after developing an infatuation for his daughter's attractive friend. His relationship with his wife Carolyn and his daughter Jane becomes strained. On the way, he realizes his life and learns to appreciate the beauty around him.",
                    imdb = Movie.IMDB(rating = 8.3)
                ),
                Movie(
                    id = 9,
                    title = "One Flew Over the Cuckoo's Nest",
                    year = 1975,
                    cast = listOf("Jack Nicholson", "Louise Fletcher", "Will Sampson"),
                    genres = listOf("Drama"),
                    type = "movie",
                    rated = "R",
                    plot = "A criminal pleads insanity and is admitted to a mental institution, where he rebels against the oppressive nurse and rallies up the scared patients.",
                    fullplot = "McMurphy has a criminal past and has once again gotten himself into trouble and is sentenced by the court. To escape labor duties in prison, McMurphy pleads insanity and is sent to a ward for the mentally unstable. Once here, McMurphy both endures and stands witness to the abuse and degradation of the oppressive Nurse Ratched, who gains superiority and power through the flaws of the other inmates. McMurphy and the other inmates band together to make a rebellious stance against the atrocious Nurse.",
                    imdb = Movie.IMDB(rating = 8.7)
                )
            )
            movieCollection.insertMany(movies)

            val comments = listOf(
                Comment(movie_id = 1, movie = "The Shawshank Redemption"),
                Comment(movie_id = 2, movie = "The Godfather"),
                Comment(movie_id = 3, movie = "Pulp Fiction"),
                Comment(movie_id = 4, movie = "The Dark Knight"),
                Comment(movie_id = 5, movie = "Forrest Gump"),
                Comment(movie_id = 6, movie = "The Matrix"),
                Comment(movie_id = 7, movie = "Fight Club"),
                Comment(movie_id = 8, movie = "American Beauty"),
                Comment(movie_id = 9, movie = "One Flew Over the Cuckoo's Nest")
                )
            commentsCollection.insertMany(comments)
        }
    }

    @AfterEach
    fun afterEach() {
        runBlocking {
            //movieCollection.dropIndexes()
            movieCollection.drop()
        }
    }

    @Test
    fun matchTest()  = runBlocking {
        // :snippet-start: match
         val match = movieCollection.aggregate<Movie>(listOf(
            Aggregates.match(Filters.eq(Movie::title.name, "The Shawshank Redemption"))
        ))
        match.collect { println(it)}
        // :snippet-end:
        val results = match.toList()
        assertEquals("The Shawshank Redemption", results.first().title)
    }

    @Test
    fun projectTest()  = runBlocking {
        // :snippet-start: project
        data class Results(val title: String, val plot: String)

        val project = movieCollection.aggregate<Results>(
            listOf(
                Aggregates.project(
                    Projections.fields(
                        Projections.include(Movie::title.name, Movie::plot.name),
                        Projections.excludeId())
                )
            )
        )
        project.collect { println(it) }
        // :snippet-end:
        val results = project.toList()
        assertEquals("The Shawshank Redemption", results.first().title)
    }

    @Test
    fun projectComputeTest()  = runBlocking {
        // :snippet-start: project-computed
        data class Results(val rating: String)

        val compute = movieCollection.aggregate<Results>(
            listOf(
                Aggregates.project(
                    Projections.fields(
                        Projections.computed("rating", "\$rated"),
                        Projections.excludeId()))
            )
        )
        compute.collect { println(it) }
        // :snippet-end:
        val results = compute.toList()
        assertEquals(9, results.size)
    }

//    @Test
//    fun documentsTest() = runBlocking {
//        // :snippet-start: documents
//        data class Results(val title: String)
//
//        val docs = database.aggregate<Results>(
//            listOf(
//                Aggregates.documents(
//                    listOf(
//                        Results("The Shawshank Redemption"),
//                        Results("The Shawshank Redemption"),
//                        Results("The Shawshank Redemption")
//                ))
//        ))
//        // :snippet-end:
//    }

    @Test
    fun sampleTest() = runBlocking {
        // :snippet-start: sample
        val results = movieCollection.aggregate<Movie>(
            listOf(Aggregates.sample(5)))
        results.collect { println(it)}
        // :snippet-end:
    }

    @Test
    fun sortTest() = runBlocking {
        // :snippet-start: sort
        val results = movieCollection.aggregate<Movie>(listOf(
            Aggregates.sort(
                Sorts.orderBy(
                    Sorts.descending(Movie::year.name),
                    Sorts.ascending(Movie::title.name))
            )
        ))
        results.collect { println(it)}
        // :snippet-end:
    }

    @Test
    fun skipTest() = runBlocking {
        // :snippet-start: skip
        val results = movieCollection.aggregate<Movie>(listOf(
            Aggregates.skip(5)))
        results.collect { println(it)}
        // :snippet-end:
    }

    @Test
    fun limitTest() = runBlocking {
        // :snippet-start: limit
        val results = movieCollection.aggregate<Movie>(listOf(
            Aggregates.limit(4)
        ))
        results.collect { println(it)}
        // :snippet-end:
    }

    @Test
    fun lookupTest() = runBlocking {
        // :snippet-start: lookup
        data class Combined(val id:Int, val movie_id: Int)
        data class Results(val joined_comments: List<Combined>)

        val results = movieCollection.aggregate<Results>(listOf(
            Aggregates.lookup("comments", "id", "movie_id", "joined_comments")))
        results.collect { println(it)}
        // :snippet-end:
    }

    @Test
    fun lookupFullJoinTest() = runBlocking {
        data class Order(@BsonId val id: Int, val order_item: String, val order_qty: Int)
        data class Inventory(@BsonId val id: Int, val stock_item: String, val instock: Int)

        val orderCollection = database.getCollection<Order>("orders")
        val warehouseCollection = database.getCollection<Inventory>("warehouses")

        val orders = listOf(Order(1, "item1", 5), Order(2, "item2", 5), Order(3, "item3", 5))
        val inventory= listOf(Inventory(1, "item1", 10), Inventory(2, "item2", 20), Inventory(3, "item3", 30))

        orderCollection.insertMany(orders)
        warehouseCollection.insertMany(inventory)

        // :snippet-start: lookup-full-join
        val variables = listOf(
            Variable("order_item", "\$item"),
            Variable("order_qty", "\$ordered")
        )

        val pipeline = listOf(
            Aggregates.match(
                Filters.expr(
                    Document("\$and", listOf(
                        Document("\$eq", listOf("$\$order_item", "\$stock_item")),
                        Document("\$gte", listOf("\$instock", "$\$order_qty"))
                    ))
                )
            ),
            Aggregates.project(
                Projections.fields(
                    Projections.exclude("stock_item"),
                    Projections.excludeId()))
        )

        val innerJoinLookup = Aggregates.lookup("warehouses", variables, pipeline, "stockdata")
    // :snippet-end:

// WRITE TEST & TROUBLESHOOT RETURNED DATA
        warehouseCollection.drop()
        orderCollection.drop()
    }

    @Test
    fun groupTest() = runBlocking {
        data class Order(@BsonId val id: Int, val customerId: Int, val quantity: Int)

        val orderCollection = database.getCollection<Order>("orders")
        val orders = listOf(Order(1, 1, 1), Order(2, 1, 10), Order(3, 2, 5), Order(4, 2, 50))
        orderCollection.insertMany(orders)

        // :snippet-start: group
        data class Results(val totalQuantity: Int, val averageQuantity: Double)

        val grouping = orderCollection.aggregate<Results>(listOf(
            Aggregates.group("\$customerId",
                sum("totalQuantity", "\$quantity"),
                avg("averageQuantity", "\$quantity"))))

        grouping.collect { println(it)}
        // :snippet-end:
// WRITE TEST
        orderCollection.drop()
    }

    @Test
    fun minNTest() = runBlocking {
        // :snippet-start: minN
        data class Results(val lowest_three_ratings: List<Double>)

        val resultsFlow = movieCollection.aggregate<Results>(
            listOf(
                Aggregates.group(
                    "\$year",
                    Accumulators.minN(
                        "lowest_three_ratings",
                        "\$imdb.rating",
                        3
                    )
                )
            )
        )
        resultsFlow.collect { println(it)}
        // :snippet-end:
        val results = resultsFlow.toList()
        assertEquals(5, results.size)
    }

    @Test
    fun maxNTest() = runBlocking {
        // :snippet-start: maxN
        data class Results(val highest_two_ratings: List<Double>)

        val resultsFlow = movieCollection.aggregate<Results>(
            listOf(
                Aggregates.group(
                    "\$year",
                    Accumulators.maxN(
                        "highest_two_ratings",
                        "\$imdb.rating",
                        2
                    )
                )
            )
        )
        resultsFlow.collect { println(it)}
        // :snippet-end:
        val results = resultsFlow.toList()
        assertEquals(5, results.size)
    }

    @Test
    fun firstNTest() = runBlocking {
        // :snippet-start: firstN
        data class Results(val first_two_movies: List<String>)

        val resultsFlow = movieCollection.aggregate<Results>(
            listOf(
                Aggregates.group(
                    "\$year",
                    Accumulators.firstN(
                        "first_two_movies",
                        "\$title",
                        2
                    )
                )
            )
        )
        resultsFlow.collect { println(it)}
        // :snippet-end:
        val results = resultsFlow.toList()
    }

    @Test
    fun lastNTest() = runBlocking {
        // :snippet-start: lastN
        data class Results(val last_three_movies: List<String>)

        val resultsFlow = movieCollection.aggregate<Results>(
            listOf(
                Aggregates.group(
                    "\$year",
                    Accumulators.lastN(
                        "last_three_movies",
                        "\$title",
                        3
                    )
                )
            )
        )
        resultsFlow.collect { println(it)}
        // :snippet-end:
        val results = resultsFlow.toList()
    }

    @Test
    fun topTest() = runBlocking {
        // :snippet-start: top

        data class TopRated(val title: BsonString, val rating: BsonDouble)
        data class Results(val top_rated_movie: List<Any>)

        val resultsFlow = movieCollection.aggregate<Results>(
            listOf(
                Aggregates.group(
                    "\$year",
                    Accumulators.top(
                        "top_rated_movie",
                        Sorts.descending("imdb.rating"),
                        listOf("\$title", "\$imdb.rating")
                    )
                )
            )
        )
       // resultsFlow.collect { println(it)}
        // :snippet-end:
        val results = resultsFlow.toList()
    }


}