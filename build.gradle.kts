import vpreprocessor.FilePreprocessor
import java.io.ByteArrayOutputStream

buildscript {
  repositories {
    flatDir {
      dirs("$projectDir/bootstrap")
    }
    mavenLocal()
    mavenCentral()
  }
  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    classpath(":vjson-bootstrap")
  }
}

plugins {
  java
  jacoco
  kotlin("jvm") version ("1.4.31")
}

group = "vjson"
version = loadVersion()

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
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

tasks.test {
  include("**/Suite.class")
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
}

tasks.create("checkGit", Exec::class) {
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

tasks.create("bootstrap", Exec::class) {
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

tasks.create("coverage") {
  dependsOn(tasks.getByName("checkGit"))
  doLast {
    System.setProperty("COVERAGE", "1")
    processSource()
  }
  finalizedBy(tasks.jacocoTestReport)
}

tasks.create("kotlinNative") {
  dependsOn("checkGit")
  doLast {
    System.setProperty("KOTLIN_NATIVE", "1")
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
