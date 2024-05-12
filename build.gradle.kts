// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.android.library") version "8.1.4" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version("2.0.1") apply false
    id("org.sonarqube") version "5.0.0.4638"
}

buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}

sonar {
//    val properties = java.util.Properties().apply {
//        load(rootProject.file("local.properties").reader())
//    }
//    val mySecret = properties["SONAR_TOKEN"] as String
    properties {
        property("sonar.projectKey", "minhhpkp_RideSharingApp")
        property("sonar.organization", "minhhpkp")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}