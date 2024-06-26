/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    id("buildlogic.java-conventions")
    id("io.freefair.lombok") version "8.6"
}

dependencies {
    api(projects.nitrite)
    api(libs.org.slf4j.slf4j.api)
    api(libs.com.h2database.h2.mvstore)
    testImplementation(libs.junit.junit)
    testImplementation(libs.org.mockito.mockito.core)
    testImplementation(libs.uk.co.jemos.podam.podam)
    testImplementation(libs.com.github.javafaker.javafaker)
    testImplementation(libs.org.apache.logging.log4j.log4j.api)
    testImplementation(libs.org.apache.logging.log4j.log4j.slf4j2.impl)
    testImplementation(libs.org.apache.logging.log4j.log4j.core)
    testImplementation(libs.org.awaitility.awaitility)
    testImplementation(libs.joda.time.joda.time)
    testImplementation(libs.org.meanbean.meanbean)
    testImplementation(libs.com.fasterxml.jackson.core.jackson.databind)
    testImplementation(libs.commons.io.commons.io)
    //testImplementation(libs.org.yaml.snakeyaml)
    testImplementation(libs.jakarta.xml.bind.jakarta.xml.bind.api)
    testImplementation(libs.com.sun.xml.bind.jaxb.impl)
    testImplementation(libs.com.google.guava.guava)
    compileOnly(libs.org.projectlombok.lombok)
}

description = "Nitrite MVStore Adapter"

java {
    withJavadocJar()
}
