// Set up the MongoDB client and get the collection
suspend fun performTransaction(client: MongoClient) {
    client.startSession().use { session ->
        session.startTransaction() // Start the transaction
        try {
            val database = client.getDatabase("bank")

            val savingsColl = database.getCollection<SavingsAccount>("savings_accounts")
            savingsColl.findOneAndUpdate(
                session,
                SavingsAccount::accountId eq "9876",
                inc(SavingsAccount::amount, -100)
            )
         
            val checkingColl = database.getCollection<CheckingAccount>("checking_accounts")
            checkingColl.findOneAndUpdate(
                session,
                CheckingAccount::accountId eq "9876",
                inc(CheckingAccount::amount, 100)
            )
            // Commit the transaction
            session.commitTransaction()
            println("Transaction committed.")
        } catch (error: Exception) {
            println("An error occurred during the transaction: ${error.message}")
            // Abort the transaction
            session.abortTransaction() 
        }
    }
}

data class SavingsAccount(val accountId: String, val amount: Int)
data class CheckingAccount(val accountId: String, val amount: Int)

fun main() = runBlocking {
    val uri = "<connection string uri>"
    val client = MongoClient.create(uri)
    performTransaction(client)
}
