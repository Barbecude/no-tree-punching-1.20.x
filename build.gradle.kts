plugins {
    java
    id("fabric-loom") version "1.7.4"
}

val modId: String by extra
val modName: String by extra
val modAuthor: String by extra
val modGroup: String by extra
val modIssueUrl: String by extra
val modHomeUrl: String by extra
val modDescription: String by extra
val modJavaVersion: String by extra
val modVersion: String = System.getenv("VERSION") ?: "1.21.1-1.0.0"

val minecraftVersion: String by extra
val minecraftVersionRange: String by extra
val fabricVersion: String by extra
val fabricVersionRange: String by extra
val fabricLoaderVersion: String by extra
val fabricLoaderVersionRange: String by extra

version = modVersion
group = modGroup

base {
    archivesName.set("${modId}-fabric-${minecraftVersion}")
}

repositories {
    exclusiveContent {
        forRepository { maven("https://maven.fabricmc.net/") }
        filter { includeGroup("net.fabricmc") }
    }
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")

    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.jetbrains:annotations:24.0.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(modJavaVersion.toInt())
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(modJavaVersion.toInt()))
    }
    withSourcesJar()
}

loom {
    mixin {
        defaultRefmapName.set("${modId}.refmap.json")
    }

    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("run")
        }
        named("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("run")
        }
    }
}

tasks {
    processResources {
        filesMatching(listOf("pack.mcmeta", "fabric.mod.json")) {
            expand(mapOf(
                "modName" to modName,
                "modAuthor" to modAuthor,
                "modId" to modId,
                "modGroup" to modGroup,
                "modIssueUrl" to modIssueUrl,
                "modHomeUrl" to modHomeUrl,
                "modDescription" to modDescription,
                "modJavaVersion" to modJavaVersion,
                "modVersion" to modVersion,
                "minecraftVersion" to minecraftVersion,
                "minecraftVersionRange" to minecraftVersionRange,
                "fabricVersion" to fabricVersion,
                "fabricVersionRange" to fabricVersionRange,
                "fabricLoaderVersion" to fabricLoaderVersion,
                "fabricLoaderVersionRange" to fabricLoaderVersionRange,
            ))
        }
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${modName}" }
        }
    }
}