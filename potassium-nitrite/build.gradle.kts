/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    id("buildlogic.java-conventions")
    id("io.freefair.lombok") version "8.6"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.serialization") version "1.9.24"
}

dependencies {
    api(projects.nitrite)
    api(projects.nitriteSpatial)
    api(projects.nitriteJacksonMapper)
    api(libs.com.fasterxml.jackson.core.jackson.databind)
    api(libs.com.fasterxml.jackson.module.jackson.module.kotlin)
    api(libs.com.fasterxml.jackson.datatype.jackson.datatype.jdk8)
    api(libs.com.fasterxml.jackson.datatype.jackson.datatype.jsr310)
    api(libs.org.jetbrains.kotlin.kotlin.stdlib)
    api(libs.org.jetbrains.kotlin.kotlin.reflect)
    api(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    testImplementation(libs.org.threeten.threetenbp)
    testImplementation(libs.org.jetbrains.kotlin.kotlin.test.junit)
    testImplementation(libs.junit.junit)
    testImplementation(projects.nitriteMvstoreAdapter)
    testImplementation(libs.com.github.javafaker.javafaker)
    //testImplementation(libs.org.yaml.snakeyaml)
    testImplementation(libs.org.apache.logging.log4j.log4j.api)
    testImplementation(libs.org.apache.logging.log4j.log4j.slf4j2.impl)
    testImplementation(libs.org.apache.logging.log4j.log4j.core)
    compileOnly(libs.org.projectlombok.lombok)
}

description = "Potassium Nitrite"