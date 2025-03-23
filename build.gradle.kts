plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
}

group = "org.beyondmedicine"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly("com.mysql:mysql-connector-j:8.2.0")
	
	// Swagger 관련 의존성
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.4.0")
	
	// 테스트 라이브러리
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(module = "mockito-core") // Mockito 대신 MockK 사용
	}
	testImplementation("io.mockk:mockk:1.13.9") // MockK 추가
	testImplementation("com.ninja-squad:springmockk:4.0.2") // Spring에서 MockK 사용
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("com.h2database:h2") // H2 인메모리 데이터베이스
	testImplementation("io.kotest:kotest-runner-junit5:5.9.1") // Kotest
	testImplementation("io.kotest:kotest-assertions-core:5.9.1") // Kotest Assertions
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3") // Kotest Spring Extension
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
