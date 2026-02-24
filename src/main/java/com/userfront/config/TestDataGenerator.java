package com.userfront.config;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.userfront.dao.RoleDao;
import com.userfront.dao.UserDao;
import com.userfront.domain.User;
import com.userfront.domain.security.Role;
import com.userfront.domain.security.UserRole;
import com.userfront.service.AccountService;

@Component
public class TestDataGenerator implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TestDataGenerator.class);

    private final RoleDao roleDao;
    private final UserDao userDao;
    private final AccountService accountService;
    private final BCryptPasswordEncoder passwordEncoder;

    public TestDataGenerator(RoleDao roleDao, UserDao userDao, AccountService accountService, BCryptPasswordEncoder passwordEncoder) {
        this.roleDao = roleDao;
        this.userDao = userDao;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && "init-test-data".equals(args[0])) {
            LOG.info("开始生成测试用户数据...");
            generateTestUsers();
            LOG.info("测试用户数据生成完成！");
        }
    }

    public void generateTestUsers() {
        Role userRole = roleDao.findByName("ROLE_USER");
        
        if (userRole == null) {
            LOG.warn("角色 ROLE_USER 不存在，请先运行系统初始化");
            return;
        }

        createTestUser("user1", "password1", "张", "三", "user1@bank.com", "13800138001", userRole);
        createTestUser("user2", "password2", "李", "四", "user2@bank.com", "13800138002", userRole);
        createTestUser("user3", "password3", "王", "五", "user3@bank.com", "13800138003", userRole);
        
        LOG.info("成功创建3个测试用户");
    }

    private void createTestUser(String username, String password, String firstName, String lastName, String email, String phone, Role role) {
        if (userDao.findByUsername(username) != null) {
            LOG.info("用户 {} 已存在，跳过", username);
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setEnabled(true);

        user.setPrimaryAccount(accountService.createPrimaryAccount());
        user.setSavingsAccount(accountService.createSavingsAccount());

        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, role));
        user.setUserRoles(userRoles);

        userDao.save(user);
        LOG.info("创建用户: {} (密码: {})", username, password);
    }
}
