plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.jgoodies:forms:1.2.1")

    testCompile("junit:junit:4.12")
    testCompile(kotlin("test"))
}
