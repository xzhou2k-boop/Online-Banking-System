package com.userfront.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.userfront.dao.RoleDao;
import com.userfront.dao.UserDao;
import com.userfront.domain.User;
import com.userfront.domain.security.Role;
import com.userfront.domain.security.UserRole;
import com.userfront.service.AccountService;

import java.util.HashSet;
import java.util.Set;

@Component
public class InitialDataLoader implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(InitialDataLoader.class);

    private final RoleDao roleDao;
    private final UserDao userDao;
    private final AccountService accountService;
    private final BCryptPasswordEncoder passwordEncoder;

    public InitialDataLoader(RoleDao roleDao, UserDao userDao, AccountService accountService, BCryptPasswordEncoder passwordEncoder) {
        this.roleDao = roleDao;
        this.userDao = userDao;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        LOG.info("Initializing roles and admin user...");
        
        if (roleDao.findByName("ROLE_USER") == null) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleDao.save(userRole);
            LOG.info("Created ROLE_USER");
        }

        if (roleDao.findByName("ROLE_ADMIN") == null) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleDao.save(adminRole);
            LOG.info("Created ROLE_ADMIN");
        }

        if (userDao.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@bank.com");
            admin.setPhone("1234567890");
            admin.setEnabled(true);

            admin.setPrimaryAccount(accountService.createPrimaryAccount());
            admin.setSavingsAccount(accountService.createSavingsAccount());

            Set<UserRole> userRoles = new HashSet<>();
            userRoles.add(new UserRole(admin, roleDao.findByName("ROLE_USER")));
            userRoles.add(new UserRole(admin, roleDao.findByName("ROLE_ADMIN")));
            admin.setUserRoles(userRoles);

            userDao.save(admin);
            LOG.info("Created admin user (username: admin, password: admin)");
        }

        LOG.info("Data initialization completed.");
    }
}
