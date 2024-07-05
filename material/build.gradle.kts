base.archivesName = "HT-Materials-${rootProject.version}"

loom {
    // accessWidenerPath = file("src/main/resources/ht_materials.accesswidener")
    runs {
        getByName("client") {
            programArg("--username=Developer")
            vmArg("-Dmixin.debug.export=true")
        }
        getByName("server") {
            runDir = "server"
            vmArg("-Dmixin.debug.export=true")
        }
    }
    /*mods {
        register("ht_api") {
            sourceSet(project(":api").sourceSets.main.get())
        }
        create("ht_materials") {
            sourceSet(project(":material").sourceSets.main.get())
        }
    }*/
}

dependencies {
    modImplementation("TechReborn:TechReborn-1.16:+") {
        exclude(module = "fabric-api")
        exclude(module = "fabric-loader")
    }

    modCompileOnly("curse.maven:industrial-revolution-391708:3364481")

    implementation(project(path = ":api", configuration = "namedElements")) {
        exclude(module = "fabric-api")
        exclude(module = "fabric-loader")
        exclude(module = "yarn")
    }
}

tasks {
    jar {
        listOf("LICENSE", "CHANGELOG.md", "README.md").forEach { path ->
            from(path) {
                rename { "${project.base.archivesName.get()}_$it" }
            }
        }
    }
}
