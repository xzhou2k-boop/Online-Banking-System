# 在线银行系统启动说明

## 项目简介

在线银行系统是一个基于 Spring Boot + Thymeleaf 的前后端分离式银行管理系统，提供用户端和管理员端功能。

## 系统功能

### 用户端功能
- 用户注册/登录
- 账户管理（活期账户、储蓄账户）
- 转账功能（向他人转账、账户间转账）
- 存款/取款
- 预约管理
- 交易记录查询
- 个人资料管理

### 管理员端功能
- 用户管理
- 交易管理
- 预约管理

## 环境要求

- Java 8 或更高版本
- Maven 3.0+

## 快速启动

### 方式一：直接运行 JAR 包

```bash
cd release
java -jar OnlineBankingSystem-1.0.0.jar
```

### 方式二：Maven 运行

```bash
mvn spring-boot:run
```

### 方式三：IDEA/Eclipse 运行

在 IDE 中运行 `UserFrontApplication` 主类即可。

## 访问地址

- 系统地址：http://localhost:8080
- H2 数据库控制台：http://localhost:8080/h2-console

## 默认账户

### 管理员账户
- 用户名：admin
- 密码：admin

### 初始化测试数据（可选）

启动时添加 `init-test-data` 参数即可创建三个测试用户：

```bash
# JAR 包运行
java -jar OnlineBankingSystem-1.0.0.jar init-test-data

# Maven 运行
mvn spring-boot:run -Dspring-boot.run.arguments=init-test-data
```

#### 测试用户账户

| 用户名 | 密码 | 姓名 |
|--------|------|------|
| user1 | password1 | 张三 |
| user2 | password2 | 李四 |
| user3 | password3 | 王五 |

每个测试用户已自动分配活期账户和储蓄账户，初始余额各 5000 元。

## 数据库配置

系统默认使用 H2 内存数据库，配置如下：

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

如需使用 MySQL 数据库，请修改 `src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/OnlineBankingSystem
spring.datasource.username=root
spring.datasource.password=123456
spring.jpa.database-platform=org.hibernate.dialect.MySQL5Dialect
```

## 技术栈

- Spring Boot 2.0.0.M7
- Spring Security
- Spring Data JPA
- Thymeleaf
- H2 Database / MySQL
- Bootstrap + jQuery

## 注意事项

1. 首次启动时，系统会自动创建数据库表结构
2. H2 内存数据库，重启应用数据会丢失
3. 生产环境建议使用 MySQL 数据库
4. 默认端口为 8080，如需修改请在配置文件中添加：
   ```properties
   server.port=8081
   ```
