plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-config-server")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    implementation(project(":common:core"))
}
