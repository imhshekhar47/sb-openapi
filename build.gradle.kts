import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.7"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("kapt") version "1.6.21"

    id("org.openapi.generator") version "6.0.1"
}

group = "org.hshekhar"
version = "1.0.0-SNAPSHOT"
java {
    sourceCompatibility = JavaVersion.VERSION_11
}


sourceSets.getByName("main") {
    java.srcDirs(
        "src/main/kotlin",
        "${buildDir}/generated/src/main/kotlin",
    )
}

repositories {
    mavenCentral()
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    implementation("org.springframework.boot:spring-boot-starter-web")


    // openapi document
    implementation("org.springdoc:springdoc-openapi-ui:1.6.14")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.0")

    // persistence
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.oracle.database.jdbc:ojdbc8")

    // mapper
    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    kapt("org.mapstruct:mapstruct-processor:1.5.3.Final")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

springBoot {
    buildInfo {
        properties {
            artifact = project.name
            version = "${project.version}"
            group = "${project.group}"
            name = "User management APIs"
        }
    }
}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("generate") {
    generatorName.set("kotlin-spring")
    inputSpec.set("${project.rootDir}/api.yaml")
    outputDir.set("${project.buildDir}/generated")
    apiPackage.set("${project.group}.api")
    modelPackage.set("${project.group}.model")
    packageName.set("${project.group}")
    invokerPackage.set("${project.group}")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "swaggerAnnotations" to "true",
            "serviceInterface" to "true",
            "serializationLibrary" to "jackson",
            "modelMutable" to "true",
            "enumPropertyNaming" to "original",
            "collectionType" to "list"
        )
    )
    globalProperties.set(
        mapOf(
            "modelDocs" to "false"
        )
    )
    generateApiTests.set(false)
    generateApiDocumentation.set(true)
    enablePostProcessFile.set(false)
    logToStderr.set(true)
    doLast {
        val generatedApiDir =
            "${project.buildDir}/generated/src/main/kotlin/${project.group.toString().replace(".", "/")}"

        val dropApis = listOf<String>()

        delete(
            //file("$generatedApiDir/SpringDocConfiguration.kt"),
            file("$generatedApiDir/api/Exceptions.kt"),
            file("$generatedApiDir/Application.kt"),
            dropApis.map { File("$generatedApiDir" + File.separator + "api", "${it}Api.kt") },
            dropApis.map { File("$generatedApiDir" + File.separator + "api", "${it}ApiService.kt") },
            dropApis.map { File("$generatedApiDir" + File.separator + "api", "${it}ApiController.kt") },
        )
    }
}

tasks.getByName("compileKotlin").dependsOn("generate")


tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    manifest {
        attributes(
            "Main-Class" to "org.springframework.boot.loader.JarLauncher",
            "Start-Class" to "${project.group}.ApplicationKt",
            "Implementation-Version" to "${project.version}",
            "Implementation-Title" to "${project.name}"
        )
    }

    launchScript {
        properties(mapOf(
            "mode" to "service",
            "logFilename" to "${project.name}.log",
        ))
    }
}


tasks.create("postBuild") {
    doLast {
        val plainJar = File("${project.buildDir}/libs", "${project.name}-${project.version}-plain.jar")
        if (plainJar.exists()) {
            plainJar.delete()
        }
    }
}

tasks.findByName("build")?.finalizedBy("postBuild")