base.archivesName = "HT-Engineering-${rootProject.version}"

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
        register("ht_materials") {
            sourceSet(project(":material").sourceSets.main.get())
        }
        create("ht_engineering") {
            sourceSet(project(":engineering").sourceSets.main.get())
        }
    }*/
}

dependencies {
    modImplementation("TechReborn:TechReborn-1.16:+") {
        exclude(module = "fabric-api")
        exclude(module = "fabric-loader")
    }

    // modCompileOnly("curse.maven:industrial-revolution-391708:3364481")
    modImplementation("io.github.cottonmc:LibGui:3.4.0+1.16.5") {
        exclude(module = "fabric-api")
        exclude(module = "fabric-loader")
    }

    implementation(project(path = ":api", configuration = "namedElements")) {
        exclude(module = "fabric-api")
        exclude(module = "fabric-loader")
        exclude(module = "yarn")
    }
    implementation(project(path = ":material", configuration = "namedElements")) {
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
