plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.4.20"
}

group = "me.vislavy"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT")
    api("com.oracle.database.jdbc:ojdbc11-production:21.4.0.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    api("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:1.5.0")
    api("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:1.5.0")
    api("com.charleskorn.kaml:kaml:0.40.0")
    api("org.apache.logging.log4j:log4j-api:2.17.1")
    api("org.apache.logging.log4j:log4j-core:2.17.1")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "${JavaVersion.VERSION_11}"
}