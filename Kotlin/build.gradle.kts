plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    val scarlet_version = "0.1.11";
    // =============== WebSocket ===============
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.tinder.scarlet:scarlet:$scarlet_version")
    implementation("com.tinder.scarlet:message-adapter-gson:$scarlet_version")
    implementation("com.tinder.scarlet:stream-adapter-rxjava2:$scarlet_version")
    implementation("com.tinder.scarlet:websocket-okhttp:$scarlet_version")

    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    implementation("com.tinder.scarlet:message-adapter-moshi:0.1.12")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}