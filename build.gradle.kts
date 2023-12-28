import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.diegolirio"
version = "0.0.1-SNAPSHOT"

plugins {
	//id("io.gitlab.arturbosch.detekt") version "1.17.0"
	kotlin("jvm") version "1.9.21"  apply false
	kotlin("plugin.spring") version "1.9.21"
}

buildscript {
	repositories {
		mavenCentral()
	}
}
