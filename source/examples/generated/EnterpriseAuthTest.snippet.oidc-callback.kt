val credential = MongoCredential.createOidcCredential(null)
    .withMechanismProperty("OIDC_CALLBACK") { Context context ->
        val accessToken = "..."
        OidcCallbackResult(accessToken)
    }
