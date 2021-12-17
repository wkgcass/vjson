import vpreprocessor.FilePreprocessor
import java.io.ByteArrayOutputStream

buildscript {
  repositories {
    mavenLocal()
    mavenCentral()
  }
  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    classpath("io.vproxy:vjson:1.3.3")
  }
}

plugins {
  java
  jacoco
  kotlin("jvm") version ("1.5.31")
  id("org.jetbrains.dokka") version "1.5.30"
  `maven-publish`
  signing
}

group = "io.vproxy"
version = loadVersion()

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8

  withSourcesJar()
}

jacoco {
  toolVersion = "0.8.7"
}

tasks {
  compileJava {
    options.encoding = "UTF-8"
  }
  compileTestJava {
    options.encoding = "UTF-8"
  }
  compileKotlin {
    kotlinOptions {
      jvmTarget = "1.8"
      freeCompilerArgs = listOf("-Xjvm-default=enable")
    }
  }
  compileTestKotlin {
    kotlinOptions {
      jvmTarget = "1.8"
    }
  }
}

repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

  testImplementation(group = "junit", name = "junit", version = "4.12")
  testImplementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.9.9.3")
  testImplementation(group = "com.google.code.gson", name = "gson", version = "2.8.5")
  testImplementation(group = "com.alibaba", name = "fastjson", version = "1.2.59")
  testImplementation(group = "org.openjdk.jmh", name = "jmh-generator-annprocess", version = "1.21")

  // only used when running coverage (normally they are commented)
  // see https://github.com/jacoco/jacoco/issues/921#issuecomment-800514452
  // also see the text preprocessor for building this project
  compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.18")
}

val buildJavaDoc = tasks.create("buildJavaDoc", org.jetbrains.dokka.gradle.DokkaTask::class) {
  outputDirectory.set(tasks.named<Javadoc>("javadoc").get().destinationDir)
  dokkaSourceSets {
    named("main") {
      sourceRoot(file("src/main/kotlin"))
    }
  }
}

val javadocJar = tasks.create("javadocJar", Jar::class) {
  classifier = "javadoc"
  from("$buildDir/docs/javadoc")

  dependsOn(buildJavaDoc)
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      artifactId = "vjson"

      artifact(javadocJar)

      from(components["java"])

      pom {
        name.set("vjson")
        description.set("json library")
        url.set("https://github.com/wkgcass/vjson")
        licenses {
          license {
            name.set("MIT License")
            url.set("https://github.com/wkgcass/vjson/blob/master/LICENSE")
          }
        }
        developers {
          developer {
            id.set("wkgcass")
            name.set("wkgcass")
            email.set("wkgcass@hotmail.com")
          }
        }
        scm {
          connection.set("scm:git:git://github.com/wkgcass/vjson")
          developerConnection.set("scm:git:ssh://github.com/wkgcass/vjson.git")
          url.set("https://github.com/wkgcass/vjson/")
        }
      }
    }
  }
  repositories {
    maven {
      credentials {
        username = "wkgcass"
        password = System.getProperty("MavenPublishPassword")
      }
      val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
      val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
      url = uri(
        if (loadVersion().contains("-DEV")) {
          snapshotsRepoUrl
        } else {
          releasesRepoUrl
        }
      )
    }
  }
}

signing {
  sign(publishing.publications["maven"])
}

tasks.test {
  include("**/Suite.class")
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
}

val checkGit = tasks.create("checkGit", Exec::class) {
  val output = ByteArrayOutputStream()
  commandLine = listOf("./scripts/check-git.sh", loadVersion())
  standardOutput = output
  errorOutput = output
  isIgnoreExitValue = true
  doLast {
    println(output)
    val exitValue = execResult!!.exitValue
    if (exitValue != 0) {
      throw Exception("bootstrap failed: exit code $exitValue")
    }
  }
}

val bootstrap = tasks.create("bootstrap", Exec::class) {
  val output = ByteArrayOutputStream()
  commandLine = listOf("./scripts/bootstrap.sh", loadVersion())
  standardOutput = output
  errorOutput = output
  isIgnoreExitValue = true
  doLast {
    println(output)
    val exitValue = execResult!!.exitValue
    if (exitValue != 0) {
      throw Exception("bootstrap failed: exit code $exitValue")
    }
  }
}

val coverage = tasks.create("coverage") {
  dependsOn(checkGit)
  doLast {
    System.setProperty("COVERAGE", "1")
    processSource()
  }
  finalizedBy(tasks.jacocoTestReport)
}

val kotlinNative = tasks.create("kotlinNative") {
  dependsOn(checkGit)
  doLast {
    System.setProperty("KOTLIN_NATIVE", "1")
    processSource()
  }
}

val kotlinJs = tasks.create("kotlinJs") {
  dependsOn(checkGit)
  doLast {
    System.setProperty("KOTLIN_NATIVE", "1")
    System.setProperty("KOTLIN_JS", "1")
    processSource()
  }
}

fun loadVersion(): String {
  val prefix = "const val VERSION = \""
  val suffix = "\" // _THE_VERSION_"
  val ver = file(kotlin.sourceSets.getByName("main").kotlin.srcDirs.first().path + "/vjson/util/VERSION.kt")
  val lines = ver.readText().split("\n")
  for (l in lines) {
    val line = l.trim()
    if (line.startsWith(prefix) && line.endsWith(suffix)) {
      return line.substring(prefix.length, line.length - suffix.length)
    }
  }
  return "unknown"
}

fun processSource() {
  FilePreprocessor.process(kotlin.sourceSets.getByName("main").kotlin.srcDirs.first().path,
    FilePreprocessor.ProcessParams(
      {
        for ((key) in System.getProperties()) {
          it.define(key as String)
        }
      },
      { true },
      { println("processing $it") }
    ))
}
