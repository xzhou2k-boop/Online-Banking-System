package com.userfront.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.Model;

import com.userfront.domain.Appointment;
import com.userfront.domain.User;
import com.userfront.service.AppointmentService;
import com.userfront.service.UserService;

/**
 * AppointmentController单元测试类
 *
 * 测试目标：AppointmentController 预约控制器
 *
 * 测试策略：
 * 1. 使用Mockito模拟Service层依赖
 * 2. 重点测试预约创建和列表功能
 * 3. 验证请求参数验证和页面跳转
 *
 * 覆盖的测试场景：
 * - 预约创建页面（GET）
 * - 提交预约（日期为空、地点为空、成功）
 * - 预约列表查询
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @InjectMocks
    private AppointmentController appointmentController;

    private Appointment testAppointment;
    private User testUser;

    /**
     * 初始化测试数据
     * 在每个测试方法执行前运行
     */
    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        // 创建测试预约
        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setDate(new Date());
        testAppointment.setLocation("Bank Branch A");
        testAppointment.setDescription("Account opening");
        testAppointment.setUser(testUser);
    }

    /**
     * 测试用例：创建预约页面（GET请求）
     * Given: Model对象
     * When: 调用createAppointment方法
     * Then: 返回appointment视图，model包含空的appointment和dateString
     */
    @Test
    @DisplayName("测试createAppointment - GET请求显示创建页面")
    void testCreateAppointment_Get() {
        // Given
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        // When
        String result = appointmentController.createAppointment(model);

        // Then
        assertEquals("appointment", result);
        verify(model, times(2)).addAttribute(anyString(), any());
    }

    /**
     * 测试用例：提交预约 - 日期为空
     * Given: dateString为空字符串
     * When: 调用createAppointmentPost方法
     * Then: 抛出IllegalArgumentException异常
     */
    @Test
    @DisplayName("测试createAppointmentPost - 日期为空")
    void testCreateAppointmentPost_DateEmpty() {
        // Given
        Appointment appointment = new Appointment();
        String dateString = "";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> appointmentController.createAppointmentPost(appointment, dateString, model, principal)
        );
        assertEquals("请选择日期和时间", exception.getMessage());
    }

    /**
     * 测试用例：提交预约 - 地点为空
     * Given: 有效的日期，但location为null
     * When: 调用createAppointmentPost方法
     * Then: 抛出IllegalArgumentException异常
     */
    @Test
    @DisplayName("测试createAppointmentPost - 地点为空")
    void testCreateAppointmentPost_LocationEmpty() {
        // Given
        Appointment appointment = new Appointment();
        appointment.setLocation(null);
        String dateString = "2024-01-15 10:00";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> appointmentController.createAppointmentPost(appointment, dateString, model, principal)
        );
        assertEquals("请选择办理地点", exception.getMessage());
    }

    /**
     * 测试用例：提交预约 - 日期格式错误
     * Given: 无效的日期格式
     * When: 调用createAppointmentPost方法
     * Then: 抛出ParseException异常
     */
    @Test
    @DisplayName("测试createAppointmentPost - 日期格式错误")
    void testCreateAppointmentPost_DateFormatError() throws Exception {
        // Given
        Appointment appointment = new Appointment();
        appointment.setLocation("Bank Branch A");
        String dateString = "invalid-date";

        // When & Then
        assertThrows(
            Exception.class,
            () -> appointmentController.createAppointmentPost(appointment, dateString, model, principal)
        );
    }

    /**
     * 测试用例：提交预约 - 成功
     * Given: 有效的日期和地点
     * When: 调用createAppointmentPost方法
     * Then: 预约保存成功，重定向到用户首页
     */
    @Test
    @DisplayName("测试createAppointmentPost - 提交成功")
    void testCreateAppointmentPost_Success() throws Exception {
        // Given
        Appointment appointment = new Appointment();
        appointment.setLocation("Bank Branch A");
        String dateString = "2024-01-15 10:00";

        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(appointmentService.createAppointment(any(Appointment.class))).thenReturn(appointment);

        // When
        String result = appointmentController.createAppointmentPost(appointment, dateString, model, principal);

        // Then
        assertEquals("redirect:/userFront", result);
        verify(userService, times(1)).findByUsername("testuser");
        verify(appointmentService, times(1)).createAppointment(any(Appointment.class));
    }

    /**
     * 测试用例：预约列表查询
     * Given: 登录用户
     * When: 调用appointmentList方法
     * Then: 返回appointmentList视图，model包含预约列表
     */
    @Test
    @DisplayName("测试appointmentList - 查询预约列表")
    void testAppointmentList_Success() {
        // Given
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(principal.getName()).thenReturn("testuser");
        when(appointmentService.findByUsername("testuser")).thenReturn(appointments);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        // When
        String result = appointmentController.appointmentList(model, principal);

        // Then
        assertEquals("appointmentList", result);
        verify(principal, times(1)).getName();
        verify(appointmentService, times(1)).findByUsername("testuser");
        verify(model, times(1)).addAttribute("appointmentList", appointments);
    }

    /**
     * 测试用例：预约列表查询 - 无预约
     * Given: 没有预约的用户
     * When: 调用appointmentList方法
     * Then: 返回空列表
     */
    @Test
    @DisplayName("测试appointmentList - 无预约")
    void testAppointmentList_Empty() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(appointmentService.findByUsername("testuser")).thenReturn(Arrays.asList());
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        // When
        String result = appointmentController.appointmentList(model, principal);

        // Then
        assertEquals("appointmentList", result);
        verify(appointmentService, times(1)).findByUsername("testuser");
    }

    /**
     * 测试用例：创建预约页面 - Model验证
     * Given: Model对象
     * When: 调用createAppointment方法
     * Then: 验证addAttribute调用参数
     */
    @Test
    @DisplayName("测试createAppointment - Model属性验证")
    void testCreateAppointment_ModelAttributes() {
        // Given
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        // When
        appointmentController.createAppointment(model);

        // Then
        verify(model).addAttribute(eq("appointment"), any(Appointment.class));
        verify(model).addAttribute(eq("dateString"), eq(""));
    }
}