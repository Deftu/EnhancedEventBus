plugins {
    kotlin("jvm") version("1.6.21")
    java
    `maven-publish`
}

group = extra["project.group"]?.toString() ?: throw IllegalArgumentException("The project group has not been set.")
version = extra["project.version"]?.toString() ?: throw IllegalArgumentException("The project version has not been set.")

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    implementation(kotlin("stdlib"))
    
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("com.github.deamsy:eventbus:1.1")
    testImplementation("com.google.guava:guava:31.1-jre")
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = extra["project.name"]?.toString()?.toLowerCase() ?: throw IllegalArgumentException("The project name has not been set.")
            groupId = project.group.toString()
            version = project.version.toString()

            from(components["java"])
        }
    }

    repositories {
        if (project.hasProperty("deftu.publishing.username") && project.hasProperty("deftu.publishing.password")) {
            fun MavenArtifactRepository.applyCredentials() {
                authentication.create<BasicAuthentication>("basic")
                credentials {
                    username = property("deftu.publishing.username")?.toString()
                    password = property("deftu.publishing.password")?.toString()
                }
            }

            maven {
                name = "DeftuReleases"
                url = uri("https://maven.deftu.dev/releases")
                applyCredentials()
            }

            maven {
                name = "DeftuSnapshots"
                url = uri("https://maven.deftu.dev/snapshots")
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
