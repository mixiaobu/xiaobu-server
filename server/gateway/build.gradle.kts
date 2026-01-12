plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webmvc")

    implementation(project(":common:config"))
    implementation(project(":common:core"))
}
