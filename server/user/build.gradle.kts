plugins {
    id("org.springframework.boot")
}

dependencies {
    runtimeOnly("com.mysql:mysql-connector-j")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation(project(":common:config"))
    implementation(project(":common:core"))
    implementation(project(":common:mybatis-plus"))
    implementation(project(":common:openfeign"))
    implementation(project(":common:redis"))
    implementation(project(":common:resource"))
    implementation(project(":common:web"))
}
