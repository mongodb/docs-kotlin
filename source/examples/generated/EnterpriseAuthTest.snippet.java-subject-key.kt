val loginContext = LoginContext("<LoginModule implementation from JAAS config>")
loginContext.login()
val subject: Subject = loginContext.subject

val credential = MongoCredential.createGSSAPICredential("<Kerberos principal>")
    .withMechanismProperty(MongoCredential.JAVA_SUBJECT_KEY, subject)
