# 小布后台框架（服务端）

这是一个基于 Kotlin + Spring Boot 4 / Spring Cloud 的多模块微服务后台框架，包含服务注册与发现、网关、OAuth2 授权服务器、用户中心等基础能力，并提供一组通用基础模块（配置、核心工具、MyBatis-Plus、OpenFeign、Redis、资源服务、Web 组件等）。

## 技术栈

- **语言与构建**：Kotlin 2.3、Gradle Kotlin DSL、Java 25
- **Spring 体系**：Spring Boot 4.0.1、Spring Cloud 2025.1.0
- **数据访问**：MyBatis-Plus
- **注册与发现**：Netflix Eureka
- **鉴权**：Spring Authorization Server（OAuth2）
- **网关**：Spring Cloud Gateway（WebMVC 版本）
- **其他**：OpenFeign、Redis、Fastjson2、Hutool

## 模块划分

### 业务/运行模块

- **eureka**：服务注册中心，负责服务发现。默认端口 `8080`。
- **gateway**：统一网关入口，负责路由与转发。默认端口 `8088`。
- **oauth**：OAuth2 授权服务器，包含自定义密码模式（Password Grant）扩展。默认端口 `8082`。
- **user**：用户中心服务，提供用户、角色、权限等数据与接口。默认端口 `8083`。

### common 公共模块

- **common:config**：Eureka 客户端相关配置依赖。
- **common:core**：通用实体、常量、工具类与异常封装。
- **common:mybatis-plus**：MyBatis-Plus 配置与类型处理。
- **common:openfeign**：OpenFeign 客户端与拦截器封装。
- **common:redis**：Redis 配置与服务封装。
- **common:resource**：资源服务器（JWT 资源保护、权限验证、CORS）配置。
- **common:web**：Web MVC 相关通用配置与工具。

## 配置说明

- **端口与服务名**：见各模块 `application.yml`。
- **环境配置**：各模块在 `application-dev.yml` / `application-prod.yml` / `application-test.yml` 中配置数据库、Redis、Eureka 地址等。
- **授权服务器密钥**：`oauth` 模块通过类路径下的 `xiaobu.jks` 生成 JWT 签名密钥，需要启动时指定系统属性：
  ```bash
  -Dkeystore.password=你的密钥密码
  ```

## 网关路由

`gateway` 模块默认路由如下：

- `/oauth/**` → `lb://oauth`
- `/user/**` → `lb://user`
- `/file/**` → `lb://file`
- `/xiaobu/**` → `lb://xiaobu`

## 快速启动

> 需先准备 **MySQL** 与 **Redis**，并根据环境文件修改连接配置。

1. 启动 Eureka 注册中心：
   ```bash
   ./gradlew :eureka:bootRun
   ```
2. 启动授权服务器（需要 JKS 密码）：
   ```bash
   ./gradlew :oauth:bootRun -Dkeystore.password=你的密钥密码
   ```
3. 启动用户服务：
   ```bash
   ./gradlew :user:bootRun
   ```
4. 启动网关：
   ```bash
   ./gradlew :gateway:bootRun
   ```

启动后可通过网关访问服务，例如：`http://localhost:8088/user/...`。

## 认证与鉴权要点

- 授权服务器支持 **Authorization Code、Client Credentials、Refresh Token** 以及自定义 **Password Grant**。
- 资源服务通过 JWT 校验并从用户中心加载权限列表，实现基于权限的访问控制。

## 数据库与缓存

- `oauth`、`user` 模块默认使用 MySQL（见 `application-*.yml`）。
- Redis 用于 Session 与缓存相关能力（见 `application-*.yml`）。

## 开发建议

- 修改数据库、Redis、Eureka 地址等配置时，请优先调整对应环境配置文件。
- 服务之间通过 OpenFeign 调用，公共模型与工具类位于 `common` 模块。
