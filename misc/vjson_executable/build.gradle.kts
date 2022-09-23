import org.gradle.internal.os.OperatingSystem

plugins {
  kotlin("multiplatform") version "1.7.10"
}

repositories {
  mavenLocal()
  mavenCentral()
}

kotlin {
  val os = OperatingSystem.current()
  if (os == OperatingSystem.MAC_OS) {
    macosX64("native") {
      binaries {
        executable()
      }
    }
  } else if (os == OperatingSystem.LINUX) {
    linuxX64("native") {
      binaries {
        executable()
      }
    }
  } else if (os == OperatingSystem.WINDOWS) {
    mingwX64("native") {
      binaries {
        executable()
      }
    }
  }

  kotlin {
    sourceSets {
      val nativeMain by getting {
        dependencies {
          implementation("com.squareup.okio:okio:3.2.0")
          implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
        }
      }
    }
  }
}
