package com.userfront.testutil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 测试配置工具类
 * 负责读取config.properties配置文件，提供统一的配置读取接口
 * 使用单例模式，在类加载时自动读取配置文件
 */
public class TestConfig {

    /**
     * Properties对象，用于存储配置文件中的键值对
     */
    private static Properties prop;

    /**
     * 静态代码块
     * 在类加载时执行一次，自动读取config.properties配置文件
     */
    static {
        prop = new Properties();
        try {
            // 读取配置文件
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            prop.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取配置文件中的指定属性值
     * 
     * @param key 属性键名
     * @return 属性值，如果不存在则返回null
     */
    public static String getProperty(String key) {
        return prop.getProperty(key);
    }

    /**
     * 获取被测试应用的Base URL
     * 
     * @return 应用的基础URL，如 http://localhost:8080
     */
    public static String getBaseUrl() {
        return getProperty("base.url");
    }

    /**
     * 获取浏览器类型配置
     * 
     * @return 浏览器类型（如chrome、firefox等）
     */
    public static String getBrowser() {
        return getProperty("browser");
    }

    /**
     * 获取测试用户1的用户名
     * 
     * @return user1的用户名
     */
    public static String getTestUser1Username() {
        return getProperty("test.user1.username");
    }

    /**
     * 获取测试用户1的密码
     * 
     * @return user1的密码
     */
    public static String getTestUser1Password() {
        return getProperty("test.user1.password");
    }

    /**
     * 获取测试用户1的邮箱
     * 
     * @return user1的邮箱地址
     */
    public static String getTestUser1Email() {
        return getProperty("test.user1.email");
    }

    /**
     * 获取测试用户2的用户名
     * 
     * @return user2的用户名
     */
    public static String getTestUser2Username() {
        return getProperty("test.user2.username");
    }

    /**
     * 获取测试用户2的邮箱
     * 
     * @return user2的邮箱地址
     */
    public static String getTestUser2Email() {
        return getProperty("test.user2.email");
    }

    /**
     * 获取测试用户2的密码
     * 
     * @return user2的密码
     */
    public static String getTestUser2Password() {
        return getProperty("test.user2.password");
    }

    /**
     * 获取测试用户3的用户名
     * 
     * @return user3的用户名（用于测试禁用账号登录）
     */
    public static String getTestUser3Username() {
        return getProperty("test.user3.username");
    }

    /**
     * 获取测试用户3的邮箱
     * 
     * @return user3的邮箱地址
     */
    public static String getTestUser3Email() {
        return getProperty("test.user3.email");
    }

    /**
     * 获取测试用户3的密码
     * 
     * @return user3的密码
     */
    public static String getTestUser3Password() {
        return getProperty("test.user3.password");
    }

    /**
     * 获取管理员用户名
     * 
     * @return admin的管理员用户名
     */
    public static String getAdminUsername() {
        return getProperty("admin.username");
    }

    /**
     * 获取管理员密码
     * 
     * @return admin的管理员密码
     */
    public static String getAdminPassword() {
        return getProperty("admin.password");
    }
}
