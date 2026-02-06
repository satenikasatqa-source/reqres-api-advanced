plugins {
    java

}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.rest-assured:rest-assured:5.5.0")
    testImplementation("org.hamcrest:hamcrest:2.2")

    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")

    testImplementation("org.assertj:assertj-core:3.25.3")

    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

    testImplementation("io.qameta.allure:allure-junit5:2.29.0")
    testImplementation("io.qameta.allure:allure-rest-assured:2.29.0")

    testImplementation("com.github.javafaker:javafaker:1.0.2")
}

tasks.test {

    val tags = System.getProperty("tags")
    if (!tags.isNullOrBlank()) {
        val list = tags.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        useJUnitPlatform {
            includeTags(*list.toTypedArray())
        }

        println("Running tests with tags: $list")
    } else {
        useJUnitPlatform()
    }


    val apiKey = System.getProperty("REQRES_API_KEY") ?: System.getenv("REQRES_API_KEY")
    if (!apiKey.isNullOrBlank()) {
        systemProperty("REQRES_API_KEY", apiKey)
    }

 systemProperty("allure.results.directory", "${buildDir}/allure-results")

    doLast {
        copy {
            from("src/test/resources/allure")
            into("${buildDir}/allure-results")
        }
    }
}
