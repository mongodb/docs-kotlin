package config

import io.github.cdimascio.dotenv.dotenv

data class Config(val connectionUri: String)

fun getConfig(): Config {
    val ci = System.getenv("CI")
    println("Value of CI is: $ci")
    if(ci == "true") {
        val connectionUri = System.getenv("CONNECTION_URI")
        println("Value of CONNECTION_URI is: $connectionUri")
        return Config(System.getenv("CONNECTION_URI"))
    }
    val env = dotenv()
    val connectionUri = env["MONGODB_CONNECTION_URI"]
    println("Value of MONGODB_CONNECTION_URI is: $connectionUri")
    return Config(connectionUri)
}
