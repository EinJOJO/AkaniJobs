plugins {
    id("java")

}

group = "it.einjojo.akani"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.akani.dev/releases")
    maven("https://repo.oraxen.com/releases")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://repo.xenondevs.xyz/releases")
}

dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.akanicore)
    compileOnly(libs.hikaricp)
    implementation(libs.acf)
    implementation(libs.invui)

    testImplementation(libs.hikaricp)
    testImplementation(libs.mariadb)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Test> {
        useJUnitPlatform()
    }

    processResources {

    }

}
