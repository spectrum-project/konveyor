import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import java.util.*
import java.time.*
import java.time.format.DateTimeFormatter

buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
}

plugins {
    kotlin("multiplatform") version "1.3.20"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.4"
//    id("com.jfrog.artifactory") version "4.9.0"
    id("maven-publish")
    id("net.nemerosa.versioning") version "2.8.2"
    jacoco
    id("org.jetbrains.dokka") version "0.9.17"
    maven
}

repositories {
    mavenCentral()
    jcenter()
}

group = "codes.spectrum"
version = "0.1.0-SNAPSHOT"

jacoco {
    toolVersion = "0.8.3"
}

tasks {

    val codeCoverageReport by creating(JacocoReport::class) {
        group = "verification"
        dependsOn()
        executionData(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))

        classDirectories.setFrom(
            files("${buildDir}/classes/kotlin/js/main"),
            files("${buildDir}/classes/kotlin/jvm/main"),
            files("${buildDir}/classes/kotlin/linux/main")
        )
        reports {
            xml.isEnabled = true
            xml.destination = File("$buildDir/reports/jacoco/report.xml")
            html.isEnabled = false
            html.destination = File("$buildDir/reports/jacoco/report.html")
            csv.isEnabled = false
        }

    }
    check {
        dependsOn(codeCoverageReport)
    }


}

kotlin {
    jvm {
        val main by compilations.getting {

            compileKotlinTask // get the Kotlin task 'compileKotlinJvm'
            output // get the main compilation output
        }

        val test by compilations.getting {
            kotlinOptions {
                // Setup the Kotlin compiler options for the 'main' compilation:
                jvmTarget = "1.8"
            }

            compileKotlinTask // get the Kotlin task 'compileKotlinJvm'
            output // get the main compilation output
        }

        compilations["test"].runtimeDependencyFiles // get the test runtime classpath

        tasks {
            dokka {
                val dokkaOut = "$buildDir/docs/${main.platformType.name}"
                outputFormat = "html"
                outputDirectory = dokkaOut

                // This will force platform tags for all non-common sources e.g. "JVM"
                impliedPlatforms = mutableListOf("Common", "JVM")

                doFirst {
                    println("Cleaning doc directory $dokkaOut...")
//                    project.delete(fileTree(dokkaOut))
                }

                // dokka fails to retrieve sources from MPP-tasks so they must be set empty to avoid exception
                kotlinTasks(closureOf<Any?> { emptyList<Any?>() })

                sourceDirs = main.defaultSourceSet.kotlin

            }

            val jvmJar by getting(Jar::class) {
                manifest {
                    val buildTimeAndDate = OffsetDateTime.now()
//                    var generateManifest by extra(false)
                    val buildDate by extra { DateTimeFormatter.ISO_LOCAL_DATE.format(buildTimeAndDate) }
                    val buildTime by extra { DateTimeFormatter.ofPattern("HH:mm:ss.SSSZ").format(buildTimeAndDate) }
                    val buildRevision by extra { versioning.info.commit }
                    val builtByValue by extra { project.findProperty("builtBy") ?: project.property("defaultBuiltBy") }

                    attributes(
                        mutableMapOf(
                            "Created-By" to "${System.getProperty("java.version")} (${System.getProperty("java.vendor")} ${System.getProperty("java.vm.version")})",
                            "Built-By" to builtByValue,
                            "Build-Date" to buildDate,
                            "Build-Time" to buildTime,
                            "Build-Revision" to buildRevision,
                            "Specification-Title" to project.name,
                            "Specification-Version" to project.version as String,
                            "Specification-Vendor" to "Spectrum-Project",
                            "Implementation-Title" to project.name,
                            "Implementation-Version" to project.version,
                            "Implementation-Vendor" to "Spectrum-Project"
                        )
                    )
                }
            }

            val dokkaJar by creating(Jar::class) {
                group = JavaBasePlugin.DOCUMENTATION_GROUP
                description = "Assembles Kotlin docs with Dokka"
                archiveClassifier.set("javadoc")
                dependsOn(dokka) // not needed; dependency automatically inferred by from(tasks.dokka)
                from(dokka.get().outputDirectory)
            }

            // Create sources Jar from main kotlin sources
            val sourcesJar by creating(Jar::class) {
                group = JavaBasePlugin.DOCUMENTATION_GROUP
                description = "Assembles sources JAR"
                archiveClassifier.set("sources")
                from(
                    listOf(
                        "src/commonMain/kotlin",
                        "src/jvmMain/kotlin"
                    ).map { projectDir.resolve(it) }
                )
            }

            publishing {
                publications {
                    register("mavenJava", MavenPublication::class) {
                        pom {

                            name.set("Konveyor")
                            description.set("Conveyor belt software design pattern in a kotlin DSL-like style")
                            url.set("https://github.com/spectrum-project/konveyor")
                            licenses {
                                license {
                                    name.set("The Apache License, Version 2.0")
                                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                                }
                            }
                            developers {
                                developer {
                                    id.set("svok")
                                    name.set("Sergey Okatov")
                                    email.set("sokatov@gmail.com")
                                }
                            }

                            withXml {
                                asNode().appendNode("dependencies").let { depNode ->
                                    configurations.forEach {
                                        println("configuration-----${it.name}")
                                    }
                                    val jvmImplementation by configurations
                                    jvmImplementation.allDependencies.forEach {
                                        if (it.name != "unspecified") {
                                            depNode.appendNode("dependency").apply {
                                                appendNode("groupId", it.group)
                                                appendNode("artifactId", it.name)
                                                appendNode("version", it.version)
                                            }
                                        }
                                    }
                                }
                            }

//                    scm {
//                        connection.set("scm:git:git://example.com/my-library.git")
//                        developerConnection.set("scm:git:ssh://example.com/my-library.git")
//                        url.set("http://example.com/my-library/")
//                    }
                        }
                        val jvmJar by getting(Jar::class)
                        from(getComponents().get("kotlin"))
                        artifact(sourcesJar)
                        artifact(dokkaJar)
                        artifact(jvmJar.archiveFile.get())
                    }
                }
                repositories {
                    maven {
                        url = uri("$buildDir/repository")
                    }
                }
            }

            bintray {
                user = System.getenv("bintrayUser")?.toString() ?: ""
                key = System.getenv("bintrayKey")?.toString() ?: ""
                override = true
                setPublications("mavenJava")

                pkg(closureOf<BintrayExtension.PackageConfig> {
                    repo = "konveyor"
                    name = "konveyor"
                    desc = "Conveyor belt software design pattern in a kotlin DSL-like style"
                    userOrg = "spectrum-project"
                    websiteUrl = "https://github.com/spectrum-project/konveyor"
                    issueTrackerUrl = "https://github.com/spectrum-project/konveyor/issues"
                    vcsUrl = "https://github.com/spectrum-project/konveyor.git"
                    githubRepo = "spectrum-project/konveyor"
                    githubReleaseNotesFile = "CHANGELOG.md"
                    setLicenses("Apache-2.0")
                    setLabels(
                        "conveyor",
                        "conveyor-belt",
                        "processor",
                        "workflow",
                        "kotlin",
                        "kotlin dsl",
                        "handler"
                    )
                    publish = true
                    setPublications("mavenJava")
                    version(closureOf<BintrayExtension.VersionConfig> {
                        this.name = project.version.toString()
                        released = Date().toString()
                    })
                })
            }

            val bintrayUpload by existing(BintrayUploadTask::class) {
                dependsOn("build")
                dependsOn("generatePomFileForMavenJavaPublication")
                dependsOn("generatePomFileForJvmPublication")
                dependsOn(sourcesJar)
                dependsOn(dokkaJar)
            }

//    named<Task>("afterReleaseBuild") {
//        dependsOn(bintrayUpload)
//    }
            publish {
                dependsOn(bintrayUpload)
                dependsOn(bintrayPublish)
            }
        }
    }
    //js()
    // For ARM, should be changed to iosArm32 or iosArm64
    // For Linux, should be changed to e.g. linuxX64
    // For MacOS, should be changed to e.g. macosX64
    // For Windows, should be changed to e.g. mingwX64
    // linuxX64("linux")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.1.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.1.1")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
            }
        }
//        val jsMain by getting {
//            dependencies {
//                implementation(kotlin("stdlib-js"))
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
//            }
//        }
//        val jsTest by getting {
//            dependencies {
//                implementation(kotlin("test-js"))
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
//            }
//        }
        // val linuxMain by getting {
        // }
        // val linuxTest by getting {
        // }

        all {
            languageSettings.apply {
                progressiveMode = true
                languageVersion = "1.3"
                apiVersion = "1.3"
                useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
//                enableLanguageFeature("InlineClasses") // language feature name
            }
        }
    }
}

