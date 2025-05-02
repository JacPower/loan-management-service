plugins {
	java
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.hibernate.orm") version "6.6.11.Final"
	id("org.graalvm.buildtools.native") version "0.10.6"
	id("jacoco")
}

group = "com.interview"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.flywaydb:flyway-database-postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.mockito:mockito-core")
	testImplementation("org.mockito:mockito-subclass")
	testImplementation("org.mockito:mockito-junit-jupiter")
}

hibernate {
	enhancement {
		enableAssociationManagement = true
	}
}

tasks.withType<Test> {
	finalizedBy(tasks.jacocoTestReport)
	useJUnitPlatform()
	testLogging {
		events("passed", "skipped", "failed")
	}
	jvmArgs("-Xshare:off")
	jvmArgs("-XX:+EnableDynamicAgentLoading")
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		csv.required.set(false)
		html.required.set(true)
	}

	classDirectories.setFrom(files(classDirectories.files.map {
		fileTree(it) {
			exclude(
				//"com/interview/*",
				"com/interview/exception/*",
				"com/interview/config/*",
				"com/interview/enums/*",
				"com/interview/util/*",
				"com/interview/entity/*",
				"com/interview/repository/*",
				"com/interview/dto/*"
			)
		}
	}))
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			element = "CLASS"
			excludes = listOf(
				"com.interview.*",
				"com.interview.exception.*",
				"com.interview.config.*",
				"com.interview.enums.*",
				"com.interview.util.*",
				"com.interview.entity.*",
				"com.interview.dto.*"
			)

			limit {
				counter = "LINE"
				value = "COVEREDRATIO"
				minimum = 0.85.toBigDecimal()
			}
		}
	}
}

tasks.bootJar {
	exclude("org/projectlombok/**")
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}


