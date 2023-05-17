
import com.mongodb.MongoNamespace
import com.mongodb.client.model.*
import com.mongodb.client.model.Accumulators.*
import com.mongodb.client.model.Aggregates.*
import com.mongodb.client.model.Projections.fields
import com.mongodb.client.model.Sorts.ascending
import com.mongodb.client.model.densify.DensifyOptions
import com.mongodb.client.model.densify.DensifyRange
import com.mongodb.client.model.fill.FillOptions
import com.mongodb.client.model.fill.FillOutputField
import com.mongodb.client.model.geojson.Point
import com.mongodb.client.model.geojson.Position
import com.mongodb.client.model.search.SearchOperator
import com.mongodb.client.model.search.SearchOptions
import com.mongodb.client.model.search.SearchPath
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.BsonDouble
import org.bson.BsonString
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.test.assertEquals


class AggregatesBuilderTest {

    // :snippet-start: movie-data-class
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
        val runtime: Int,
        val imdb: IMDB
    ){
        data class IMDB(
            val rating: Double
        )
    }
    // :snippet-end:

    // :snippet-start: graph-data-class
    data class Employee(
        val id: Int,
        val name: String,
        val department: String,
        val reportsTo: String? = null,
    )
    // :snippet-end:

    // :snippet-start: bucket-data-class
    data class Screen(
        val id: String,
        val screenSize: Int,
        val manufacturer: String,
        val price: Double
    )
    // :snippet-end:

    companion object {
        val dotenv = dotenv()
        private val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        private val database = client.getDatabase("aggregation")
        val movieCollection = database.getCollection<Movie>("movies")
        private val ftsDatabase = client.getDatabase("sample_mflix")
        val ftsCollection = ftsDatabase.getCollection<Movie>("movies")
        val employeesCollection = database.getCollection<Employee>("employees")
        val screenCollection = database.getCollection<Screen>("screens")


        @BeforeAll
        @JvmStatic
        fun beforeAll() {
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
                        runtime = 142,
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
                        runtime = 175,
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
                        runtime = 154,
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
                        runtime = 154,
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
                        runtime = 142,
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
                        runtime = 136,
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
                        runtime = 139,
                        imdb = Movie.IMDB(rating = 8.8)
                    ),
                    Movie(
                        id = 8,
                        title = "The Sixth Sense",
                        year = 1999,
                        cast = listOf("Bruce Willis", "Haley Joel Osment", "Toni Collette"),
                        genres = listOf("Drama", "Mystery", "Thriller"),
                        type = "movie",
                        rated = "PG-13",
                        plot = "A boy who communicates with spirits seeks the help of a disheartened child psychologist.",
                        fullplot = "Malcom Crowe is a child psychologist who receives an award on the same night that he is visited by a very unhappy ex-patient. After this encounter, Crowe takes on the task of curing a young boy with the same ills as the ex-patient. This boy 'sees dead people'. Crowe spends a lot of time with the boy (Cole) much to the dismay of his wife. Cole's mom is at her wit's end with what to do about her son's increasing problems. Crowe is the boy's only hope.",
                        runtime = 107,
                        imdb = Movie.IMDB(rating = 8.1)
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
                        runtime = 133,
                        imdb = Movie.IMDB(rating = 8.7)
                    )
                )
                movieCollection.insertMany(movies)

                val employees = listOf(Employee(1, "Dev", "Executive"), Employee(2, "Eliot", "Engineering", "Dev"), Employee(3, "Ron", "Marketing", "Eliot"), Employee(4, "Andrew", "HR", "Eliot"), Employee(5, "Asya", "Engineering", "Ron"), Employee(6, "Dan", "HR", "Andrew"))
                employeesCollection.insertMany(employees)

                val screens = listOf(
                    Screen("1", 15,  "Manufacturer A", 100.00),
                    Screen("2", 27,  "Manufacturer B", 250.00),
                    Screen("3", 45,  "Manufacturer C", 750.00),
                    Screen("4", 72,  "Manufacturer D", 1950.00),
                    Screen("5", 500,  "Manufacturer E", 5000.00)
                )
                screenCollection.insertMany(screens)
            }
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            runBlocking {
                database.drop()
                client.close()
            }
        }
    }

    @Test
    fun matchTest()  = runBlocking {
         val match = movieCollection.aggregate<Movie>(listOf(
             // :snippet-start: match
            Aggregates.match(Filters.eq(Movie::title.name, "The Shawshank Redemption"))
             // :snippet-end:
        ))
        assertEquals("The Shawshank Redemption", match.toList().first().title)
    }

    @Test
    fun projectTest()  = runBlocking {
        data class Results(val title: String, val plot: String)
        val project = movieCollection.aggregate<Results>(
            listOf(
                // :snippet-start: project
                Aggregates.project(
                    Projections.fields(
                        Projections.include(Movie::title.name, Movie::plot.name),
                        Projections.excludeId())
                )
                // :snippet-end:
            ))
        assertEquals("The Shawshank Redemption", project.toList().first().title)
    }

    @Test
    fun projectComputeTest()  = runBlocking {
        data class Results(val rating: String)
        val compute = movieCollection.aggregate<Results>(
            listOf(
                // :snippet-start: project-computed
                Aggregates.project(
                    Projections.fields(
                        Projections.computed("rating", "\$rated"),
                        Projections.excludeId()
                    )
                )
                // :snippet-end:
            ))
        assertEquals(9, compute.toList().size)
    }

    @Test
    fun documentsTest() = runBlocking {
        data class Results(val title: String)
        // :snippet-start: doc-important
        val docsStage = database.aggregate<Results>( // ... )
            // :snippet-end:
            listOf(
                // :snippet-start: documents
                Aggregates.documents(
                    listOf(
                        Document("title", "Steel Magnolias"),
                        Document("title", "Back to the Future"),
                        Document("title", "Jurassic Park")
                    )
                )
                // :snippet-end:
        ))
        assertEquals(3, docsStage.toList().size)
    }

    @Test
    fun sampleTest() = runBlocking {
        val sample = movieCollection.aggregate<Movie>(
            listOf(
                // :snippet-start: sample
                Aggregates.sample(5)
                // :snippet-end:
            ))
        assertEquals(5, sample.toList().size)
    }

    @Test
    fun sortTest() = runBlocking {
        val sort = movieCollection.aggregate<Movie>(
            listOf(
                // :snippet-start: sort
                Aggregates.sort(
                    Sorts.orderBy(
                        Sorts.descending(Movie::year.name),
                        Sorts.ascending(Movie::title.name)
                    )
                )
                // :snippet-end:
            )
        )
        assertEquals("The Dark Knight", sort.toList().first().title)
    }

    @Test
    fun skipTest() = runBlocking {
        val skip = movieCollection.aggregate<Movie>(listOf(
            // :snippet-start: skip
            Aggregates.skip(5)
            // :snippet-end:
        ))
        assertEquals(4, skip.toList().size)
    }

    @Test
    fun limitTest() = runBlocking {
        val limit = movieCollection.aggregate<Movie>(listOf(
            // :snippet-start: limit
            Aggregates.limit(4)
            // :snippet-end:
        ))
        assertEquals(4, limit.toList().size)
    }

    @Test
    fun lookupTest() = runBlocking {
        data class Comment(@BsonId val id: ObjectId, val name: String, val email: String, val movie_id: Int, val text: String, val date: LocalDateTime)
        val commentCollection = database.getCollection<Comment>("comments")

        val comments = listOf(
            Comment(id = ObjectId(), name = "John Doe", email = "john_doe@fakegmail.com", movie_id = 1, text = "This is a great movie. I enjoyed it a lot.", date = Instant.ofEpochMilli(1332804020000).atZone(ZoneId.systemDefault()).toLocalDateTime()),
            Comment(id = ObjectId(), name = "Andrea Le", email = "andrea_le@fakegmail.com", movie_id = 1, text = "Rem officiis eaque repellendus amet eos doloribus.", date = Instant.ofEpochMilli(1332804016000).atZone(ZoneId.systemDefault()).toLocalDateTime()),
            Comment(id = ObjectId(), name = "Jane Doe", email = "jane_doe@fakegmail.com", movie_id = 2, text = "I didn't like this movie. It was boring and predictable.", date = Instant.ofEpochMilli(1332804024000).atZone(ZoneId.systemDefault()).toLocalDateTime()),
            Comment(id = ObjectId(), name = "Bob Smith", email = "bob_smith@fakegmail.com", movie_id = 3, text = "This movie is a masterpiece. Highly recommended.", date = Instant.ofEpochMilli(1332804028000).atZone(ZoneId.systemDefault()).toLocalDateTime()))
        commentCollection.insertMany(comments)

        val lookup = movieCollection.aggregate<Document>(listOf(
            // :snippet-start: lookup
            Aggregates.lookup("comments", "_id", "movie_id", "joined_comments")
            // :snippet-end:
        ))
        assertEquals(9, lookup.toList().size)
        assertEquals("The Shawshank Redemption", lookup.toList().first()["title"])
    }

    @Test
    fun lookupFullJoinTest() = runBlocking {
        // :snippet-start: lookup-data-classes
        data class Order(
            @BsonId val id: Int,
            val customerId: Int,
            val item: String,
            val ordered: Int
        )
        data class Inventory(
            @BsonId val id: Int,
            val stock_item: String,
            val instock: Int
        )
        // :snippet-end:
        data class StockData(val instock: Int)
        data class Results(val item: String, val ordered: Int, val stockdata: List<StockData>)

        val orderCollection = database.getCollection<Order>("orders")
        val warehouseCollection = database.getCollection<Inventory>("warehouses")

        val orders = listOf(Order(1, 1, "item1", 5), Order(2, 1, "item2", 5), Order(3, 1, "item3", 5))
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
                    Projections.exclude("customerId", "stock_item"),
                    Projections.excludeId()
                )
            )
        )
        val innerJoinLookup =
            Aggregates.lookup("warehouses", variables, pipeline, "stockdata")
        // :snippet-end:
        val resultsCollection = database.getCollection<Results>("orders")
        val result = resultsCollection.aggregate<Results>(listOf(innerJoinLookup)).toList()
        println(result)
        val expected = listOf(
            Results("item1", 5, listOf(StockData(10))),
            Results("item2", 5, listOf(StockData(20))),
            Results("item3", 5, listOf(StockData(30)))
        )
      assertEquals(expected,result)
        warehouseCollection.drop()
        orderCollection.drop()
    }

    @Test
    fun groupTest() = runBlocking {
        data class Order(@BsonId val id: Int, val customerId: Int, val item: String, val ordered: Int)

        val orderCollection = database.getCollection<Order>("orders")
        val orders = listOf(Order(1, 1, "Item1", 1), Order(2, 1, "Item2", 10), Order(3, 2, "Item3", 5), Order(4, 2, "Item4" , 50))
        orderCollection.insertMany(orders)

        data class Results(val totalQuantity: Int, val averageQuantity: Double)

        val grouping = orderCollection.aggregate<Results>(listOf(
            // :snippet-start: group
            Aggregates.group("\$customerId",
                sum("totalQuantity", "\$ordered"),
                avg("averageQuantity", "\$ordered"))
            // :snippet-end:
        ))
        val expected = listOf(
            Results(11, 5.5),
            Results(55, 27.5)
        )
        assertEquals(expected, grouping.toList())
        orderCollection.drop()
    }

    @Test
    fun minNTest() = runBlocking {
        data class Results(val lowest_three_ratings: List<Double>)

        val resultsFlow = movieCollection.aggregate<Results>(listOf(
                // :snippet-start: minN
                Aggregates.group(
                    "\$year",
                    Accumulators.minN(
                        "lowest_three_ratings",
                        "\$imdb.rating",
                        3
                    )
                )
                // :snippet-end:
            ))
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun maxNTest() = runBlocking {
        data class Results(val highest_two_ratings: List<Double>)

        val resultsFlow = movieCollection.aggregate<Results>(listOf(
                // :snippet-start: maxN
                Aggregates.group(
                    "\$year",
                    Accumulators.maxN(
                        "highest_two_ratings",
                        "\$imdb.rating",
                        2
                    )
                )
                // :snippet-end:
            ))
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun firstNTest() = runBlocking {
        data class Results(val first_two_movies: List<String>)

        val resultsFlow = movieCollection.aggregate<Results>(listOf(
                // :snippet-start: firstN
                Aggregates.group(
                    "\$year",
                    Accumulators.firstN(
                        "first_two_movies",
                        "\$title",
                        2
                    )
                )
                // :snippet-end:
            ))
        assertEquals(5, resultsFlow.toList().size)

    }

    @Test
    fun lastNTest() = runBlocking {
        data class Results(val last_three_movies: List<String>)

        val resultsFlow = movieCollection.aggregate<Results>(listOf(
                // :snippet-start: lastN
                Aggregates.group(
                    "\$year",
                    Accumulators.lastN(
                        "last_three_movies",
                        "\$title",
                        3
                    )
                )
                // :snippet-end:
            )
        )
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun topTest() = runBlocking {
        data class TopRated(val title: BsonString, val rating: BsonDouble)
        data class Results(val top_rated_movie: List<TopRated>) // ERROR: Unable to decode top_rated_movie for Results data class

        val resultsFlow = movieCollection.aggregate<Document>(listOf(
                // :snippet-start: top
                Aggregates.group(
                    "\$year",
                    Accumulators.top(
                        "top_rated_movie",
                        Sorts.descending("imdb.rating"),
                        listOf("\$title", "\$imdb.rating")
                    )
                )
                // :snippet-end:
            ))
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun topNTest() = runBlocking {
        data class Longest(val title: String, val runtime: Int)
        data class Results(@BsonId val year: Int, val longest_three_movies: List<Longest>) // ERROR: Unable to decode longest_three_movies for Results data class

        val resultsFlow = movieCollection.aggregate<Document>(listOf(
                // :snippet-start: topN
                Aggregates.group(
                    "\$year",
                    Accumulators.topN(
                        "longest_three_movies",
                        Sorts.descending("runtime"),
                        listOf("\$title", "\$runtime"),
                        3
                    )
                )
                // :snippet-end:
            ))
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun bottomTest() = runBlocking {
        data class Shortest(val title: String, val runtime: Int)
        data class Results(val id: Int, val shortest_movies: List<Shortest>) // ERROR: Unable to decode shortest_movies for Results data class

        val resultsFlow = movieCollection.aggregate<Document>(listOf(
                // :snippet-start: bottom
                Aggregates.group(
                    "\$year",
                    Accumulators.bottom(
                        "shortest_movies",
                        Sorts.descending("runtime"),
                        listOf("\$title", "\$runtime")
                    )
                )
                // :snippet-end:
            ))
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun bottomNTest() = runBlocking {
        data class LowestRated(val title: String, val runtime: Int)
        data class Results(val id: Int, val lowest_rated_two_movies: List<LowestRated>) // ERROR: Unable to decode lowest_rated_two_movies for Results data class

        val resultsFlow = movieCollection.aggregate<Document>(listOf(
                // :snippet-start: bottomN
                Aggregates.group(
                    "\$year",
                    Accumulators.bottom(
                        "lowest_rated_two_movies",
                        Sorts.descending("imdb.rating"),
                        listOf("\$title", "\$imdb.rating")
                    )
                )
                // :snippet-end:
            ))
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun unwindTest() = runBlocking {
        data class LowestRated(val title: String, val runtime: Int)
        data class Results(val id: Int, val lowest_rated_two_movies: List<LowestRated>) // ERROR: Unable to decode lowest_rated_two_movies for Results data class

        val resultsFlow = movieCollection.aggregate<Document>(listOf(Aggregates.group("\$year", Accumulators.bottom("lowest_rated_two_movies", Sorts.descending("imdb.rating"), listOf("\$title", "\$imdb.rating"))),
                // :snippet-start: unwind
                Aggregates.unwind("\$lowest_rated_two_movies")
                // :snippet-end:
            ))
        assertEquals(10, resultsFlow.toList().size)
    }

    @Test
    fun unwindOptionsTest() = runBlocking {
        data class LowestRated(val title: String, val runtime: Int)
        data class Results(val id: Int, val lowest_rated_two_movies: List<LowestRated>) // ERROR: Unable to decode lowest_rated_two_movies for Results data class

        val resultsFlow = movieCollection.aggregate<Document>(listOf(Aggregates.group("\$year", Accumulators.bottom("lowest_rated_two_movies", Sorts.descending("imdb.rating"), listOf("\$title", "\$imdb.rating"))),
                // :snippet-start: unwind-options
                Aggregates.unwind(
                    "\$lowest_rated_two_movies",
                    UnwindOptions().preserveNullAndEmptyArrays(true)
                )
                // :snippet-end:
            ))
        assertEquals(10, resultsFlow.toList().size)
    }

    @Test
    fun unwindOptionsArrayTest() = runBlocking {
        data class LowestRated(val title: String, val runtime: Int)
        data class Results(val id: Int, val lowest_rated_two_movies: List<LowestRated>) // ERROR: Unable to decode lowest_rated_two_movies for Results data class

        val resultsFlow = movieCollection.aggregate<Document>(listOf(Aggregates.group("\$year", Accumulators.bottom("lowest_rated_two_movies", Sorts.descending("imdb.rating"), listOf("\$title", "\$imdb.rating"))),
                // :snippet-start: unwind-options-array
                Aggregates.unwind(
                    "\$lowest_rated_two_movies",
                    UnwindOptions().includeArrayIndex("position")
                )
                // :snippet-end:
            ))
        assertEquals(10, resultsFlow.toList().size)
    }

    @Test
    fun outTest() = runBlocking {
        val resultsFlow = movieCollection.aggregate<Document>(listOf(Aggregates.group("\$year", Accumulators.bottom("lowest_rated_two_movies", Sorts.descending("imdb.rating"), listOf("\$title", "\$imdb.rating"))),
                // :snippet-start: out
                Aggregates.out("comments"),
                // :snippet-end:
            ))
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun mergeTest() = runBlocking {
        val resultsFlow = movieCollection.aggregate<Document>(listOf(
                Aggregates.group("\$year", Accumulators.bottom("lowest_rated_two_movies", Sorts.descending("imdb.rating"), listOf("\$title", "\$imdb.rating"))),
                // :snippet-start: merge
                Aggregates.merge("comments")
                // :snippet-end:
            ))
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun mergeOptionsTest() = runBlocking {
       // movieCollection.createIndex(Indexes.ascending("year", "title"))

        val resultsFlow = movieCollection.aggregate<Movie>(listOf(
            Aggregates.project(fields(Projections.computed("year", "\$_id - \$year"), Projections.include("title", "year"))),
                // :snippet-start: merge-options
                Aggregates.merge(
                    MongoNamespace("aggregation", "movies"), // ERROR: 'Cannot find index to verify that join fields will be unique'
                    MergeOptions().uniqueIdentifier(listOf("year", "title"))
                        .whenMatched(MergeOptions.WhenMatched.REPLACE)
                        .whenNotMatched(MergeOptions.WhenNotMatched.INSERT)
                )
            // :snippet-end:
            )
        )
        println(resultsFlow.toList())
     //   movieCollection.dropIndex(Indexes.ascending("year", "title"))
    }

    @Test
    fun graphLookupTest() = runBlocking {
        data class Results(val id: Int, val name: String, val reportsTo: String? = null, val reportingHierarchy: List<Employee>)
        val resultsFlow = employeesCollection.aggregate<Results>(listOf(
                // :snippet-start: graph-lookup
                Aggregates.graphLookup(
                    "employees", "\$reportsTo", "reportsTo", "name", "reportingHierarchy"
                )
                // :snippet-end:
            ))
        assertEquals("Dev", resultsFlow.toList()[0].name)
    }

    @Test
    fun graphLookupDepthTest() = runBlocking {
        data class Depth(val name: String, val degrees: Int)
        data class Results(val id: Int, val name: String, val reportsTo: String? = null, val reportingHierarchy: List<Depth>)
        val resultsFlow = employeesCollection.aggregate<Results>(listOf(
                // :snippet-start: graph-lookup-depth
                Aggregates.graphLookup(
                    "employees", "\$reportsTo", "reportsTo", "name", "reportingHierarchy",
                    GraphLookupOptions().maxDepth(2).depthField("degrees")
                )
                // :snippet-end:
            ))
        assertEquals("Dev", resultsFlow.toList()[0].name)
        assertEquals(0, resultsFlow.toList()[1].reportingHierarchy[0].degrees)
    }

    @Test
    fun graphLookupOptionsTest() = runBlocking {
        data class Results(val id: Int, val name: String, val reportsTo: String? = null, val reportingHierarchy: List<Employee>)
        val resultsFlow = employeesCollection.aggregate<Results>(listOf(
                // :snippet-start: graph-lookup-options
                Aggregates.graphLookup(
                    "employees", "\$reportsTo", "reportsTo", "name", "reportingHierarchy",
                    GraphLookupOptions().maxDepth(1).restrictSearchWithMatch(
                        Filters.eq("department", "Engineering")
                    )
                )
                // :snippet-end:
            ))
        assertEquals("Dev", resultsFlow.toList()[0].name)
        assertEquals(0, resultsFlow.toList()[1].reportingHierarchy.size)
    }

    @Test
    fun sortByCount() = runBlocking {
        data class Results(@BsonId val id: String, val count: Int)

        val resultsFlow = movieCollection.aggregate<Results>(listOf(Aggregates.unwind("\$genres"),
            // :snippet-start: sort-by-count
            Aggregates.sortByCount("\$genres"),
            // :snippet-end:
            Aggregates.sort(Sorts.descending("count", "_id"))))
        val results = resultsFlow.toList()
        val actual = listOf(Results("Drama", 8), Results("Crime", 3), Results("Action", 2), Results("Thriller", 1), Results("Sci-Fi", 1), Results("Romance", 1), Results("Mystery", 1),)
        assertEquals(results, actual)
    }

    @Test
    fun replaceRootTest() = runBlocking {
        data class Book(val id: Int, val spanishTranslation: Map<String, String>)
        val bookCollection = database.getCollection<Book>("books")
        val books = listOf(Book(1, mapOf("Book1" to "Libro1")), Book(2, mapOf("Book2" to "Libro2")), Book(3, mapOf("Book3" to "Libro3")))
        bookCollection.insertMany(books)

        val resultsFlow = bookCollection.aggregate<Document>(listOf(
            // :snippet-start: replace-root
            replaceRoot("\$spanishTranslation")
            //  :snippet-end:

        ))
        assertEquals("Libro1", resultsFlow.toList()[0]["Book1"])
        bookCollection.drop()
    }

    @Test
    fun addFieldsTest() = runBlocking {
        data class Contact(@BsonId val id: Int, val firstName: String, val lastName: String)
        data class Results (@BsonId val id: Int, val firstName: String, val lastName: String, val city: String? = null, val state: String? = null)
        val contactsCollection = database.getCollection<Contact>("contacts")

        val contacts = listOf(Contact(1, "Gary", "Sheffield"), Contact(2, "Nancy", "Walker"), Contact(3,"Peter", "Sumner" ))
        contactsCollection.insertMany(contacts)

        val resultsFlow = contactsCollection.aggregate<Results>(listOf(
            // :snippet-start: add-fields
            addFields(Field("city", "New York City"), Field("state", "NY"))
            // :snippet-end:
        ))
        assertEquals("New York City", resultsFlow.toList()[0].city)
        contactsCollection.drop()
    }

    @Test
    fun countTest() = runBlocking {
        data class Results(val total: Int)
        val resultsFlow = movieCollection.aggregate<Results>(listOf(
                // :snippet-start: count
                Aggregates.count("total")
                // :snippet-end:
            ))
        assertEquals(9, movieCollection.countDocuments())
    }

    @Test
    fun bucketTest() = runBlocking {
        val resultsFlow = screenCollection.aggregate<Document>(listOf(
            // :snippet-start: bucket
            bucket("\$screenSize", listOf(0, 24, 32, 50, 70, 1000))
            // :snippet-end:
        ))
        assertEquals(4, resultsFlow.toList().size)
    }

    @Test
    fun bucketOptionsTest() = runBlocking {
        val resultsFlow = screenCollection.aggregate<Document>(listOf(
            // :snippet-start: bucket-options
            bucket("\$screenSize", listOf(0, 24, 32, 50, 70),
                BucketOptions()
                    .defaultBucket("monster")
                    .output(
                        sum("count", 1),
                        push("matches", "\$screenSize")
                    )
            )
            // :snippet-end:
        ))
        assertEquals(4, resultsFlow.toList().size)
    }

    @Test
    fun bucketAutoTest() = runBlocking {
        val resultsFlow = screenCollection.aggregate<Document>(listOf(
            // :snippet-start: bucket-auto
            bucketAuto("\$screenSize", 5)
            // :snippet-end:
        ))
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun bucketAutoOptionsTest() = runBlocking {
        val resultsFlow = screenCollection.aggregate<Document>(listOf(
            // :snippet-start: bucket-auto-options
            bucketAuto(
                "\$price", 5,
                BucketAutoOptions()
                    .granularity(BucketGranularity.POWERSOF2)
                    .output(sum("count", 1), avg("avgPrice", "\$price")
                    )
            )
            // :snippet-end:
        ))
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun facetTest() = runBlocking {
        val resultsFlow = screenCollection.aggregate<Document>(listOf(
            // :snippet-start: facet
            facet(
                Facet(
                    "Screen Sizes",
                    bucketAuto(
                        "\$screenSize",
                        5,
                        BucketAutoOptions().output(sum("count", 1))
                    )
                ),
                Facet(
                    "Manufacturer",
                    sortByCount("\$manufacturer"),
                    limit(5)
                )
            )
            // :snippet-end:
        ))
        assertEquals(1, resultsFlow.toList().size)
    }

    @Test
    fun windowTest() = runBlocking {
        // :snippet-start: window-data-class
        data class Weather(
            val localityId: String,
            val measurementDateTime: Date,
            val rainfall: Double,
            val temperature: Double
        )
        // :snippet-end:

        val weatherCollection = database.getCollection<Weather>("weather")
        val weather = listOf(Weather("1", Date(), 50.2, 25.6), Weather("1", Date(), 40.5, 28.3), Weather("1", Date(), 60.1, 23.8), Weather("2", Date(), 45.7, 26.4), Weather("2", Date(), 55.9, 24.9))
        weatherCollection.insertMany(weather)

        // :snippet-start: window
        val pastMonth = Windows.timeRange(-1, MongoTimeUnit.MONTH, Windows.Bound.CURRENT)

        val resultsFlow = weatherCollection.aggregate<Document>(listOf(
               Aggregates.setWindowFields("\$localityId",
                   Sorts.ascending("measurementDateTime"),
                   WindowOutputFields.sum("monthlyRainfall", "\$rainfall", pastMonth),
                   WindowOutputFields.avg("monthlyAvgTemp", "\$temperature", pastMonth)
               )
        // :snippet-end:
        ))
        assertEquals(5, resultsFlow.toList().size)
        weatherCollection.drop()
    }

    @Test
    fun fillTest() = runBlocking {
        // :snippet-start: fill-data-class
        data class Weather(
            @BsonId val id: ObjectId = ObjectId(),
            val hour: Int,
            val temperature: String?,
            val air_pressure: Double?
        )
        // :snippet-end:
        val weatherCollection = database.getCollection<Weather>("weather")
        val weather = listOf(Weather(ObjectId(), 1, "23C", 29.74), Weather(ObjectId(), 2, "23.5C", null), Weather(ObjectId(), 3, null, 29.76))
        weatherCollection.insertMany(weather)
        // :snippet-start: fill
        val resultsFlow = weatherCollection.aggregate<Weather>(listOf(
            Aggregates.fill(
                FillOptions.fillOptions().sortBy(ascending("hour")),
                FillOutputField.value("temperature", "23.6C"),
                FillOutputField.linear("air_pressure")
            )
        ))
        resultsFlow.collect { println(it) }
        // :snippet-end:
        assertEquals(29.75, resultsFlow.toList()[1].air_pressure)
    }

    @Test
    fun densifyTest() = runBlocking {
        // :snippet-start: densify-data-class
        data class Weather(
            @BsonId val id: ObjectId = ObjectId(),
            val position: Point,
            val ts: LocalDateTime
        )
        // :snippet-end:

        val weatherCollection = database.getCollection<Weather>("weather")
        val weather = listOf(
            Weather(ObjectId(), Point(Position(-47.9, 47.6)), LocalDateTime.of(1984, 3 ,5, 8 ,0)),
            Weather(ObjectId(), Point(Position(-47.9, 47.6)), LocalDateTime.of(1984, 3 ,5, 9 ,0)),
        )
        weatherCollection.insertMany(weather)
        val resultsFlow = weatherCollection.aggregate<Document>(listOf(
            // :snippet-start: densify
            densify(
                "ts",
                DensifyRange.partitionRangeWithStep(15, MongoTimeUnit.MINUTE),
                DensifyOptions.densifyOptions().partitionByFields("position.coordinates"))
            // :snippet-end:
        ))
        resultsFlow.collect{println(it)}

        weatherCollection.drop()
    }

    @Test
    fun fullTextSearchTest(): Unit = runBlocking {
        ftsCollection.insertOne(Movie(id = 1, title = "Back to the Future", year = 1985, cast = listOf("Michael J. Fox", "Christopher Lloyd"), genres = listOf("Adventure", "Comedy", "Sci-Fi"), type = "movie", rated = "PG", plot = "Marty McFly, a 17-year-old high school student, is accidentally sent thirty years into the past in a time-traveling DeLorean.", fullplot = "Marty McFly, a 17-year-old high school student, is accidentally sent thirty years into the past in a time-traveling DeLorean invented by his close friend, the eccentric scientist Doc Brown.", runtime = 116, imdb = Movie.IMDB(rating = 8.5)))
        // the Atlas search index is enabled on ftsCollection: db ("sample_mflix") and collection ("movies")
        val resultsFlow = ftsCollection.aggregate<Movie>(
            listOf(
                // :snippet-start: full-text-search
                Aggregates.search(
                    SearchOperator.text(
                        SearchPath.fieldPath("title"), "Future"
                    ),
                    SearchOptions.searchOptions().index("title")
                )
                // :snippet-end:
            )
        )
        resultsFlow.collect { println(it) }  // TODO figure out why this isn't returning anything
        ftsCollection.drop()
    }

    @Test
    fun searchMetadataTest() = runBlocking {
        ftsCollection.insertOne(Movie(id = 1, title = "Back to the Future", year = 1985, cast = listOf("Michael J. Fox", "Christopher Lloyd"), genres = listOf("Adventure", "Comedy", "Sci-Fi"), type = "movie", rated = "PG", plot = "Marty McFly, a 17-year-old high school student, is accidentally sent thirty years into the past in a time-traveling DeLorean.", fullplot = "Marty McFly, a 17-year-old high school student, is accidentally sent thirty years into the past in a time-traveling DeLorean invented by his close friend, the eccentric scientist Doc Brown.", runtime = 116, imdb = Movie.IMDB(rating = 8.5)))

        val resultsFlow = ftsCollection.aggregate<Document>(
            listOf(
                // :snippet-start: search-meta
                Aggregates.searchMeta(
                    SearchOperator.near(1985, 2, SearchPath.fieldPath("year"))
                )
                // :snippet-end:
            )
        )
        println(resultsFlow.toList()) // TODO figure out why this is returning 0 documents
        ftsCollection.drop()
    }

}