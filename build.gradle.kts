import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.intellij") version "1.16.1"
}

group = "cc.oofo"
version = "1.0.1"

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
            一个简单易用的翻译插件，支持多种翻译引擎：
            <ul>
                <li>百度翻译</li>
                <li>有道翻译</li>
                <li>谷歌翻译</li>
            </ul>
            
            主要功能：
            <ul>
                <li>快速翻译选中的文本</li>
                <li>支持自定义词典</li>
                <li>支持多种文本转换功能（大小写转换、驼峰转换等）</li>
                <li>支持中文变量命名建议</li>
            </ul>
            
            使用方法：
            <ol>
                <li>在设置中配置翻译引擎的 API 密钥</li>
                <li>选中需要翻译的文本</li>
                <li>使用快捷键或右键菜单进行翻译</li>
            </ol>
            
            支持的 IDE：
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
                <li>1.0.1
                    <ul>
                        <li>新增变量命名建议功能（Alt + Shift + V）</li>
                        <li>扩展 IDE 兼容性支持（233+）</li>
                        <li>优化用户界面体验</li>
                    </ul>
                </li>
                <li>1.0.0
                    <ul>
                        <li>支持百度翻译、有道翻译和谷歌翻译</li>
                        <li>支持自定义词典功能</li>
                        <li>支持文本转换功能</li>
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