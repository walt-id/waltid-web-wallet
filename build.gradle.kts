import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.22"
    id("io.ktor.plugin") version "2.3.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.22"
}

group = "id.walt"
version = "0.0.1"
application {
    mainClass.set("id.walt.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}


repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://maven.walt.id/repository/waltid/")
    maven("https://repo.danubetech.com/repository/maven-public/")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}



dependencies {
    // nftkit
    implementation("id.walt:waltid-nftkit:1.2307292233.0"){
        exclude("com.sksamuel.hoplite", "hoplite-core")
        exclude("com.sksamuel.hoplite", "hoplite-yaml")
        exclude("com.sksamuel.hoplite", "hoplite-hikaricp")
    }

    /* -- KTOR -- */

    // Ktor server
    implementation("io.ktor:ktor-server-core-jvm:2.3.2")
    implementation("io.ktor:ktor-server-auth-jvm:2.3.2")
    implementation("io.ktor:ktor-server-sessions-jvm:2.3.2")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.3.2")
    implementation("io.ktor:ktor-server-auto-head-response-jvm:2.3.2")
    implementation("io.ktor:ktor-server-double-receive-jvm:2.3.2")
    implementation("io.ktor:ktor-server-host-common-jvm:2.3.2")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.3.2")
    implementation("io.ktor:ktor-server-compression-jvm:2.3.2")
    implementation("io.ktor:ktor-server-cors-jvm:2.3.2")
    implementation("io.ktor:ktor-server-forwarded-header-jvm:2.3.2")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.3.2")
    implementation("io.ktor:ktor-server-call-id-jvm:2.3.2")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.2")
    implementation("io.ktor:ktor-server-cio-jvm:2.3.2")

    // Ktor server external libs
    implementation("io.github.smiley4:ktor-swagger-ui:2.2.0")

    // Ktor client
    implementation("io.ktor:ktor-client-core-jvm:2.3.2")
    implementation("io.ktor:ktor-client-serialization-jvm:2.3.2")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.2")
    implementation("io.ktor:ktor-client-json-jvm:2.3.2")
    implementation("io.ktor:ktor-client-cio-jvm:2.3.2")
    implementation("io.ktor:ktor-client-logging-jvm:2.3.2")
    implementation("io.ktor:ktor-client-cio-jvm:2.3.2")

    /* -- Kotlin -- */

    // Kotlinx.serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.2")

    // Date
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")

    /* -- Security -- */
    // Bouncy Castle
    implementation("org.bouncycastle:bcprov-jdk18on:1.75")

    // Argon2
    implementation("de.mkammerer:argon2-jvm:2.11")

    /* -- Misc --*/

    // Cache
    implementation("io.github.reactivecircus.cache4k:cache4k:0.11.0")

    // DB
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")
    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
    implementation("org.postgresql:postgresql:42.5.4")
    // migration
    implementation("org.flywaydb:flyway-core:9.16.0")

    // Web push
    implementation("nl.martijndwars:web-push:5.1.1")

    // Config
    implementation("com.sksamuel.hoplite:hoplite-core:2.7.4")
    implementation("com.sksamuel.hoplite:hoplite-hocon:2.7.4")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.7.4")
    implementation("com.sksamuel.hoplite:hoplite-hikaricp:2.7.4")
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Logging
    implementation("io.github.oshai:kotlin-logging-jvm:4.0.2")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("org.slf4j:jul-to-slf4j:2.0.7")

    // Test
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.22")

    testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
    testImplementation("io.kotest:kotest-assertions-core:5.5.5")
    testImplementation("io.kotest.extensions:kotest-assertions-ktor:2.0.0")
    testImplementation("io.ktor:ktor-server-tests-jvm:2.3.2")
}
