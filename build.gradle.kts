plugins {
    kotlin("jvm") version("1.6.21")
    java
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
