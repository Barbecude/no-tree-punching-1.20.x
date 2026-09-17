pluginManagement {
    repositories {
        fun exclusiveMaven(url: String, filter: Action<InclusiveRepositoryContentDescriptor>) =
            exclusiveContent {
                forRepository { maven(url) }
                filter(filter)
            }

        exclusiveMaven("https://maven.fabricmc.net/") {
            includeGroup("net.fabricmc")
            includeGroup("fabric-loom")
        }

        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "notreepunching"