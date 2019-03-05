plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    testCompile("junit:junit:4.12")
    testCompile(kotlin("test"))
}
