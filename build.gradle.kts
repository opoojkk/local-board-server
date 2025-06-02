val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.1.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
}

group = "com.joykeepsflowin"
version = "0.0.2"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-compression")
    implementation("io.ktor:ktor-server-netty")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    // MySQL 驱动
    implementation("mysql:mysql-connector-java:8.0.33")

    // 连接池（必选）
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Exposed ORM（可选但推荐）
    implementation("org.jetbrains.exposed:exposed-core:0.50.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.50.0")

    // 方案1选项B：Kotlinx.DateTime（跨平台）
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.41.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    // Ktor JWT 认证
    implementation("io.ktor:ktor-server-auth:3.1.3")
    implementation("io.ktor:ktor-server-auth-jwt:3.1.3")

    // bcrypt 依赖
    implementation("at.favre.lib:bcrypt:0.9.0")
}
