# MongoDB Kotlin Driver Documentation

This repository contains documentation for the MongoDB Kotlin Driver.

## File JIRA Tickets

Please file issue reports or requests at the [Documentation Jira Project](https://jira.mongodb.org/browse/DOCS).

## Building API Documentation

Build the API documentation docs for any major and minor version releases.

We use [Dokka](https://github.com/Kotlin/dokka) to generate the API documentation and host them on GitHub Pages.

To execute a build, you must have Java JDK 17+ installed. You can use brew to install jEnv and OpenJDK to manage your Java versions:

```sh
brew install jenv
brew install openjdk@17
```

The output of the `openjdk@17` includes a command that starts with `sudo ln -sfn`. Execute this command and then run the following commands to add the JDK to your environment:

```sh
jenv add /Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home/
jenv global 17.0
```

Fork and clone the [`mongo-java-driver`](https://github.com/mongodb/mongo-java-driver) repository. Navigate to the repository and run the following `gradle` command:

```sh
./gradlew dokkaHtml
```

This should generate the docs in `./build/docs/`
Check out the upstream `gh-pages` branch:

```sh
git checkout gh-pages
```

If the branch doesn't exist, create a directory for the target version. For example:

```sh
mkdir -p 4.10/apidocs/
```

Move the contents of the `./build/docs/` directory into the target version directory. For example:

```sh
mv ./build/docs/* 4.10/apidocs/
```

Stage the newly-created docs in Git and ensure that you did not stage anything but the desired pages. For example,

```sh
git add 4.10/apidocs
git status
```

Commit and push your changes for review. Once the PR is merged to the upstream `gh-pages` branch, GitHub Pages will automatically deploy the API documentation.

To build the Kotlin API documentation along with all of the other JVM documentation (Java, Scala, RS), see the instructions in the `docs-java-other` [README file](https://github.com/mongodb/docs-java-other#building-api-documentation).

## Licenses

All documentation is available under the terms of a [Creative Commons License](https://creativecommons.org/licenses/by-nc-sa/3.0/).

The MongoDB Documentation Project is governed by the terms of the
[MongoDB Contributor Agreement](https://www.mongodb.com/legal/contributor-agreement).

-- The MongoDB Docs Team
