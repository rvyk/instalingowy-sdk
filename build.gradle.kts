plugins {
    id("java")
}

group = "pl.rvyk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:3.14.6")
    implementation("org.jsoup:jsoup:1.17.1")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.1")
}

tasks.test {
    useJUnitPlatform()
}