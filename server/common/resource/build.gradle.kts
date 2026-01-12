dependencies {
    api("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    api(project(":common:core"))
    api(project(":common:web"))
    api(project(":common:openfeign"))
}
