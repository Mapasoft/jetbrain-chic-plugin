import org.jetbrains.grammarkit.tasks.GenerateLexerTask

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.17.3"
    id("org.jetbrains.grammarkit") version "2022.3.2.2"
}

group = "com.chic"
version = "1.0.9"

kotlin {
    jvmToolchain(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

intellij {
    type.set("CL")
    version.set("2024.1.4")
    downloadSources.set(false)
    plugins.set(listOf("com.intellij.clion", "com.intellij.nativeDebug", "com.intellij.cidr.base"))
}

// ---------------------------------------------------------------------------
// JFlex lexer generation
// ---------------------------------------------------------------------------

val generateChicLexer = tasks.register<GenerateLexerTask>("generateChicLexer") {
    sourceFile.set(file("src/main/flex/ChicLexer.flex"))
    targetOutputDir.set(layout.projectDirectory.dir("src/main/gen/com/chic/lexer"))
    // targetClass is determined by the %class directive inside ChicLexer.flex (_ChicLexer)
    purgeOldFiles.set(true)
}

// Make compilation depend on lexer generation
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    dependsOn(generateChicLexer)
    kotlinOptions.jvmTarget = "17"
}
tasks.named("compileJava") {
    dependsOn(generateChicLexer)
}

sourceSets {
    main {
        java {
            srcDirs("src/main/gen")
        }
    }
}

// ---------------------------------------------------------------------------
// Plugin metadata
// ---------------------------------------------------------------------------

tasks {
    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("261.*")
    }

    // Skip searchable options for faster local builds
    buildSearchableOptions {
        enabled = false
    }
}
