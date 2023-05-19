
import com.mongodb.client.model.*
import com.mongodb.client.model.Accumulators.*
import com.mongodb.client.model.Aggregates.*
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
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import java.time.LocalDateTime
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
                        Projections.computed(Results::rating.name, "\$${Movie::rated.name}"),
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
                        Document(Movie::title.name, "Steel Magnolias"),
                        Document(Movie::title.name, "Back to the Future"),
                        Document(Movie::title.name, "Jurassic Park")
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
        data class Comment(@BsonId val id: ObjectId, val name: String, val movieId: Int, val text: String)
        data class Results(@BsonId val id: Int, val title: String, val year: Int, val cast: List<String>, val genres: List<String>, val type: String, val rated: String, val plot: String, val fullplot: String, val runtime: Int, val imdb: Movie.IMDB, val joinedComments: List<Comment>)
        val commentCollection = database.getCollection<Comment>("comments")

        val comments = listOf(
            Comment(id = ObjectId(), name = "John Doe", movieId = 1, text = "This is a great movie. I enjoyed it a lot."),
            Comment(id = ObjectId(), name = "Andrea Le", movieId = 1, text = "Rem officiis eaque repellendus amet eos doloribus."),
            Comment(id = ObjectId(), name = "Jane Doe", movieId = 2, text = "I didn't like this movie. It was boring and predictable."),
            Comment(id = ObjectId(), name = "Bob Smith", movieId = 3, text = "This movie is a masterpiece. Highly recommended."))
        commentCollection.insertMany(comments)

        val lookup = movieCollection.aggregate<Document>(listOf(
            // :snippet-start: lookup
            Aggregates.lookup("comments", "_id", Comment::movieId.name, Results::joinedComments.name
            // :snippet-end:
        )))
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
            val stockItem: String,
            val inStock: Int
        )
        // :snippet-end:
        data class StockData(val inStock: Int)
        data class Results(val item: String, val ordered: Int, val stockData: List<StockData>)

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
                        Document("\$eq", listOf("$\$order_item", "\$${Inventory::stockItem.name}")),
                        Document("\$gte", listOf("\$${Inventory::inStock.name}", "$\$order_qty"))
                    ))
                )
            ),
            Aggregates.project(
                Projections.fields(
                    Projections.exclude(Order::customerId.name, Inventory::stockItem.name),
                    Projections.excludeId()
                )
            )
        )
        val innerJoinLookup =
            Aggregates.lookup("warehouses", variables, pipeline, Results::stockData.name)
        // :snippet-end:
        val resultsCollection = database.getCollection<Results>("orders")
        val result = resultsCollection.aggregate<Results>(listOf(innerJoinLookup)).toList()
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
                sum(Results::totalQuantity.name, "\$ordered"),
                avg(Results::averageQuantity.name, "\$ordered"))
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
        data class Results(val lowestThreeRatings: List<Double>)

        val resultsFlow = movieCollection.aggregate<Results>(listOf(
                // :snippet-start: minN
                Aggregates.group(
                    "\$${Movie::year.name}",
                    Accumulators.minN(
                        Results::lowestThreeRatings.name,
                        "\$${Movie::imdb.name}.${Movie.IMDB::rating.name}",
                        3
                    )
                )
                // :snippet-end:
            , Aggregates.sort(Sorts.descending(Movie::year.name))))
        assertEquals(8.9, resultsFlow.toList().first().lowestThreeRatings.first())
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun maxNTest() = runBlocking {
        data class Results(val highestTwoRatings: List<Double>)

        val resultsFlow = movieCollection.aggregate<Results>(listOf(
                // :snippet-start: maxN
                Aggregates.group(
                    "\$${Movie::year.name}",
                    Accumulators.maxN(
                        Results::highestTwoRatings.name,
                        "\$${Movie::imdb.name}.${Movie.IMDB::rating.name}",
                        2
                    )
                )
                // :snippet-end:
        ))
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun firstNTest() = runBlocking {
        data class Results(val firstTwoMovies: List<String>)

        val resultsFlow = movieCollection.aggregate<Results>(listOf(
                // :snippet-start: firstN
                Aggregates.group(
                    "\$${Movie::year.name}",
                    Accumulators.firstN(
                        Results::firstTwoMovies.name,
                        "\$${Movie::title.name}",
                        2
                    )
                )
                // :snippet-end:
            ))
        println(resultsFlow.toList())
        assertEquals(5, resultsFlow.toList().size)

    }

    @Test
    fun lastNTest() = runBlocking {
        data class Results(val lastThreeMovies: List<String>)

        val resultsFlow = movieCollection.aggregate<Results>(listOf(
                // :snippet-start: lastN
                Aggregates.group(
                    "\$${Movie::year.name}",
                    Accumulators.lastN(
                        Results::lastThreeMovies.name,
                        "\$${Movie::title.name}",
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
        data class TopRated(val title: String, val rating: Movie.IMDB)
        data class Results(@BsonId val id: Int, val topRatedMovie: List<TopRated>) // TODO ERROR: Unable to decode topRatedMovie for Results data class (Document{{_id=1972, topRatedMovie=[The Godfather, 8.9]}})

        val resultsFlow = movieCollection.aggregate<Results>(listOf(
                // :snippet-start: top
                Aggregates.group(
                    "\$${Movie::year.name}",
                    Accumulators.top(
                        Results::topRatedMovie.name,
                        Sorts.descending("${Movie::imdb.name}.${Movie.IMDB::rating.name}"),
                        listOf("\$${Movie::title.name}", "\$${Movie::imdb.name}.${Movie.IMDB::rating.name}")
                    )
                )
                // :snippet-end:
            ))
        println(resultsFlow.toList())
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun topNTest() = runBlocking {
        data class Longest(val title: String, val runtime: Int)
        data class Results(@BsonId val year: Int, val longestThreeMovies: List<Longest>) // TODO ERROR: Unable to decode longestThreeMovies for Results data class

        val resultsFlow = movieCollection.aggregate<Document>(listOf(
                // :snippet-start: topN
                Aggregates.group(
                    "\$${Movie::year.name}",
                    Accumulators.topN(
                        Results::longestThreeMovies.name,
                        Sorts.descending(Movie::runtime.name),
                        listOf("\$${Movie::title.name}", "\$${Movie::runtime.name}"),
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
        data class Results(val id: Int, val shortestMovies: List<Shortest>) // TODO ERROR: Unable to decode shortestMovies for Results data class

        val resultsFlow = movieCollection.aggregate<Document>(listOf(
                // :snippet-start: bottom
                Aggregates.group(
                    "\$${Movie::year.name}",
                    Accumulators.bottom(
                        Results::shortestMovies.name,
                        Sorts.descending("${Movie::runtime.name}"),
                        listOf("\$${Movie::title.name}", "\$${Movie::runtime.name}")
                    )
                )
                // :snippet-end:
            ))
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun bottomNTest() = runBlocking {
        data class LowestRated(val title: String, val runtime: Int)
        data class Results(@BsonId val id: Int, val lowestRatedTwoMovies: List<LowestRated>) // TODO ERROR: Unable to decode lowestRatedTwoMovies for Results data class

        val resultsFlow = movieCollection.aggregate<Document>(listOf(
                // :snippet-start: bottomN
                Aggregates.group(
                    "\$${Movie::year.name}",
                    Accumulators.bottom(
                        Results::lowestRatedTwoMovies.name,
                        Sorts.descending("${Movie::imdb.name}.${Movie.IMDB::rating.name}"),
                        listOf("\$${Movie::title.name}", "\$${Movie::imdb.name}.${Movie.IMDB::rating.name}"),
                    )
                )
                // :snippet-end:
            ))
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun unwindTest() = runBlocking {
        data class LowestRated(val title: String, val runtime: Int)
        data class Results(@BsonId val id: Int, val lowestRatedTwoMovies: List<LowestRated>) // TODO ERROR: Unable to decode lowestRatedTwoMovies for Results data class

        val resultsFlow = movieCollection.aggregate<Document>(listOf(
            // aggregate content to unwind
            Aggregates.group("\$${Movie::year.name}", Accumulators.bottom(Results::lowestRatedTwoMovies.name, Sorts.descending("${Movie::imdb.name}.${Movie.IMDB::rating.name}"), listOf("\$${Movie::title.name}", "\$${Movie::imdb.name}.${Movie.IMDB::rating.name}"))),
                // :snippet-start: unwind
                Aggregates.unwind("\$${Results::lowestRatedTwoMovies.name}")
                // :snippet-end:
            ))
        assertEquals(10, resultsFlow.toList().size)
    }

    @Test
    fun unwindOptionsTest() = runBlocking {
        data class LowestRated(val title: String, val runtime: Int)
        data class Results(val id: Int, val lowestRatedTwoMovies: List<LowestRated>) // TODO ERROR: Unable to decode lowestRatedTwoMovies for Results data class

        val resultsFlow = movieCollection.aggregate<Document>(listOf(
            // aggregate content to unwind
            Aggregates.group("\$${Movie::year.name}", Accumulators.bottom(Results::lowestRatedTwoMovies.name, Sorts.descending("${Movie::imdb.name}.${Movie.IMDB::rating.name}"), listOf("\$${Movie::title.name}", "\$${Movie::imdb.name}.${Movie.IMDB::rating.name}"))),
                // :snippet-start: unwind-options
                Aggregates.unwind(
                    "\$${Results::lowestRatedTwoMovies.name}",
                    UnwindOptions().preserveNullAndEmptyArrays(true)
                )
                // :snippet-end:
            ))
        assertEquals(10, resultsFlow.toList().size)
    }

    @Test
    fun unwindOptionsArrayTest() = runBlocking {
        data class LowestRated(val title: String, val runtime: Int)
        data class Results(val id: Int, val lowestRatedTwoMovies: List<LowestRated>) // TODO ERROR: Unable to decode lowestRatedTwoMovies for Results data class

        val resultsFlow = movieCollection.aggregate<Document>(listOf(
            // aggregate content to unwind
            Aggregates.group("\$${Movie::year.name}", Accumulators.bottom(Results::lowestRatedTwoMovies.name, Sorts.descending("${Movie::imdb.name}.${Movie.IMDB::rating.name}"), listOf("\$${Movie::title.name}", "\$${Movie::imdb.name}.${Movie.IMDB::rating.name}"))),
            // :snippet-start: unwind-options-array
                Aggregates.unwind(
                    "\$${Results::lowestRatedTwoMovies.name}",
                    UnwindOptions().includeArrayIndex("position")
                )
                // :snippet-end:
            ))
        assertEquals(10, resultsFlow.toList().size)
    }

    @Test
    fun outTest() = runBlocking {
        data class LowestRated(val title: String, val runtime: Int)
        data class Results(val id: Int, val lowestRatedTwoMovies: List<LowestRated>) // TODO ERROR: Unable to decode lowestRatedTwoMovies for Results data class

        val resultsFlow = movieCollection.aggregate<Document>(listOf(
            // aggregate content to push out
            Aggregates.group("\$${Movie::year.name}", Accumulators.bottom(Results::lowestRatedTwoMovies.name, Sorts.descending("${Movie::imdb.name}.${Movie.IMDB::rating.name}"), listOf("\$${Movie::title.name}", "\$${Movie::imdb.name}.${Movie.IMDB::rating.name}"))),
                // :snippet-start: out
                Aggregates.out("comments"),
                // :snippet-end:
            ))
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun mergeTest() = runBlocking {
        data class LowestRated(val title: String, val runtime: Int)
        data class Results(val id: Int, val lowestRatedTwoMovies: List<LowestRated>) // TODO ERROR: Unable to decode lowestRatedTwoMovies for Results data class

        val resultsFlow = movieCollection.aggregate<Document>(listOf(
            // aggregate content to merge
            Aggregates.group("\$${Movie::year.name}", Accumulators.bottom(Results::lowestRatedTwoMovies.name, Sorts.descending("${Movie::imdb.name}.${Movie.IMDB::rating.name}"), listOf("\$${Movie::title.name}", "\$${Movie::imdb.name}.${Movie.IMDB::rating.name}"))),
                // :snippet-start: merge
                Aggregates.merge("comments")
                // :snippet-end:
            ))
        assertEquals(5, resultsFlow.toList().size)
    }

//    @Test
//    fun mergeOptionsTest() = runBlocking {
//       movieCollection.createIndex(Indexes.ascending("year", "title")) // not needed?
//
//        val resultsFlow = movieCollection.aggregate<Movie>(listOf(
//            Aggregates.project(fields(Projections.computed("year", "\$_id - \$year"), Projections.include("title", "year"))), // trying to make the index unique
//                // :snippet-start: merge-options
//                Aggregates.merge(
//                    MongoNamespace("aggregation", "movies"), // TODO ERROR: 'Cannot find index to verify that join fields will be unique'
//                    MergeOptions().uniqueIdentifier(listOf("year", "title"))
//                        .whenMatched(MergeOptions.WhenMatched.REPLACE)
//                        .whenNotMatched(MergeOptions.WhenNotMatched.INSERT)
//                )
//            // :snippet-end:
//            )
//        )
//        println(resultsFlow.toList())
//        movieCollection.dropIndex(Indexes.ascending("year", "title"))
//    }

    @Test
    fun graphLookupTest() = runBlocking {
        data class Results(val id: Int, val name: String, val reportsTo: String? = null, val reportingHierarchy: List<Employee>)
        val resultsFlow = employeesCollection.aggregate<Results>(listOf(
                // :snippet-start: graph-lookup
                Aggregates.graphLookup(
                    "employees",
                    "\$${Employee::reportsTo.name}", Employee::reportsTo.name, Employee::name.name, Results::reportingHierarchy.name
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
                    "employees",
                    "\$${Employee::reportsTo.name}", Employee::reportsTo.name, Employee::name.name, Results::reportingHierarchy.name,
                    GraphLookupOptions().maxDepth(2).depthField(Depth::degrees.name)
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
                    "employees",
                    "\$${Employee::reportsTo.name}", Employee::reportsTo.name, Employee::name.name, Results::reportingHierarchy.name,
                    GraphLookupOptions().maxDepth(1).restrictSearchWithMatch(
                        Filters.eq(Employee::department.name, "Engineering")
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

        val resultsFlow = movieCollection.aggregate<Results>(listOf(Aggregates.unwind("\$${Movie::genres.name}"),
            // :snippet-start: sort-by-count
            Aggregates.sortByCount("\$${Movie::genres.name}"),
            // :snippet-end:
            Aggregates.sort(Sorts.descending(Results::count.name, "_id"))))
        val results = resultsFlow.toList()
        val actual = listOf(Results("Drama", 8), Results("Crime", 3), Results("Action", 2), Results("Thriller", 1), Results("Sci-Fi", 1), Results("Romance", 1), Results("Mystery", 1),)
        assertEquals(results, actual)
    }

    @Test
    fun replaceRootTest() = runBlocking {
        data class Movie(val id: Int, val spanishTranslation: Map<String, String>)
        val translateCollection = database.getCollection<Movie>("movies_translate")
        val movies = listOf(Movie(1, mapOf("Movie1" to "Película1")), Movie(2, mapOf("Movie2" to "Película2")), Movie(3, mapOf("Movie3" to "Película3")))
        translateCollection.insertMany(movies)

        val resultsFlow = translateCollection.aggregate<Document>(listOf(
            // :snippet-start: replace-root
            replaceRoot("\$${Movie::spanishTranslation.name}")
            //  :snippet-end:

        ))
        assertEquals("Película1", resultsFlow.toList()[0]["Movie1"])
        translateCollection.drop()
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
            addFields(Field(Results::city.name, "New York City"), Field(Results::state.name, "NY"))
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
                Aggregates.count(Results::total.name)
                // :snippet-end:
            ))
        assertEquals(9, movieCollection.countDocuments())
    }

    @Test
    fun bucketTest() = runBlocking {
        val resultsFlow = screenCollection.aggregate<Document>(listOf(
            // :snippet-start: bucket
            bucket("\$${Screen::screenSize.name}", listOf(0, 24, 32, 50, 70, 1000))
            // :snippet-end:
        ))
        assertEquals(4, resultsFlow.toList().size)
    }

    @Test
    fun bucketOptionsTest() = runBlocking {
        data class Results(val count: Int, val matches: List<Int>)
        val resultsFlow = screenCollection.aggregate<Results>(listOf(
            // :snippet-start: bucket-options
            bucket("\$${Screen::screenSize.name}", listOf(0, 24, 32, 50, 70),
                BucketOptions()
                    .defaultBucket("monster")
                    .output(
                        sum(Results::count.name, 1),
                        push(Results::matches.name, "\$${Screen::screenSize.name}")
                    )
            )
            // :snippet-end:
        ))
        assertEquals(4, resultsFlow.toList().size)
    }

    @Test
    fun bucketAutoTest() = runBlocking {
        data class MinMax(val min: Int, val max: Int)
        data class Results(@BsonId val id: List<MinMax>, val count: Int)
        val resultsFlow = screenCollection.aggregate<Document>(listOf( // TODO: figure out data class (Document{{_id=Document{{min=15, max=27}}, count=1}})
            // :snippet-start: bucket-auto
            bucketAuto("\$${Screen::screenSize.name}", 5)
            // :snippet-end:
        ))
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun bucketAutoOptionsTest() = runBlocking {
        data class MinMax(val min: Int, val max: Int)
        data class Bucket(val minMax: List<MinMax>, val count: Int, val avgPrice: Double)
        data class Results(@BsonId val id: List<Bucket>)
        val resultsFlow = screenCollection.aggregate<Document>(listOf( // TODO: figure out data class ([Document{{_id=Document{{min=64, max=128}}, count=1, avgPrice=100.0}})
            // :snippet-start: bucket-auto-options
            bucketAuto(
                "\$${Screen::price.name}", 5,
                BucketAutoOptions()
                    .granularity(BucketGranularity.POWERSOF2)
                    .output(sum(Bucket::count.name, 1), avg(Bucket::avgPrice.name, "\$${Screen::price.name}"))
                    )
            // :snippet-end:
        ))
        assertEquals(5, resultsFlow.toList().size)
    }

    @Test
    fun facetTest() = runBlocking {

        val resultsFlow = screenCollection.aggregate<Document>(listOf( // TODO: figure out data class (Document{{Screen Sizes=[Document{{_id=Document{{min=15, max=27}}, count=1}})
            // :snippet-start: facet
            facet(
                Facet(
                    "Screen Sizes",
                    bucketAuto(
                        "\$${Screen::screenSize.name}",
                        5,
                        BucketAutoOptions().output(sum("count", 1))
                    )
                ),
                Facet(
                    "Manufacturer",
                    sortByCount("\$${Screen::manufacturer.name}"),
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
        data class Results(val localityId: String, val measurementDateTime: Date, val rainfall: Double, val temperature: Double, val monthlyRainfall: Double, val monthlyAvgTemp: Double)

        val weatherCollection = database.getCollection<Weather>("weather")
        val weather = listOf(Weather("1", Date(), 50.2, 25.6), Weather("1", Date(), 40.5, 28.3), Weather("1", Date(), 60.1, 23.8), Weather("2", Date(), 45.7, 26.4), Weather("2", Date(), 55.9, 24.9))
        weatherCollection.insertMany(weather)

        // :snippet-start: window
        val pastMonth = Windows.timeRange(-1, MongoTimeUnit.MONTH, Windows.Bound.CURRENT)

        val resultsFlow = weatherCollection.aggregate<Results>(listOf(
               Aggregates.setWindowFields("\$${Weather::localityId.name}",
                   Sorts.ascending("${Weather::measurementDateTime.name}"),
                   WindowOutputFields.sum(Results::monthlyRainfall.name, "\$${Weather::rainfall.name}", pastMonth),
                   WindowOutputFields.avg(Results::monthlyAvgTemp.name, "\$${Weather::temperature.name}", pastMonth)
               )
        // :snippet-end:
        ))
        println(resultsFlow.toList())
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
            val airPressure: Double?
        )
        // :snippet-end:
        val weatherCollection = database.getCollection<Weather>("weather")
        val weather = listOf(Weather(ObjectId(), 1, "23C", 29.74), Weather(ObjectId(), 2, "23.5C", null), Weather(ObjectId(), 3, null, 29.76))
        weatherCollection.insertMany(weather)
        // :snippet-start: fill
        val resultsFlow = weatherCollection.aggregate<Weather>(listOf(
            Aggregates.fill(
                FillOptions.fillOptions().sortBy(ascending("${Weather::hour.name}")),
                FillOutputField.value(Weather::temperature.name, "23.6C"),
                FillOutputField.linear(Weather::airPressure.name)
            )
        ))
        resultsFlow.collect { println(it) }
        // :snippet-end:
        assertEquals(29.75, resultsFlow.toList()[1].airPressure)
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
                DensifyOptions.densifyOptions().partitionByFields("position.coordinates")) // TODO: typesafe this?
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
                        SearchPath.fieldPath(Movie::title.name), "Future"
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
                    SearchOperator.near(1985, 2, SearchPath.fieldPath(Movie::year.name))
                )
                // :snippet-end:
            )
        )
        println(resultsFlow.toList()) // TODO figure out why this is returning 0 documents
        ftsCollection.drop()
    }

}