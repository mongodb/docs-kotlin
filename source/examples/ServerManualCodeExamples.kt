import com.mongodb.MongoException
import com.mongodb.client.model.Filters.*
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.Document

fun main() = runBlocking {
    val uri = "<connection string>"
    val mongoClient = MongoClient.create(uri)
    val database = mongoClient.getDatabase("db")
    val collection = database.getCollection<Document>("inventory")

    // Start Example 1
    val result = collection.insertOne(
        Document("item", "canvas")
            .append("qty", 100)
            .append("tags", listOf("cotton"))
            .append("size", Document("h", 28)
                .append("w", 35.5)
                .append("uom", "cm")
            )
    )
    // End Example 1

    println("Success! Inserted document: " + result.insertedId)

    // Start Example 2
    val flowInsertOne = collection.withDocumentClass<Document>()
        .find(eq("item", "canvas"))
        .firstOrNull()
    // End Example 2

    if (flowInsertOne == null) {
        println("No results found.");
    } else {
        println(flowInsertOne)
    }

    collection.deleteMany(empty())

    // Start Example 3
    val results = collection.insertMany(
        listOf(
            Document("item", "journal")
                .append("qty", 25)
                .append("tags", listOf("blank", "red"))
                .append("size", Document("h", 14)
                    .append("w", 21)
                    .append("uom", "cm")
                ),
            Document("item", "mat")
                .append("qty", 25)
                .append("tags", listOf("gray"))
                .append("size", Document("h", 27.9)
                    .append("w", 35.5)
                    .append("uom", "cm")
                ),
            Document("item", "mousepad")
                .append("qty", 25)
                .append("tags", listOf("gel", "blue"))
                .append("size", Document("h", 19)
                    .append("w", 22.85)
                    .append("uom", "cm")
                )
        )
    )
    // End Example 3

    println("Success! Inserted documents: " + results.insertedIds)

    // Start Example 7
    val flowInsertMany = collection.withDocumentClass<Document>()
        .find(empty())
    // End Example 7

    flowInsertMany.collect { println(it) }
    collection.deleteMany(empty())

    // Start Example 6
    collection.insertMany(
        listOf(
            Document("item", "journal")
                .append("qty", 25)
                .append("size", Document("h", 14)
                    .append("w", 21)
                    .append("uom", "cm")
                )
                .append("status", "A"),
            Document("item", "notebook")
                .append("qty", 50)
                .append("size", Document("h", 8.5)
                    .append("w", 11)
                    .append("uom", "in")
                )
                .append("status", "A"),
            Document("item", "paper")
                .append("qty", 100)
                .append("size", Document("h", 8.5)
                    .append("w", 11)
                    .append("uom", "in")
                )
                .append("status", "D"),
            Document("item", "planner")
                .append("qty", 75)
                .append("size", Document("h", 22.85)
                    .append("w", 30)
                    .append("uom", "cm")
                )
                .append("status", "D"),
            Document("item", "postcard")
                .append("qty", 45)
                .append("size", Document("h", 10)
                    .append("w", 15.25)
                    .append("uom", "cm")
                )
                .append("status", "A"),
        )
    )
    // End Example 6

    // Start Example 9
    val flowFindEquality = collection.withDocumentClass<Document>()
        .find(eq("status", "D"))
    // End Example 9

    flowFindEquality.collect { println(it) }

    // Start Example 10
    val flowFindOperator = collection.withDocumentClass<Document>()
        .find(`in`("status", "A", "D"))
    // End Example 10

    flowFindOperator.collect { println(it) }

    // Start Example 11
    val flowFindAND = collection.withDocumentClass<Document>()
        .find(and(eq("status", "A"), lt("qty", 30)))
    // End Example 11

    flowFindAND.collect { println(it) }

    // Start Example 12
    val flowFindOR = collection.withDocumentClass<Document>()
        .find(or(eq("status", "A"), lt("qty", 30)))
    // End Example 12

    flowFindOR.collect { println(it) }

    // Start Example 13
    val flowFindANDOR = collection.withDocumentClass<Document>()
        .find(
            and(eq("status", "A"),
                or(lt("qty", 30), regex("item", "^p")))
        )
    // End Example 13

    flowFindANDOR.collect { println(it) }
    collection.deleteMany(empty())

    // Start Example 14
    collection.insertMany(
        listOf(
            Document("item", "journal")
                .append("qty", 25)
                .append("size", Document("h", 14)
                    .append("w", 21)
                    .append("uom", "cm")
                )
                .append("status", "A"),
            Document("item", "notebook")
                .append("qty", 50)
                .append("size", Document("h", 8.5)
                    .append("w", 11)
                    .append("uom", "in")
                )
                .append("status", "A"),
            Document("item", "paper")
                .append("qty", 100)
                .append("size", Document("h", 8.5)
                    .append("w", 11)
                    .append("uom", "in")
                )
                .append("status", "D"),
            Document("item", "planner")
                .append("qty", 75)
                .append("size", Document("h", 22.85)
                    .append("w", 30)
                    .append("uom", "cm")
                )
                .append("status", "D"),
            Document("item", "postcard")
                .append("qty", 45)
                .append("size", Document("h", 10)
                    .append("w", 15.25)
                    .append("uom", "cm")
                )
                .append("status", "A"),
        )
    )
    // End Example 14

    // Start Example 17
    val flowFindNESTED = collection.withDocumentClass<Document>()
        .find(eq("size.uom", "in"))
    // End Example 17

    flowFindNESTED.collect { println(it) }

    // Start Example 18
    val flowFindEQNESTED = collection.withDocumentClass<Document>()
        .find(lt("size.h", 15))
    // End Example 18

    flowFindEQNESTED.collect { println(it) }

    // Start Example 19
    val flowFindANDNESTED = collection.withDocumentClass<Document>()
        .find(and(
            lt("size.h", 15),
            eq("size.uom", "in"),
            eq("status", "D")
        ))
    // End Example 19

    flowFindANDNESTED.collect { println(it) }

    // Start Example 15
    val flowFindEQDOC = collection.withDocumentClass<Document>()
        .find(eq("size", Document.parse("{ h: 14, w: 21, uom: 'cm' }")))
    // End Example 15

    flowFindEQDOC.collect { println(it) }

    // Start Example 16
    val flowFindEQDOC = collection.withDocumentClass<Document>()
        .find(eq("size", Document.parse("{ w: 21, h: 14, uom: 'cm' }")))
    // End Example 16

    collection.deleteMany(empty()) 

    // Start Example 20
    collection.insertMany(
        listOf(
            Document("item", "journal")
                .append("qty", 25)
                .append("tags", listOf("blank", "red"))
                .append("dim_cm", listOf(14, 21)),
            Document("item", "notebook")
                .append("qty", 50)
                .append("tags", listOf("red", "blank"))
                .append("dim_cm", listOf(14, 21)),
            Document("item", "paper")
                .append("qty", 100)
                .append("tags", listOf("red", "blank", "plain"))
                .append("dim_cm", listOf(14, 21)),
            Document("item", "planner")
                .append("qty", 75)
                .append("tags", listOf("blank", "red"))
                .append("dim_cm", listOf(22.85, 30)),
            Document("item", "postcard")
                .append("qty", 45)
                .append("tags", listOf("blue"))
                .append("dim_cm", listOf(10, 15.25)),
        )
    )
    // End Example 20

    // Start Example 21
    val flowFindARREQ = collection.withDocumentClass<Document>()
        .find(eq("tags", listOf("red", "blank")))
    // End Example 21

    // Start Example 22
    val flowFindARRALL = collection.withDocumentClass<Document>()
        .find(all("tags", listOf("red", "blank")))
    // End Example 22

    // Start Example 23
    val flowFindARRELEM = collection.withDocumentClass<Document>()
        .find(eq("tags", "red"))
    // End Example 23

    // Start Example 24
    val flowFindARRELEMOP = collection.withDocumentClass<Document>()
        .find(gt("dim_cm", 25))
    // End Example 24

    // Start Example 25
    val flowFindARRELEMCOND = collection.withDocumentClass<Document>()
        .find(and(gt("dim_cm", 15), lt("dim_cm", 20)))
    // End Example 25

    // Start Example 26
    val flowFindARRELEMMATCH = collection.withDocumentClass<Document>()
        .find(elemMatch("dim_cm", Document.parse("{ \$gt: 22, \$lt: 30 }")))
    // End Example 26

    // Start Example 27
    val flowFindARRPOS = collection.withDocumentClass<Document>()
        .find(gt("dim_cm.1", 25))
    // End Example 27

    // Start Example 28
    val flowFindARRSIZE = collection.withDocumentClass<Document>()
        .find(size("tags", 3))
    // End Example 28

    mongoClient.close()
}
