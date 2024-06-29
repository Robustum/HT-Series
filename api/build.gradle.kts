base.archivesName = "HT-API-${rootProject.version}"

loom {
    accessWidenerPath = file("src/main/resources/ht_api.accesswidener")
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
}

dependencies {
    "io.github.cottonmc:LibGui:3.4.0+1.16.5".run {
        modImplementation(this)
        include(this) { isTransitive = false }
    }
}
