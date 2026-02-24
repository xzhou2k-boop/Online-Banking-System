# 修改总结 (2026-02-23)

## 一、修复的Bug

| 文件 | 问题 | 修复方案 |
|------|------|----------|
| `application.properties:43` | H2数据库错误配置MySQL方言 | 注释掉`MySQL5Dialect`配置 |
| `AppointmentServiceImpl.java:27` | `findAppointment()`返回null导致NPE | 实现`findById().orElse(null)`查询 |
| `UserServiceImpl.java:76` | `checkEmailExists(username)`参数错误 | 改为`checkEmailExists(email)` |
| `UserResource.java:51` | 方法名拼写错误`diableUser` | 改为`disableUser` |
| `Role.java:12` | `roleId`缺少`@GeneratedValue`注解 | 添加注解启用自动生成 |
| `HomeController.java:65` | 注册时`ROLE_USER`可能不存在 | 见下方新增功能 |

## 二、修复的安全/业务问题

| 文件 | 问题 | 修复方案 |
|------|------|----------|
| `AccountServiceImpl.java` | 取款无余额检查，可透支 | 添加余额不足校验并抛出异常 |
| `TransactionServiceImpl.java` | 账户间转账无余额检查 | 添加余额不足校验 |
| `TransactionServiceImpl.java` | 给他人转账无余额检查 | 添加余额不足校验 |
| `RequestFilter.java` | 异常处理不规范(仅printStackTrace) | 使用SLF4J日志+正确声明异常 |
| `AccountController.java` | 存取款无金额有效性验证 | 添加金额必须为正数校验 |
| `TransferController.java` | 转账无金额有效性验证 | 添加金额必须为正数校验 |

## 三、新增功能

| 文件 | 功能说明 |
|------|----------|
| `InitialDataLoader.java` | 系统启动时初始化：创建`ROLE_USER`、`ROLE_ADMIN`角色和管理员账号 |
| `GlobalExceptionHandler.java` | 全局异常处理器，统一捕获并提示运行时异常 |
| `userFront.html` | 添加错误消息显示区域 |

## 四、默认账号信息

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | `admin` | `admin` |
| 普通用户 | 自行注册 | - |

## 五、界面汉化 (2026-02-23 更新)

### 汉化的模板文件
| 文件 | 汉化内容 |
|------|----------|
| `header.html` | 导航栏菜单（首页、账户管理、转账汇款、预约服务、我的） |
| `index.html` | 登录页面（标题、表单标签、按钮） |
| `signup.html` | 注册页面（标题、表单标签、按钮、提示信息） |
| `userFront.html` | 主页（账户余额、存取款入口） |
| `deposit.html` | 存款页面（标签、按钮） |
| `withdraw.html` | 取款页面（标签、按钮） |
| `primaryAccount.html` | 主账户页面（余额标题、交易记录表头） |
| `savingsAccount.html` | 储蓄账户页面（余额标题、交易记录表头） |
| `betweenAccounts.html` | 账户间转账页面（标签、按钮） |
| `toSomeoneElse.html` | 向他人转账页面（标签、按钮） |
| `recipient.html` | 收款人管理页面（表单标签、列表标题） |
| `appointment.html` | 预约页面（标签、地点选项改为国内城市） |
| `profile.html` | 个人资料页面（标签、按钮） |

### 汉化的Java代码
| 文件 | 汉化内容 |
|------|----------|
| `AccountServiceImpl.java` | 交易描述、错误提示信息 |
| `TransactionServiceImpl.java` | 交易描述、错误提示信息 |
| `AccountController.java` | 验证错误提示信息 |
| `TransferController.java` | 验证错误提示信息 |
| `GlobalExceptionHandler.java` | 异常提示信息 |

### 其他修改
- 所有HTML文件的`lang`属性从`en`改为`zh-CN`
- 预约地点从孟加拉城市改为国内城市（北京、上海、广州等）

## 六、文件变更统计

- **修改文件**: 25个
- **新增文件**: 2个
- **编译状态**: BUILD SUCCESS
| `RequestFilter.java` | 异常处理不规范(仅printStackTrace) | 使用SLF4J日志+正确声明异常 |
| `AccountController.java` | 存取款无金额有效性验证 | 添加金额必须为正数校验 |
| `TransferController.java` | 转账无金额有效性验证 | 添加金额必须为正数校验 |

## 三、新增功能

| 文件 | 功能说明 |
|------|----------|
| `InitialDataLoader.java` | 系统启动时初始化：创建`ROLE_USER`、`ROLE_ADMIN`角色和管理员账号 |
| `GlobalExceptionHandler.java` | 全局异常处理器，统一捕获并提示运行时异常 |
| `userFront.html` | 添加错误消息显示区域 |

## 四、默认账号信息

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | `admin` | `admin` |
| 普通用户 | 自行注册 | - |

## 五、文件变更统计

- **修改文件**: 10个
- **新增文件**: 2个
- **编译状态**: BUILD SUCCESS

## 六、详细修改说明

### 1. 数据库配置修复 (application.properties)
```properties
# 原来：同时配置H2和MySQL方言导致冲突
# 修复：注释掉MySQL方言，使用H2方言
# spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL5Dialect
```

### 2. 空指针异常修复 (AppointmentServiceImpl.java)
```java
// 原来：直接返回null，导致confirmAppointment()出现NPE
public Appointment findAppointment(Long id) {
    return null;
}

// 修复：实现正确的查询逻辑
public Appointment findAppointment(Long id) {
    return appointmentDao.findById(id).orElse(null);
}
```

### 3. 参数错误修复 (UserServiceImpl.java)
```java
// 原来：错误地使用username参数检查邮箱
if (checkUsernameExists(username) || checkEmailExists(username))

// 修复：使用正确的email参数
if (checkUsernameExists(username) || checkEmailExists(email))
```

### 4. 方法名拼写修复 (UserResource.java)
```java
// 原来：方法名拼写错误
public void diableUser(@PathVariable("username") String username)

// 修复：正确的方法名
public void disableUser(@PathVariable("username") String username)
```

### 5. 实体注解修复 (Role.java)
```java
// 原来：缺少@GeneratedValue，导致ID无法自动生成
@Id
// @GeneratedValue(strategy = GenerationType.AUTO)
private int roleId;

// 修复：启用注解
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private int roleId;
```

### 6. 余额检查增强 (AccountServiceImpl.java, TransactionServiceImpl.java)
```java
// 新增：取款前检查余额
if (primaryAccount.getAccountBalance().compareTo(new BigDecimal(amount)) < 0) {
    throw new RuntimeException("Insufficient funds in Primary Account");
}

// 新增：转账前检查余额
if (primaryAccount.getAccountBalance().compareTo(transferAmount) < 0) {
    throw new Exception("Insufficient funds in Primary Account");
}
```

### 7. 金额验证增强 (AccountController.java, TransferController.java)
```java
// 新增：金额有效性验证
double amount = Double.parseDouble(amountStr);
if (amount <= 0) {
    throw new IllegalArgumentException("Amount must be positive");
}
```

### 8. 异常处理规范 (RequestFilter.java)
```java
// 原来：仅打印堆栈，吞掉异常
try {
    chain.doFilter(req, res);
} catch(Exception e) {
    e.printStackTrace();
}

// 修复：使用SLF4J日志并正确声明异常
public void doFilter(...) throws IOException, ServletException {
    // ...
    chain.doFilter(req, res);
}
```

### 9. 全局异常处理器 (GlobalExceptionHandler.java)
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, RedirectAttributes redirectAttributes) {
        LOG.error("Runtime exception occurred: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/userFront";
    }
}
```

### 10. 系统初始化 (InitialDataLoader.java)
```java
@Component
public class InitialDataLoader implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        // 初始化角色
        if (roleDao.findByName("ROLE_USER") == null) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleDao.save(userRole);
        }
        
        // 初始化管理员账号
        if (userDao.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            // ... 设置其他属性并保存
        }
    }
}
```

---

## 七、向他人转账功能修复 (2026-02-24)

### 问题描述
向他人转账时，转出方账户余额减少，但收款人账户余额没有增加。

### 修复方案
修改 `TransactionServiceImpl.toSomeoneElseTransfer()` 方法：
1. 根据收款人的账户号码查找对应的用户
2. 将转账金额增加到收款人的对应账户
3. 同时记录收款人的交易流水

### 修改文件
- `TransactionServiceImpl.java`

### 代码示例
```java
// 通过收款人账户号码查找用户
User recipientUser = userService.findByUsername(recipient.getAccountNumber());

if (recipientUser == null) {
    throw new RuntimeException("收款人账户不存在，无法转账");
}

// 增加收款人账户余额
if (accountType.equalsIgnoreCase("Primary")) {
    recipientPrimaryAccount.setAccountBalance(
        recipientPrimaryAccount.getAccountBalance().add(transferAmount));
    primaryAccountDao.save(recipientPrimaryAccount);
    
    // 记录收款人交易
    PrimaryTransaction recipientTransaction = new PrimaryTransaction(
        date, "收到来自" + recipient.getName() + "的转账", 
        "Transfer", "Finished", amount, 
        recipientPrimaryAccount.getAccountBalance(), recipientPrimaryAccount);
    primaryTransactionDao.save(recipientTransaction);
}
```

---

## 八、新增测试数据辅助工具 (2026-02-24)

### 新增文件
| 文件 | 功能说明 |
|------|----------|
| `TestDataGenerator.java` | 测试数据生成器，可生成3个测试用户 |
| `ToolResource.java` | REST API端点，用于触发测试数据生成 |

### 测试用户
| 用户名 | 密码 | 姓名 | 邮箱 |
|--------|------|------|------|
| user1 | password1 | 张三 | user1@bank.com |
| user2 | password2 | 李四 | user2@bank.com |
| user3 | password3 | 王五 | user3@bank.com |

### 使用方式
1. **API调用**：GET /api/tool/generate-test-users (需要管理员权限)
2. **命令行**：./mvnw spring-boot:run -Dspring-boot.run.arguments=init-test-data
