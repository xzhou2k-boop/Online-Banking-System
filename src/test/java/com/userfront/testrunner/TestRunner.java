package com.userfront.testrunner;

import org.testng.TestNG;
import org.testng.xml.XmlSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试运行器类
 * 用于以编程方式运行TestNG测试套件
 * 可替代命令行方式运行测试
 * 
 * 使用方式：
 * 1. 直接运行该类的main方法
 * 2. 或者通过IDE运行该类
 */
public class TestRunner {

    /**
     * 主方法，程序入口
     * 负责初始化TestNG并运行测试套件
     * @param args 命令行参数（暂未使用）
     */
    public static void main(String[] args) {
        // 创建TestNG实例
        TestNG testng = new TestNG();
        
        // 创建测试套件列表
        List<String> suites = new ArrayList<>();
        // 添加TestNG配置文件路径
        suites.add("src/test/resources/testng.xml");
        
        // 设置测试套件
        testng.setTestSuites(suites);
        // 设置并行模式为按类并行（同一个类的测试方法将并行执行）
        testng.setParallel(XmlSuite.ParallelMode.CLASSES);
        // 设置线程数为1（避免并发冲突）
        testng.setThreadCount(1);
        // 使用默认监听器（生成报告等）
        testng.setUseDefaultListeners(true);
        
        // 运行测试
        testng.run();
    }
}
