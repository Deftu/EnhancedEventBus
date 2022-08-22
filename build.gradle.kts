plugins {
    kotlin("jvm") version("1.6.21")
    java
    `maven-publish`
}

group =
    extra["project.group"]?.toString() ?: throw IllegalArgumentException("The project group has not been set.")
version =
    extra["project.version"]?.toString() ?: throw IllegalArgumentException("The project version has not been set.")

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    implementation(kotlin("stdlib"))
    
    testImplementation("junit:junit:4.12")
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
    testImplementation("com.github.deamsy:eventbus:1.1")
    testImplementation("com.google.guava:guava:29.0-jre")
    testImplementation("org.testng:testng:7.1.0")
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId =
                extra["project.name"]?.toString() ?: throw IllegalArgumentException("The project name has not been set.")
            groupId = project.group.toString()
            version = project.version.toString()

            from(components["java"])
        }
    }

    repositories {
        if (project.hasProperty("unifycraft.publishing.username") && project.hasProperty("unifycraft.publishing.password")) {
            fun MavenArtifactRepository.applyCredentials() {
                credentials {
                    username = property("unifycraft.publishing.username")?.toString()
                    password = property("unifycraft.publishing.password")?.toString()
                }
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }

            maven {
                name = "UnifyCraftRelease"
                url = uri("https://maven.unifycraft.xyz/releases")
                applyCredentials()
            }

            maven {
                name = "UnifyCraftSnapshots"
                url = uri("https://maven.unifycraft.xyz/snapshots")
                applyCredentials()
            }
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}
