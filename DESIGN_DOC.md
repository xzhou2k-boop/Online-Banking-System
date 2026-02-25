# 在线银行系统设计文档

## 文档信息

| 项目 | 内容 |
|------|------|
| 项目名称 | 在线银行系统 (Online Banking System) |
| 版本 | v1.1 |
| 创建日期 | 2026-02-23 |
| 更新日期 | 2026-02-25 |
| 技术栈 | Spring Boot 2.0 + Spring Security + Thymeleaf + JPA + H2 |

---

## 一、系统架构设计

### 1.1 整体架构

系统采用经典的三层架构（3-Tier Architecture）：

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                      │
│                     (表现层 - Controller)                    │
├─────────────────────────────────────────────────────────────┤
│                      Service Layer                          │
│                      (业务层 - Service)                      │
├─────────────────────────────────────────────────────────────┤
│                       Data Layer                            │
│                      (数据层 - DAO)                          │
├─────────────────────────────────────────────────────────────┤
│                    Database Layer                           │
│                      (数据库 - H2/MySQL)                     │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 技术架构

```
┌──────────────────────────────────────────────────────────────┐
│                        前端 (Thymeleaf)                       │
│         HTML + CSS + JavaScript + Bootstrap                  │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│                    Spring MVC Controller                     │
│    HomeController, AccountController, TransferController     │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│                       Service 层                             │
│   AccountService, TransactionService, UserService            │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│                        DAO 层                                │
│        Spring Data JPA (CrudRepository)                      │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│                      数据库层                                 │
│                   H2 / MySQL                                 │
└──────────────────────────────────────────────────────────────┘
```

### 1.3 包结构设计

```
com.userfront/
├── UserFrontApplication.java          # Spring Boot 启动类
├── config/                           # 配置包
│   ├── SecurityConfig.java           # Spring Security 配置
│   ├── RequestFilter.java            # CORS 跨域过滤器
│   ├── GlobalExceptionHandler.java   # 全局异常处理器
│   └── InitialDataLoader.java        # 系统数据初始化
├── controller/                       # 控制器包 (表现层)
│   ├── HomeController.java           # 首页控制器
│   ├── UserController.java           # 用户控制器
│   ├── AccountController.java        # 账户控制器
│   ├── TransferController.java       # 转账控制器
│   └── AppointmentController.java    # 预约控制器
├── service/                          # 业务接口包
│   ├── UserService.java              # 用户服务接口
│   ├── AccountService.java           # 账户服务接口
│   ├── TransactionService.java       # 交易服务接口
│   ├── AppointmentService.java       # 预约服务接口
│   └── UserServiceImpl/              # 业务实现包
│       ├── UserServiceImpl.java
│       ├── AccountServiceImpl.java
│       ├── TransactionServiceImpl.java
│       ├── AppointmentServiceImpl.java
│       └── UserSecurityService.java  # Spring Security 用户认证
├── dao/                              # 数据访问包
│   ├── UserDao.java
│   ├── RoleDao.java
│   ├── PrimaryAccountDao.java
│   ├── SavingsAccountDao.java
│   ├── PrimaryTransactionDao.java
│   ├── SavingsTransactionDao.java
│   ├── RecipientDao.java
│   └── AppointmentDao.java
├── domain/                           # 实体包
│   ├── User.java                     # 用户实体
│   ├── PrimaryAccount.java           # 主账户实体
│   ├── SavingsAccount.java           # 储蓄账户实体
│   ├── PrimaryTransaction.java       # 主账户交易实体
│   ├── SavingsTransaction.java       # 储蓄账户交易实体
│   ├── Recipient.java                # 收款人实体
│   ├── Appointment.java              # 预约实体
│   └── security/                    # 安全相关实体
│       ├── Role.java                 # 角色实体
│       ├── Authority.java             # 权限实体
│       └── UserRole.java             # 用户角色关联实体
└── resource/                         # REST API 资源包
    ├── UserResource.java             # 用户资源
    └── AppointmentResource.java      # 预约资源
```

---

## 二、数据库设计

### 2.1 实体关系图

```
┌─────────────────┐       ┌─────────────────┐
│      User       │       │      Role       │
├─────────────────┤       ├─────────────────┤
│ userId (PK)     │       │ roleId (PK)     │
│ username        │       │ name            │
│ password        │       └────────┬────────┘
│ firstName       │                │
│ lastName        │       ┌────────┴────────┐
│ email           │       │   UserRole      │
│ phone           │       ├─────────────────┤
│ enabled         │       │ userRoleId (PK) │
└───────┬─────────┘       │ userId (FK)     │
        │                 │ roleId (FK)     │
        │                 └────────┬────────┘
        │                          │
        │                 ┌─────────┴─────────┐
        │                 │                   │
        ▼                 ▼                   ▼
┌───────────────┐  ┌──────────────┐  ┌──────────────┐
│PrimaryAccount │  │SavingsAccount│  │  Appointment │
├───────────────┤  ├──────────────┤  ├──────────────┤
│ id (PK)       │  │ id (PK)      │  │ id (PK)      │
│ accountNumber │  │ accountNumber│  │ date         │
│ accountBalance│  │ accountBalance│ │ location     │
│ userId (FK)   │  │ userId (FK)  │  │ description  │
└───────┬───────┘  └───────┬───────┘ │ confirmed    │
        │                  │         │ userId (FK)  │
        │                  │         └──────────────┘
        │                  │
        ▼                  ▼
┌───────────────┐  ┌──────────────┐
│PrimaryTrans.  │  │SavingsTrans. │
├───────────────┤  ├──────────────┤
│ id (PK)       │  │ id (PK)      │
│ date          │  │ date         │
│ description   │  │ description  │
│ type          │  │ type         │
│ status        │  │ status       │
│ amount        │  │ amount       │
│ balance       │  │ balance      │
│ accountId(FK) │  │ accountId(FK)│
└───────────────┘  └──────────────┘

┌───────────────┐
│   Recipient   │
├───────────────┤
│ id (PK)       │
│ name          │
│ email         │
│ phone         │
│ accountNumber │
│ description   │
│ userId (FK)   │
└───────────────┘
```

### 2.2 数据库表结构

#### 2.2.1 用户表 (User)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| user_id | BIGINT | PK, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(255) | NOT NULL, UNIQUE | 用户名 |
| password | VARCHAR(255) | NOT NULL | 密码(加密存储) |
| first_name | VARCHAR(255) | NOT NULL | 名 |
| last_name | VARCHAR(255) | NOT NULL | 姓 |
| email | VARCHAR(255) | NOT NULL, UNIQUE | 邮箱 |
| phone | VARCHAR(255) | - | 电话 |
| enabled | BOOLEAN | DEFAULT TRUE | 账号是否启用 |

#### 2.2.2 角色表 (Role)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| role_id | INT | PK | 角色ID |
| name | VARCHAR(255) | NOT NULL | 角色名称 |

**预置角色**:
- `ROLE_USER` - 普通用户
- `ROLE_ADMIN` - 管理员

#### 2.2.3 用户角色关联表 (User_Role)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| user_role_id | BIGINT | PK, AUTO_INCREMENT | 关联ID |
| user_id | BIGINT | FK | 用户ID |
| role_id | INT | FK | 角色ID |

#### 2.2.4 主账户表 (Primary_Account)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 账户ID |
| account_number | INT | NOT NULL, UNIQUE | 账户号码 |
| account_balance | DECIMAL(19,2) | NOT NULL | 账户余额 |

#### 2.2.5 储蓄账户表 (Savings_Account)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 账户ID |
| account_number | INT | NOT NULL, UNIQUE | 账户号码 |
| account_balance | DECIMAL(19,2) | NOT NULL | 账户余额 |

#### 2.2.6 主账户交易表 (Primary_Transaction)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 交易ID |
| date | DATETIME | NOT NULL | 交易日期 |
| description | VARCHAR(255) | - | 交易描述 |
| type | VARCHAR(255) | - | 交易类型 |
| status | VARCHAR(255) | - | 交易状态 |
| amount | DOUBLE | NOT NULL | 交易金额 |
| available_balance | DECIMAL(19,2) | - | 可用余额 |
| primary_account_id | BIGINT | FK | 关联账户ID |

#### 2.2.7 储蓄账户交易表 (Savings_Transaction)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 交易ID |
| date | DATETIME | NOT NULL | 交易日期 |
| description | VARCHAR(255) | - | 交易描述 |
| type | VARCHAR(255) | - | 交易类型 |
| status | VARCHAR(255) | - | 交易状态 |
| amount | DOUBLE | NOT NULL | 交易金额 |
| available_balance | DECIMAL(19,2) | - | 可用余额 |
| savings_account_id | BIGINT | FK | 关联账户ID |

#### 2.2.8 收款人表 (Recipient)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 收款人ID |
| name | VARCHAR(255) | NOT NULL | 姓名 |
| email | VARCHAR(255) | - | 邮箱 |
| phone | VARCHAR(255) | - | 电话 |
| account_number | VARCHAR(255) | NOT NULL | 银行账号 |
| description | VARCHAR(255) | - | 备注 |
| user_id | BIGINT | FK | 所属用户ID |

#### 2.2.9 预约表 (Appointment)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 预约ID |
| date | DATETIME | NOT NULL | 预约日期时间 |
| location | VARCHAR(255) | NOT NULL | 办理地点 |
| description | VARCHAR(255) | - | 备注说明 |
| confirmed | BOOLEAN | DEFAULT FALSE | 是否确认 |
| user_id | BIGINT | FK | 预约用户ID |

---

## 三、功能模块设计

### 3.1 功能模块总览

<img src="C:\Users\zhouxin\AppData\Roaming\Typora\typora-user-images\image-20260223191135241.png" alt="image-20260223191135241" style="zoom: 33%;" />

### 3.2 用户模块

#### 3.2.1 功能列表

| 功能 | 说明 |
|------|------|
| 用户注册 | 新用户注册账号，系统自动创建主账户和储蓄账户 |
| 用户登录 | 使用用户名密码登录，支持"记住我"功能 |
| 个人资料管理 | 查看和修改个人信息（姓名、电话、邮箱） |
| 权限管理 | 支持 ROLE_USER 和 ROLE_ADMIN 两种角色 |

#### 3.2.2 用户注册流程

<img src="C:\Users\zhouxin\AppData\Roaming\Typora\typora-user-images\image-20260223192106833.png" alt="image-20260223192106833" style="zoom:50%;" />

#### 3.2.3 核心类设计

**UserService 接口**:

```java
public interface UserService {
    User findByUsername(String username);
    User findByEmail(String email);
    boolean checkUserExists(String username, String email);
    boolean checkUsernameExists(String username);
    boolean checkEmailExists(String email);
    void save(User user);
    User createUser(User user, Set<UserRole> userRoles);
    User saveUser(User user);
    List<User> findUserList();
    void enableUser(String username);
    void disableUser(String username);
}
```

#### 3.2.4 管理员账户保护

为保护系统安全，管理员账户（username='admin'）具有以下保护机制：

| 保护项 | 说明 |
|--------|------|
| 禁用保护 | 管理员账户无法被禁用 |
| 前端提示 | 用户管理页面显示"管理员账户受保护"提示 |
| 操作拦截 | 禁用操作自动忽略管理员账户 |

**前端实现**：
- 在用户列表中，管理员账户的禁用按钮显示为禁用状态或显示保护提示
- 鼠标悬停时显示提示信息："管理员账户受保护，无法禁用"

### 3.3 账户模块

#### 3.3.1 功能列表

| 功能 | 说明 |
|------|------|
| 存款 | 向主账户或储蓄账户存入资金 |
| 取款 | 从主账户或储蓄账户取出资金 |
| 余额查询 | 查看主账户和储蓄账户当前余额 |
| 交易记录 | 查看账户的所有交易流水 |
| 交易金额显示 | 按中国习惯显示正负号和颜色 |

#### 3.3.2 存款流程

```
用户选择账户类型(Primary/Savings)
       │
       ▼
输入存款金额
       │
       ▼
金额验证(必须>0)
       │
       ▼
更新账户余额 = 当前余额 + 存款金额
       │
       ▼
创建交易记录
       │
       ▼
保存交易记录
       │
       ▼
返回成功，跳转主页
```

#### 3.3.3 取款流程

```
用户选择账户类型(Primary/Savings)
       │
       ▼
输入取款金额
       │
       ▼
金额验证(必须>0)
       │
       ▼
余额检查(取款金额 <= 当前余额)
       │
       ▼ 否 ───────────> 抛出异常: 余额不足
       │
       ▼ 是
更新账户余额 = 当前余额 - 取款金额
       │
       ▼
创建交易记录
       │
       ▼
保存交易记录
       │
       ▼
返回成功，跳转主页
```

#### 3.3.4 核心类设计

**AccountService 接口**:
```java
public interface AccountService {
    PrimaryAccount createPrimaryAccount();
    SavingsAccount createSavingsAccount();
    void deposit(String accountType, double amount, Principal principal);
    void withdraw(String accountType, double amount, Principal principal);
}
```

#### 3.3.5 交易金额显示规则

系统根据中国用户习惯设计交易金额显示方式：

| 交易类型 | 金额符号 | 颜色 | 说明 |
|---------|---------|------|------|
| 存款 | 正数 | 红色 | 收入显示为红色 |
| 取款 | 负数 | 绿色 | 支出显示为绿色 |
| 转账转入 | 正数 | 红色 | 收入显示为红色 |
| 转账转出 | 负数 | 绿色 | 支出显示为绿色 |

**实现说明**：
- 后端在创建交易记录时，根据交易类型设置正确的正负符号
- 前端根据金额符号判断收入/支出，并应用相应的颜色样式

### 3.4 转账模块

#### 3.4.1 功能列表

| 功能 | 说明 |
|------|------|
| 账户间转账 | 在主账户和储蓄账户之间互转资金 |
| 向他人转账 | 向已添加的收款人转账 |
| 收款人管理 | 添加、编辑、删除收款人信息 |

#### 3.4.2 账户间转账流程

```
用户选择转出账户
       │
       ▼
选择转入账户
       │
       ▼
输入转账金额
       │
       ▼
金额验证(必须>0)
       │
       ▼
余额检查(转账金额 <= 转出账户余额)
       │
       ▼ 否 ───────────> 抛出异常: 余额不足
       │
       ▼ 是
更新转出账户余额 = 当前余额 - 转账金额
       │
       ▼
更新转入账户余额 = 当前余额 + 转账金额
       │
       ▼
创建转出账户交易记录
       │
       ▼
创建转入账户交易记录
       │
       ▼
保存交易记录
       │
       ▼
返回成功，跳转主页
```

#### 3.4.3 核心类设计

**TransactionService 接口**:
```java
public interface TransactionService {
    List<PrimaryTransaction> findPrimaryTransactionList(String username);
    List<SavingsTransaction> findSavingsTransactionList(String username);
    void savePrimaryDepositTransaction(PrimaryTransaction primaryTransaction);
    void saveSavingsDepositTransaction(SavingsTransaction savingsTransaction);
    void savePrimaryWithdrawTransaction(PrimaryTransaction primaryTransaction);
    void saveSavingsWithdrawTransaction(SavingsTransaction savingsTransaction);
    void betweenAccountsTransfer(String transferFrom, String transferTo, 
                                  String amount, PrimaryAccount primaryAccount, 
                                  SavingsAccount savingsAccount) throws Exception;
    List<Recipient> findRecipientList(Principal principal);
    Recipient saveRecipient(Recipient recipient);
    Recipient findRecipientByName(String recipientName);
    void deleteRecipientByName(String recipientName);
    void toSomeoneElseTransfer(Recipient recipient, String accountType, 
                                String amount, PrimaryAccount primaryAccount, 
                                SavingsAccount savingsAccount);
}
```

### 3.5 预约模块

#### 3.5.1 功能列表

| 功能 | 说明 |
|------|------|
| 创建预约 | 用户预约银行网点办理业务 |
| 查看预约 | 查看所有预约记录(管理员可见全部) |
| 确认预约 | 管理员确认用户预约 |

#### 3.5.2 核心类设计

**AppointmentService 接口**:
```java
public interface AppointmentService {
    Appointment createAppointment(Appointment appointment);
    List<Appointment> findAll();
    Appointment findAppointment(Long id);
    void confirmAppointment(Long id);
}
```

### 3.6 安全模块

#### 3.6.1 安全架构

系统使用 Spring Security 实现安全认证：

| 组件 | 说明 |
|------|------|
| BCryptPasswordEncoder | 密码加密(强度12) |
| UserSecurityService | 实现 UserDetailsService 接口 |
| SecurityConfig | Spring Security 配置类 |
| RequestFilter | CORS 跨域过滤器 |

#### 3.6.2 权限控制

| URL | 角色要求 | 说明 |
|-----|---------|------|
| /index | 公开 | 登录页 |
| /signup | 公开 | 注册页 |
| /userFront | ROLE_USER | 用户首页 |
| /account/* | ROLE_USER | 账户相关 |
| /transfer/* | ROLE_USER | 转账相关 |
| /appointment/* | ROLE_USER | 预约相关 |
| /user/profile | ROLE_USER | 个人资料 |
| /api/* | ROLE_ADMIN | REST API |
| /admin/* | ROLE_ADMIN | 管理员页面 |

#### 3.7 管理员功能模块

#### 3.7.1 管理员菜单

系统为管理员用户显示"系统管理"菜单，包含以下子菜单：

| 子菜单 | URL | 功能 |
|--------|-----|------|
| 用户管理 | /admin/users | 查看、启用/禁用用户 |
| 预约管理 | /admin/appointments | 查看、确认用户预约 |
| 交易监控 | /admin/transactions | 查看所有用户的交易记录 |

#### 3.7.2 预约列表功能

用户可以查看自己的预约列表：

- 新增预约列表页面：appointmentList.html
- 新增预约列表Controller：/appointment/list
- 用户只能查看自己创建的预约

#### 3.7.3 交易监控功能

管理员可以查看所有用户的交易记录：

- 新增获取所有主账户交易的方法：findAllPrimaryTransactions()
- 新增获取所有储蓄账户交易的方法：findAllSavingsTransactions()

#### 3.7.4 收款人隔离

为保护用户隐私，收款人数据按用户隔离：

- 收款人查询按用户ID过滤
- 收款人删除按用户ID过滤
- 用户只能操作自己的收款人

#### 3.7.5 测试数据完善

测试用户生成时自动添加：

- 每个测试用户的主账户和储蓄账户存入5000元初始余额
- 为测试用户互相添加收款人，方便转账测试

#### 3.7.6 货币符号本地化

将系统货币符号从美元($)改为人民币(¥)：

- userFront.html
- primaryAccount.html
- savingsAccount.html
- deposit.html
- withdraw.html
- betweenAccounts.html
- toSomeoneElse.html

---

## 四、页面设计

### 4.1 页面清单

| 页面 | 路径 | 功能 |
|------|------|------|
| 登录页 | /index | 用户登录 |
| 注册页 | /signup | 用户注册 |
| 首页 | /userFront | 显示账户余额、快捷操作 |
| 存款页 | /account/deposit | 存款操作 |
| 取款页 | /account/withdraw | 取款操作 |
| 主账户页 | /account/primaryAccount | 主账户详情和交易记录 |
| 储蓄账户页 | /account/savingsAccount | 储蓄账户详情和交易记录 |
| 账户间转账 | /transfer/betweenAccounts | 账户间转账 |
| 向他人转账 | /transfer/toSomeoneElse | 向收款人转账 |
| 收款人管理 | /transfer/recipient | 收款人列表和管理 |
| 预约页 | /appointment/create | 创建预约 |
| 个人资料 | /user/profile | 查看和修改个人信息 |

### 4.2 导航结构

![image-20260223184300600](C:\Users\zhouxin\AppData\Roaming\Typora\typora-user-images\image-20260223184300600.png)

![image-20260223184759091](C:\Users\zhouxin\AppData\Roaming\Typora\typora-user-images\image-20260223184759091.png)

---

## 五、API 设计

### 5.1 REST API (管理员接口)

#### 5.1.1 用户管理 API (UserResource)

| 方法 | 路径 | 角色 | 说明 | 返回类型 |
|------|------|------|------|----------|
| GET | /api/user/all | ADMIN | 获取所有用户列表 | List<User> |
| GET | /api/user/primary/transaction?username=xxx | ADMIN | 获取用户主账户交易 | List<PrimaryTransaction> |
| GET | /api/user/savings/transaction?username=xxx | ADMIN | 获取用户储蓄账户交易 | List<SavingsTransaction> |
| GET | /api/user/{username}/enable | ADMIN | 启用用户 | void |
| GET | /api/user/{username}/disable | ADMIN | 禁用用户 | void |

#### 5.1.2 预约管理 API (AppointmentResource)

| 方法 | 路径 | 角色 | 说明 | 返回类型 |
|------|------|------|------|----------|
| GET | /api/appointment/all | ADMIN | 获取所有预约列表 | List<Appointment> |
| GET | /api/appointment/{id}/confirm | ADMIN | 确认预约 | void |

### 5.2 Web 端点 API (Controller层)

#### 5.2.1 首页控制器 (HomeController)

| 方法 | 路径 | 角色 | 说明 |
|------|------|------|------|
| GET | / | 公开 | 首页，重定向到登录页 |
| GET | /index | 公开 | 登录页面 |
| GET | /signup | 公开 | 注册页面 |
| POST | /signup | 公开 | 用户注册 |
| GET | /userFront | USER | 用户首页，显示账户余额 |

#### 5.2.2 用户控制器 (UserController)

| 方法 | 路径 | 角色 | 说明 |
|------|------|------|------|
| GET | /user/profile | USER | 个人资料页面 |
| POST | /user/profile | USER | 更新个人资料 |

#### 5.2.3 账户控制器 (AccountController)

| 方法 | 路径 | 角色 | 说明 |
|------|------|------|------|
| GET | /account/primaryAccount | USER | 主账户页面 |
| GET | /account/savingsAccount | USER | 储蓄账户页面 |
| GET | /account/deposit | USER | 存款页面 |
| POST | /account/deposit | USER | 执行存款 |
| GET | /account/withdraw | USER | 取款页面 |
| POST | /account/withdraw | USER | 执行取款 |

#### 5.2.4 转账控制器 (TransferController)

| 方法 | 路径 | 角色 | 说明 |
|------|------|------|------|
| GET | /transfer/betweenAccounts | USER | 账户间转账页面 |
| POST | /transfer/betweenAccounts | USER | 执行账户间转账 |
| GET | /transfer/recipient | USER | 收款人管理页面 |
| POST | /transfer/recipient/save | USER | 保存收款人 |
| GET | /transfer/recipient/edit | USER | 编辑收款人页面 |
| GET | /transfer/recipient/delete | USER | 删除收款人 |
| GET | /transfer/toSomeoneElse | USER | 向他人转账页面 |
| POST | /transfer/toSomeoneElse | USER | 执行向他人转账 |

#### 5.2.5 预约控制器 (AppointmentController)

| 方法 | 路径 | 角色 | 说明 |
|------|------|------|------|
| GET | /appointment/create | USER | 创建预约页面 |
| POST | /appointment/create | USER | 创建预约 |

### 5.3 API 详细说明

#### 5.3.1 用户管理 API

**获取所有用户列表**
```
GET /api/user/all
Authorization: Basic Auth (ADMIN)
Response: [
  {
    "userId": 1,
    "username": "admin",
    "firstName": "Admin",
    "lastName": "User",
    "email": "admin@bank.com",
    "phone": "1234567890",
    "enabled": true
  }
]
```

**启用/禁用用户**
```
GET /api/user/{username}/enable
GET /api/user/{username}/disable
Authorization: Basic Auth (ADMIN)
Response: void (200 OK)
```

**获取用户交易记录**
```
GET /api/user/primary/transaction?username=xxx
GET /api/user/savings/transaction?username=xxx
Authorization: Basic Auth (ADMIN)
Response: [
  {
    "id": 1,
    "date": "2026-02-23T10:00:00",
    "description": "存入主账户",
    "type": "Account",
    "status": "Finished",
    "amount": 1000.0,
    "availableBalance": 1000.00
  }
]
```

#### 5.3.2 预约管理 API

**获取所有预约**
```
GET /api/appointment/all
Authorization: Basic Auth (ADMIN)
Response: [
  {
    "id": 1,
    "date": "2026-03-01T14:00:00",
    "location": "北京分行",
    "description": "办理信用卡",
    "confirmed": false,
    "user": { ... }
  }
]
```

**确认预约**
```
GET /api/appointment/{id}/confirm
Authorization: Basic Auth (ADMIN)
Response: void (200 OK)
```

### 5.4 认证说明

| API 类型 | 认证方式 | 说明 |
|----------|----------|------|
| /api/* | HTTP Basic Auth | 需要管理员角色 |
| /userFront | Session | 登录后自动创建Session |
| /account/* | Session | 需要登录 |
| /transfer/* | Session | 需要登录 |
| /appointment/* | Session | 需要登录 |

### 5.5 错误响应格式

```json
{
  "timestamp": "2026-02-23T10:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "主账户余额不足",
  "path": "/account/withdraw"
}
```

---

## 六、安全设计

### 6.1 密码安全

- 使用 BCrypt 加密存储密码
- 加密强度：12
- 密码在传输过程中使用 HTTPS

### 6.2 会话管理

- 支持"记住我"功能
- Spring Security 默认会话管理
- 登出时清除会话

### 6.3 异常处理

- 全局异常处理器统一处理运行时异常
- 友好的错误提示信息
- 详细日志记录

---

## 七、初始化数据

### 7.1 角色初始化

系统启动时自动创建以下角色：
- ROLE_USER (普通用户)
- ROLE_ADMIN (管理员)

### 7.2 默认管理员

| 字段 | 值 |
|------|-----|
| 用户名 | admin |
| 密码 | admin |
| 邮箱 | admin@bank.com |
| 电话 | 1234567890 |
| 角色 | ROLE_USER, ROLE_ADMIN |
| 主账户 | 自动创建 |
| 储蓄账户 | 自动创建 |

---

## 八、技术选型说明

### 8.1 后端技术

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 2.0.0.M7 | 快速开发框架 |
| Spring Security | 2.0.x | 安全认证 |
| Spring Data JPA | - | ORM 持久化 |
| Thymeleaf | - | 模板引擎 |
| H2 Database | - | 内存数据库(开发) |

### 8.2 前端技术

| 技术 | 用途 |
|------|------|
| Bootstrap 3.x | UI 框架 |
| jQuery | JavaScript 库 |
| DataTables | 表格插件 |
| Font Awesome | 图标库 |

---

## 九、修改记录

| 日期 | 版本 | 修改内容 | 修改人 |
|------|------|----------|--------|
| 2026-02-23 | v1.0 | 初始版本 | - |
| 2026-02-25 | v1.1 | 添加交易金额正负号显示、按中国习惯设置颜色、管理员账户保护 | - |

---

## 附录

### A. 项目文件统计

| 类型 | 数量 |
|------|------|
| Java 源文件 | 39 |
| HTML 模板 | 13 |
| 配置文件 | 2 |

### B. 编译和运行

```bash
# 编译项目
./mvnw compile

# 运行项目
./mvnw spring-boot:run

# 访问地址
http://localhost:8080
```

### C. 默认账号

| 类型 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin |
| 普通用户 | (自行注册) | - |
