
[ ![Download](https://api.bintray.com/packages/spectrum-project/konveyor/konveyor/images/download.svg?version=0.2.1) ](https://bintray.com/spectrum-project/konveyor/konveyor/0.2.1/link)

![](https://img.shields.io/github/issues/spectrum-project/konveyor.svg)
![](https://img.shields.io/github/forks/spectrum-project/konveyor.svg)
![](https://img.shields.io/github/stars/spectrum-project/konveyor.svg)
![](https://img.shields.io/github/license/spectrum-project/konveyor.svg)

![](https://img.shields.io/circleci/project/github/spectrum-project/konveyor/master.svg?label=master&style=plastic)
![](https://img.shields.io/circleci/project/github/spectrum-project/konveyor/dev.svg?label=dev&style=plastic)
[![codecov](https://codecov.io/gh/spectrum-project/konveyor/branch/master/graph/badge.svg)](https://codecov.io/gh/spectrum-project/konveyor)
# konveyor ETL Processor

## Description

Konveyor is a processor which realizes the conveyor belt software design pattern in a kotlin DSL-like 
style.

## What is the conveyor belt design pattern

The purpose of this pattern is designing of applications with complex but elastic logics. The elasticity 
of the pattern allows easily add/remove functionality without redesigning of entire application.

The central structure of the conveyor belt pattern is a Context containing all states of the workflow 
process. Suppose we have to handle a user request. Then, our Context must include the following entities.

```kotlin
data class MyContext(
    /**
     * User query in a form it came to us 
     */
    var rawQuery: UserQuery = UserQuery.EMPTY,
    
    /**
     * Result of validation, normalization and enrichement with default values 
     */
    var validatedQuery: UserQuery = UserQuery(),
    
    /**
     * A query to tha database to be performed
     */
    var dbQuery: DatabaseQuery = DatabaseQuery.EMPTY,
    
    /**
     * The result to be sent back to the requester
     */
    var dbResult: QueryResult = QueryResult()
)
```

Than, the workflow of the request handling is described with a Processor class and a set of Handlers.

```kotlin
val processor = MyProcessor(
    object: IHandler {
        override fun exec(context: MyContext) = with(context) {
            validatedQuery.field = makeValidationOnField(rawQuery.field)
        }
    },
    HandlerValidation2(),
    HandlerNormalization(),
    HandlerPrepareDbQuery(),
    HandlerPrepareResult()
)

val context = MyContext(rawQuery = aQueryFromUser)
processor.exec(context)
val result = context.result
```

You can notice that management of the workflow with such conveyor is as easy as adding/removing handlers.

### Using conveyor belt patterin with konveyor module

First of all, you need to configure your conveyor instance.

```kotlin
val conv  = conveyor<MyContext> {
    timeout { Duration.ofMillis(200) }

    // A simple handler
    exec { // this: MyContext -> 
        timeStart = Instant.now() 
    }

    // Detailed description of the handler
    handle { 
        timeout { Duration.ofMillis(10) }
        on { // this: MyContext -> 
            mode == MyContextMode.PROPER 
        }
        exec { registerQuery() }
    }

    // A nested conveyor for grouping purposes
    konveyor { // Standard logics: perform all where match = true
        timeout { Duration.ofMilliseconds(150) }
        exec { someProp = someFun() }
        handle {
            on { isOk() }
            exec { someFun() }
        }
    }
    
    // A nested conveyor with change of context
    subKonveyor<MySubContext> {
        // Splitter to create a sequence of nested context objects
        split {
            this.myContextCollection.asSequence()
        }
        
        // Normal handlers over nested context objects
        exec { someProp = someFun() }
        handle {
            on { isOk() }
            exec { someFun() }
        }
        
        // Joiner to join the series of nested context objects into main context 
        join { it: MySubContext ->
            this.myContextResult += it.someValue 
        }
    }

    // Perform only first handler with match = true
    // Under discussion
    /*
    processFirst {
        timeout { Duration.ofMilliseconds(150) }
        handle {
            on { isSomething() }
            exec { someFun() }
        }
        handle {
            on { isOk() }
            exec { someFun() }
        }
    }
    */

    // Perform all handlers with match = true in parallel
    // Under discussion
    /*
    processParallel {
        timeout { Duration.ofMilliseconds(150) }
        repeatWhile { someCondition() } // Repeat starting all handlers while `someCondition()` is true
        handle {
            on { isSomething() }
            exec { someFun() }
        }
        handle {
            on { isOk() }
            exec { someFun() }
        }
    }
    */

    // Some custom processor
    processFiles {
        readBy = ReadBy.STREAM

        setPackage { // this: MyContext ->
            PackageHandlerData(
                filePath = this.packageName, // "my/path/source_file.zip"
                fileMask = "*.xml"
            )
        }
        
        handleFile { /* this: MyContext*/ file: FileHandlerData ->
            this.inputStream = file.inputStream
            this.fileName = file.fileName
        }
        exec { someFun() }
        handleFile { /* this: MyContext */ file: FileHandlerData ->
            this.inputStream = file.inputStream
            this.fileName = file.fileName
        }

    }
    exec { // this: MyContext ->
        timeStop = Instant.now()
    }
}
```

Having that configured, you just process all data like following;
```kotlin
val context = MyContext(rawQuery = aQueryFromUser)
conv.exec(context)
val result = context.result
```

## Using in your projects

The libraries are published to kotlinx bintray repository, linked to JCenter and pushed to Maven Central.

### Maven

Add dependencies (you can also add other modules that you need):

```xml
<dependency>
	<groupId>codes.spectrum</groupId>
	<artifactId>konveyor</artifactId>
	<version>0.1.11</version>
	<type>pom</type>
</dependency>
```

### Gradle

Add dependencies (you can also add other modules that you need):

```groovy
dependencies {
    compile 'codes.spectrum:konveyor:0.1.11'
}
```

Make sure that you have jcenter() in the list of repositories:

```groovy
repository {
    jcenter()
}
```

### Gradle Kotlin DSL
Add dependencies (you can also add other modules that you need):

```kotlin
dependencies {
    implementation("codes.spectrum:konveyor:0.1.11")
}
```

## License

Konveyor is provided under Apache License version 2.0. See LICENSE.txt for more details.
