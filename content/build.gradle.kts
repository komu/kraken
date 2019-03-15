plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    api(project(":core"))

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.0")
}
