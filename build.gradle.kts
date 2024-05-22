plugins {
    id("java")
    alias(libs.plugins.runpaper)

}

group = "it.einjojo.akani"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
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
    compileOnly(libs.caffeine)
    compileOnly(libs.mariadb)
    implementation(libs.acf)
    implementation(libs.invui)

    testImplementation(libs.guava)
    testImplementation(libs.hikaricp)
    testImplementation(libs.mariadb)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))

}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Test> {
        useJUnitPlatform()
    }

    runServer {
        minecraftVersion("1.20.4")

    }

    processResources {
        filesMatching("**/plugin.yml") {
            expand(
                mapOf(
                    "version" to project.version,
                    "group" to project.group,
                    "caffeine" to libs.caffeine.get(),
                    "hikari" to libs.hikaricp.get(),
                    "maria" to libs.mariadb.get()
                )
            )
        }
    }

}
