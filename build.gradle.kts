plugins {
    kotlin("jvm") version "1.3.21" apply false
}

subprojects {
    repositories {
        jcenter()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
