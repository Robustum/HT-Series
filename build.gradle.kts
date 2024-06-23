import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm").version("2.0.0")
    id("fabric-loom").version("1.6-SNAPSHOT").apply(false)
    id("org.jlleitschuh.gradle.ktlint").version("12.1.0")
}

base.archivesName.set("HT-Series")
group = "io.github.hiiragi283"
version = "3.0.0+1.16.5"

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "fabric-loom")

    java.toolchain.languageVersion = JavaLanguageVersion.of(8)
    java.withSourcesJar()

    repositories {
        mavenCentral()
        maven(url = "https://cursemaven.com") {
            content { includeGroup("curse.maven") }
        }
        maven(url = "https://api.modrinth.com/maven") {
            content { includeGroup("maven.modrinth") }
        }
        // AE2
        maven(url = "https://modmaven.dev/") {
            content { includeGroup("appeng") }
        }
        maven(url = "https://mod-buildcraft.com/maven") {
            content { includeGroup("alexiil.mc.lib") }
        }
        maven(url = "https://raw.githubusercontent.com/Technici4n/Technici4n-maven/master/") {
            content {
                includeGroup("dev.technici4n")
                includeGroup("net.fabricmc.fabric-api")
            }
        }
        maven(url = "https://dvs1.progwml6.com/files/maven") // JEI
        maven(url = "https://maven.alexiil.uk/") // LBA
        maven(url = "https://maven.architectury.dev")
        maven(url = "https://maven.shedaniel.me") // REI
        maven(url = "https://maven.terraformersmc.com/releases")
        maven(url = "https://server.bbkr.space/artifactory/libs-release") // LibGui
    }

    dependencies {
        add("minecraft", "com.mojang:minecraft:1.16.5")
        add("mappings", "net.fabricmc:yarn:1.16.5+build.10:v2")

        add("modImplementation", "net.fabricmc:fabric-loader:0.15.+")
        add("modImplementation", "net.fabricmc.fabric-api:fabric-api:0.42.0+1.16")
        add("modImplementation", "net.fabricmc:fabric-language-kotlin:1.10.19+kotlin.1.9.23")

        add("modImplementation", "me.shedaniel:RoughlyEnoughItems:5.12.385") {
            exclude(module = "fabric-api")
            exclude(module = "fabric-loader")
        }
        add("modRuntimeOnly", "maven.modrinth:modmenu:1.16.23") {
            exclude(module = "fabric-api")
            exclude(module = "fabric-loader")
        }
    }

    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlin {
        jvmToolchain(8)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
            freeCompilerArgs.add("-Xjvm-default=all")
        }
    }

    ktlint {
        reporters {
            reporter(ReporterType.HTML)
            reporter(ReporterType.SARIF)
        }
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }

    tasks {
        processResources {
            inputs.property("version", rootProject.version)
            filesMatching("fabric.mod.json") {
                expand("version" to rootProject.version)
            }
        }
    }
}
