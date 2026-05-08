package com.userfront.config;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.userfront.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.userfront.dao.PrimaryAccountDao;
import com.userfront.dao.RecipientDao;
import com.userfront.dao.RoleDao;
import com.userfront.dao.SavingsAccountDao;
import com.userfront.dao.UserDao;
import com.userfront.domain.security.Role;
import com.userfront.domain.security.UserRole;
import com.userfront.service.AccountService;

@Component
public class TestDataGenerator implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TestDataGenerator.class);

    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("5000");

    private final RoleDao roleDao;
    private final UserDao userDao;
    private final PrimaryAccountDao primaryAccountDao;
    private final SavingsAccountDao savingsAccountDao;
    private final AccountService accountService;
    private final RecipientDao recipientDao;
    private final BCryptPasswordEncoder passwordEncoder;

    public TestDataGenerator(RoleDao roleDao, UserDao userDao, PrimaryAccountDao primaryAccountDao,
            SavingsAccountDao savingsAccountDao, AccountService accountService,
            RecipientDao recipientDao, BCryptPasswordEncoder passwordEncoder) {
        this.roleDao = roleDao;
        this.userDao = userDao;
        this.primaryAccountDao = primaryAccountDao;
        this.savingsAccountDao = savingsAccountDao;
        this.accountService = accountService;
        this.recipientDao = recipientDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && "init-test-data".equals(args[0])) {
            LOG.info("Start creating test user data...");
            generateTestUsers();
            LOG.info("Test user data generation finished!");
        }
    }

    public void generateTestUsers() {
        Role userRole = roleDao.findByName("ROLE_USER");

        if (userRole == null) {
            LOG.warn("Role ROLE_USER does not exist. Please run system initialization first.");
            return;
        }

        createTestUser("user1", "password1", "张", "三", "user1@bank.com", "13800138001", userRole, true);
        createTestUser("user2", "password2", "李", "四", "user2@bank.com", "13800138002", userRole, true);
        createTestUser("user3", "password3", "王", "五", "user3@bank.com", "13800138003", userRole, false);

        LOG.info("Successfully created 3 test users");

        addInitialBalance();
        LOG.info("Successfully deposited 5,000 yuan to each account");

        addRecipients();
        LOG.info("Successfully added recipient details");
    }

    private void addInitialBalance() {
        User user1 = userDao.findByUsername("user1");
        User user2 = userDao.findByUsername("user2");
        User user3 = userDao.findByUsername("user3");

        if (user1 != null) {
            PrimaryAccount p1 = user1.getPrimaryAccount();
            SavingsAccount s1 = user1.getSavingsAccount();
            p1.setAccountBalance(INITIAL_BALANCE);
            s1.setAccountBalance(INITIAL_BALANCE);
            primaryAccountDao.save(p1);
            savingsAccountDao.save(s1);

            LOG.info("user1\'s main and savings accounts have been credited with 5,000 yuan each");
        }

        if (user2 != null) {
            PrimaryAccount p2 = user2.getPrimaryAccount();
            SavingsAccount s2 = user2.getSavingsAccount();
            p2.setAccountBalance(INITIAL_BALANCE);
            s2.setAccountBalance(INITIAL_BALANCE);
            primaryAccountDao.save(p2);
            savingsAccountDao.save(s2);
            LOG.info("user2\'s main and savings accounts have been credited with 5,000 yuan each");
        }

        if (user3 != null) {
            PrimaryAccount p3 = user3.getPrimaryAccount();
            SavingsAccount s3 = user3.getSavingsAccount();
            p3.setAccountBalance(INITIAL_BALANCE);
            s3.setAccountBalance(INITIAL_BALANCE);
            primaryAccountDao.save(p3);
            savingsAccountDao.save(s3);
            LOG.info("user3\'s main and savings accounts have been credited with 5,000 yuan each");
        }
    }

    private void addRecipients() {
        User user1 = userDao.findByUsername("user1");
        User user2 = userDao.findByUsername("user2");
        User user3 = userDao.findByUsername("user3");

        if (user1 != null && user2 != null) {
            addRecipient(user1, "user2", "李四", "user2@bank.com", "13800138002", "user2");
        }

        if (user1 != null && user3 != null) {
            addRecipient(user1, "user3", "王五", "user3@bank.com", "13800138003", "user3");
        }

        if (user2 != null && user1 != null) {
            addRecipient(user2, "user1", "张三", "user1@bank.com", "13800138001", "user1");
        }

        if (user2 != null && user3 != null) {
            addRecipient(user2, "user3", "王五", "user3@bank.com", "13800138003", "user3");
        }

        if (user3 != null && user1 != null) {
            addRecipient(user3, "user1", "张三", "user1@bank.com", "13800138001", "user1");
        }

        if (user3 != null && user2 != null) {
            addRecipient(user3, "user2", "李四", "user2@bank.com", "13800138002", "user2");
        }
    }

    private void addRecipient(User owner, String recipientName, String recipientFullName, String email, String phone,
            String accountUsername) {
        Recipient recipient = new Recipient();
        recipient.setName(recipientFullName);
        recipient.setEmail(email);
        recipient.setPhone(phone);
        recipient.setAccountNumber(accountUsername);
        recipient.setDescription(recipientName + " - " + recipientFullName);
        recipient.setUser(owner);
        recipientDao.save(recipient);
        LOG.info(" {} add recipient: {}", owner.getUsername(), accountUsername);
    }

    private void createTestUser(String username, String password, String firstName, String lastName, String email,
            String phone, Role role, boolean isEnabled) {
        if (userDao.findByUsername(username) != null) {
            LOG.info("User {} is exist，skip", username);
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setEnabled(isEnabled);

        user.setPrimaryAccount(accountService.createPrimaryAccount());
        user.setSavingsAccount(accountService.createSavingsAccount());

        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, role));
        user.setUserRoles(userRoles);

        userDao.save(user);
        LOG.info("Create User: {} (Password: {})", username, password);
    }
}
