plugins {
    id("java")
    id("maven-publish")
}

group = "pl.rvyk"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:3.14.6")
    implementation("org.jsoup:jsoup:1.17.1")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.jetbrains:annotations:24.0.0")

    testImplementation("org.junit.platform:junit-platform-launcher:1.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")

    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.1")
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/rvyk/instalingowy-sdk")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}