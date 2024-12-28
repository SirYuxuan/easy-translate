import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.intellij") version "1.16.1"
}

group = "cc.oofo"
version = "1.0.2"

repositories {
    mavenCentral()
}

// 添加 Java 工具链配置
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// Configure Gradle IntelliJ Plugin
intellij {
    version.set("2023.2.5")
    type.set("IC") // IntelliJ IDEA Community Edition
    plugins.set(listOf(
        "com.intellij.java",
        "org.jetbrains.kotlin"
    ))
    updateSinceUntilBuild.set(false)
    sameSinceUntilBuild.set(true)
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    test {
        useJUnitPlatform()
    }

    buildSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        sinceBuild.set("233")
        untilBuild.set("")
        pluginDescription.set("""
            A powerful translation plugin for IntelliJ-based IDEs with multiple translation engines:
            <ul>
                <li>Baidu Translate API</li>
                <li>Youdao Translate API</li>
                <li>Google Translate API</li>
            </ul>
            
            Key Features:
            <ul>
                <li>Quick Translation (Alt + T)</li>
                <li>Variable Name Suggestions (Alt + Shift + V)</li>
                <li>Text Case Conversion (Alt + Shift + T)</li>
                <li>Custom Dictionary Support</li>
            </ul>
            
            Usage:
            <ol>
                <li>Configure API credentials in Settings</li>
                <li>Select text in editor</li>
                <li>Use shortcut or context menu to translate</li>
            </ol>
            
            Supported IDEs:
            <ul>
                <li>IntelliJ IDEA (Community & Ultimate)</li>
                <li>PyCharm (Community & Professional)</li>
                <li>WebStorm</li>
                <li>PhpStorm</li>
                <li>GoLand</li>
                <li>CLion</li>
                <li>DataGrip</li>
                <li>RubyMine</li>
                <li>Rider</li>
            </ul>
        """)
        changeNotes.set("""
            <ul>
                <li>1.0.2
                    <ul>
                        <li>Improved plugin description</li>
                        <li>Enhanced documentation</li>
                        <li>Added support for future IDE versions</li>
                    </ul>
                </li>
                <li>1.0.1
                    <ul>
                        <li>Added variable name suggestion feature (Alt + Shift + V)</li>
                        <li>Extended IDE compatibility (233+)</li>
                        <li>Improved UI/UX experience</li>
                        <li>Enhanced English documentation</li>
                    </ul>
                </li>
                <li>1.0.0
                    <ul>
                        <li>Initial release</li>
                        <li>Support for Baidu, Youdao, and Google translation</li>
                        <li>Custom dictionary feature</li>
                        <li>Text transformation tools</li>
                    </ul>
                </li>
            </ul>
        """)
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
        channels.set(listOf("stable"))
    }
}