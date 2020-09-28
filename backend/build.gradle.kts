buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:${Dependencies.shadowVersion}")
    }
}

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("application")
    id("com.github.johnrengelman.shadow") version Dependencies.shadowVersion
}

val engineClass = "io.ktor.server.netty.EngineMain"
application {
    mainClassName = engineClass
}

repositories {
    maven(url = "https://dl.bintray.com/kotlin/ktor")
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
    maven(url = "https://kotlin.bintray.com/kotlin-js-wrappers")
}

dependencies {
    implementation(project(":core-impl"))
    implementation(project(":runner"))
    implementation(project(":results"))

    implementation(Dependencies.Ktor.serverNetty)
    implementation(Dependencies.logbackClassic)
    implementation(Dependencies.Ktor.serverCore)
    implementation(Dependencies.Ktor.htmlBuilder)
    implementation(Dependencies.Ktor.clientCore)
    implementation(Dependencies.Ktor.clientCoreJvm)
    implementation(Dependencies.Ktor.clientApache)

    implementation(Dependencies.Ktor.gson)
    implementation(Dependencies.Ktor.clientJson)
    implementation(Dependencies.Ktor.clientGson)

    implementation(Dependencies.Dagger.dagger)
    kapt(Dependencies.Dagger.daggerCompiler)
}

tasks.shadowJar {
    manifest {
        attributes(mapOf("Main-Class" to engineClass))
    }
}