plugins {
    id("java")
    `maven-publish`
}

group = "top.birthcat"
version = "1.0.0"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
}

val commonJvmArgs = listOf(
    "--enable-preview",
    "--add-exports","java.desktop/sun.awt.windows=ALL-UNNAMED",
)
val runtimeJvmArgs = listOf(
    "--enable-native-access=ALL-UNNAMED"
)

tasks.withType<JavaCompile> {
    options.compilerArgs = commonJvmArgs
}

tasks.withType<JavaExec> {
    jvmArgs(commonJvmArgs + runtimeJvmArgs)
}

publishing.publications.register<MavenPublication>("local") {
    from(components["java"])
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}