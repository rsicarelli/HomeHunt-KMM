// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath(Build.androidBuildTools)
        classpath(Build.kotlinGradlePlugin)
        classpath(Build.hiltAndroid)
        classpath(Build.googleSecretesPlugin)
        classpath(Build.sqlDelightGradlePlugin)
    }
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}
