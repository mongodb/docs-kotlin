//                Aggregates.merge(
//                    MongoNamespace("media", "film"), // ERROR: 'Cannot find index to verify that join fields will be unique'
//                    MergeOptions().uniqueIdentifier(listOf("year", "title"))
//                        .whenMatched(MergeOptions.WhenMatched.REPLACE)
//                        .whenNotMatched(MergeOptions.WhenNotMatched.INSERT)
//                )
