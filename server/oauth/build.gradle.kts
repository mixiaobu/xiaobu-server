plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.session:spring-session-data-redis")

    runtimeOnly("com.mysql:mysql-connector-j")

    implementation(project(":common:config"))
    implementation(project(":common:core"))
    implementation(project(":common:mybatis-plus"))
    implementation(project(":common:openfeign"))
    implementation(project(":common:redis"))
    implementation(project(":common:resource"))
    implementation(project(":common:web"))
}
