
import com.mongodb.*
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance
import java.util.*
import javax.security.auth.Subject
import javax.security.auth.login.LoginContext
import kotlin.test.*
// :replace-start: {
//    "terms": {
//       "USERNAME": "<username>",
//       "PASSWORD": "<password>",
//       "HOSTNAME": "\"<hostname>\"",
//       "PORT": "\"<port>\"",
//       "SOURCE": "\"$external\""
//    }
// }


/* NOTE: Enterprise authentication examples are currently untested.
*  Like the Java examples, we're relying on technical reviewers
*  to ensure accuracy.
*/

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class EnterpriseAuthTest {

    companion object {
        private val dotenv = dotenv()
        val CONNECTION_URI_PLACEHOLDER = dotenv["MONGODB_CONNECTION_URI"]
        val USERNAME = dotenv["MONGODB_USER_NAME"]
        val HOSTNAME = dotenv["MONGODB_HOST_NAME"]
        val PORT = dotenv["MONGODB_PORT"].toInt()
        val PASSWORD = dotenv["MONGODB_PASSWORD"]
        val SOURCE = dotenv["MONGODB_SOURCE"]
    }

    @Ignore
    fun createGSSAPICred() = runBlocking {
        // :snippet-start: auth-creds-gssapi
        val credential = MongoCredential.createGSSAPICredential(USERNAME)

        val settings = MongoClientSettings.builder()
                .applyToClusterSettings { builder ->
                    builder.hosts(listOf(ServerAddress(HOSTNAME, PORT)))
                }
                .credential(credential)
                .build()

        val mongoClient = MongoClient.create(settings)
        // :snippet-end:
    }

    @Ignore
    fun serviceNameKey() = runBlocking {
        // :snippet-start: service-name-key
        val credential = MongoCredential.createGSSAPICredential(USERNAME)
            .withMechanismProperty(MongoCredential.SERVICE_NAME_KEY, "myService")
        // :snippet-end:
    }

    @Ignore
    fun javaSubjectKey() = runBlocking {
        // :snippet-start: java-subject-key
        val loginContext = LoginContext("<LoginModule implementation from JAAS config>")
        loginContext.login()
        val subject: Subject = loginContext.subject

        val credential = MongoCredential.createGSSAPICredential(USERNAME)
            .withMechanismProperty(MongoCredential.JAVA_SUBJECT_KEY, subject)

        val settings = MongoClientSettings.builder()
                .applyToClusterSettings { builder ->
                    builder.hosts(listOf(ServerAddress(HOSTNAME, PORT)))
                }
                .credential(credential)
                .build()

        val mongoClient = MongoClients.create(settings)
        // :snippet-end:
    }

    @Ignore
    fun kerberosSubjectProvider() = runBlocking {
        // :snippet-start: kerberos-subject-provider
        /* All MongoClient instances sharing this instance of KerberosSubjectProvider
        will share a Kerberos ticket cache */
        val myLoginContext = "myContext"
        /* Login context defaults to "com.sun.security.jgss.krb5.initiate"
        if unspecified in KerberosSubjectProvider */
        val credential = MongoCredential.createGSSAPICredential(USERNAME)
            .withMechanismProperty(
                MongoCredential.JAVA_SUBJECT_PROVIDER_KEY,
                KerberosSubjectProvider(myLoginContext)
            )
        // :snippet-end:
        val settings = MongoClientSettings.builder()
                .applyToClusterSettings { builder ->
                    builder.hosts(listOf(ServerAddress(HOSTNAME, PORT)))
                }
                .credential(credential)
                .build()

        val mongoClient = MongoClients.create(settings)
    }



    @Ignore
    fun ldapCredential() = runBlocking {
        // :snippet-start: ldap-mongo-credential
        val credential = MongoCredential.createPlainCredential(USERNAME, SOURCE, PASSWORD.toCharArray())

        val settings = MongoClientSettings.builder()
            .applyToClusterSettings { builder ->
                builder.hosts(listOf(ServerAddress(HOSTNAME, PORT)))
            }
            .credential(credential)
            .build()

        val mongoClient = MongoClient.create(settings)
        // :snippet-end:
    }
// :replace-end:

    @Ignore
    fun gssapiConnectionString() = runBlocking {
        // :replace-start: {
        //    "terms": {
        //       "CONNECTION_URI_PLACEHOLDER": "\"<username>@<hostname>:<port>/?authSource=$external&authMechanism=GSSAPI\""
        //    }
        // }
        // :snippet-start: gssapi-connection-string
        val connectionString = ConnectionString(CONNECTION_URI_PLACEHOLDER)
        val mongoClient = MongoClient.create(connectionString)
        // :snippet-end:
        // :replace-end:
    }
    @Ignore
    fun gssapiPropertiesConnectionString() = runBlocking {
        // :replace-start: {
        //    "terms": {
        //       "CONNECTION_URI_PLACEHOLDER": "\"<username>@<hostname>:<port>/?authSource=$external&authMechanism=GSSAPI&authMechanismProperties=SERVICE_NAME:myService\""
        //    }
        // }
        // :snippet-start: gssapi-properties-connection-string
        val connectionString = ConnectionString(CONNECTION_URI_PLACEHOLDER)
        val mongoClient = MongoClient.create(connectionString)
        // :snippet-end:
        // :replace-end:
    }


    @Ignore
    fun ldapConnectionString() = runBlocking {
        // :replace-start: {
        //    "terms": {
        //       "CONNECTION_URI_PLACEHOLDER": "\"<username>:<password>@<hostname>:<port>/?authSource=$external&authMechanism=PLAIN\""
        //    }
        // }
        // :snippet-start: ldap-connection-string
        val connectionString = ConnectionString(CONNECTION_URI_PLACEHOLDER)
        val mongoClient = MongoClient.create(connectionString)
        // :snippet-end:
        // :replace-end:
    }
}

