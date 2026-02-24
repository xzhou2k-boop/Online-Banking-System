package com.userfront.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userfront.config.TestDataGenerator;

@RestController
@RequestMapping("/api/tool")
public class ToolResource {

    private static final Logger LOG = LoggerFactory.getLogger(ToolResource.class);

    @Autowired
    private TestDataGenerator testDataGenerator;

    @RequestMapping("/generate-test-users")
    @PreAuthorize("hasRole('ADMIN')")
    public String generateTestUsers() {
        try {
            testDataGenerator.generateTestUsers();
            return "测试用户生成成功！";
        } catch (Exception e) {
            LOG.error("生成测试用户失败", e);
            return "生成失败: " + e.getMessage();
        }
    }
}
