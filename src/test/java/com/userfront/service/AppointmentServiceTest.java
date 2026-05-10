package com.userfront.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.userfront.dao.AppointmentDao;
import com.userfront.domain.Appointment;
import com.userfront.domain.User;
import com.userfront.service.UserServiceImpl.AppointmentServiceImpl;

/**
 * AppointmentService单元测试类
 *
 * 测试目标：AppointmentServiceImpl 预约服务层
 *
 * 测试策略：
 * 1. 使用Mockito模拟DAO层和UserService依赖
 * 2. 重点测试预约的创建、查询、确认功能
 * 3. 验证数据持久化的正确性
 *
 * 覆盖的测试场景：
 * - 创建新预约
 * - 查询所有预约
 * - 根据用户名查询预约
 * - 根据ID查询预约
 * - 确认预约
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AppointmentServiceTest {

    @Mock
    private AppointmentDao appointmentDao;

    @Mock
    private UserService userService;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

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
        testAppointment.setConfirmed(false);
    }

    /**
     * 测试用例：创建预约成功
     * Given: 有效的预约对象
     * When: 调用createAppointment方法
     * Then: 返回保存后的预约对象，调用DAO的save方法一次
     */
    @Test
    @DisplayName("测试createAppointment - 创建预约成功")
    void testCreateAppointment_Success() {
        // Given
        when(appointmentDao.save(any(Appointment.class))).thenReturn(testAppointment);

        // When
        Appointment result = appointmentService.createAppointment(testAppointment);

        // Then
        assertNotNull(result);
        assertEquals(testAppointment.getId(), result.getId());
        assertEquals(testAppointment.getLocation(), result.getLocation());
        verify(appointmentDao, times(1)).save(testAppointment);
    }

    /**
     * 测试用例：查询所有预约
     * Given: 数据库中存在多个预约
     * When: 调用findAll方法
     * Then: 返回所有预约列表
     */
    @Test
    @DisplayName("测试findAll - 查询所有预约")
    void testFindAll_Success() {
        // Given
        Appointment appointment2 = new Appointment();
        appointment2.setId(2L);
        appointment2.setLocation("Bank Branch B");

        List<Appointment> appointments = Arrays.asList(testAppointment, appointment2);
        when(appointmentDao.findAll()).thenReturn(appointments);

        // When
        List<Appointment> result = appointmentService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(appointmentDao, times(1)).findAll();
    }

    /**
     * 测试用例：根据用户名查询预约
     * Given: 用户名和对应的预约列表
     * When: 调用findByUsername方法
     * Then: 返回该用户的预约列表
     */
    @Test
    @DisplayName("测试findByUsername - 根据用户名查询预约")
    void testFindByUsername_Success() {
        // Given
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(appointmentDao.findByUser(testUser)).thenReturn(appointments);

        // When
        List<Appointment> result = appointmentService.findByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUser().getUsername());
        verify(userService, times(1)).findByUsername("testuser");
        verify(appointmentDao, times(1)).findByUser(testUser);
    }

    /**
     * 测试用例：根据ID查询预约
     * Given: 预约ID
     * When: 调用findAppointment方法
     * Then: 返回对应的预约对象
     */
    @Test
    @DisplayName("测试findAppointment - 根据ID查询预约")
    void testFindAppointment_Success() {
        // Given
        when(appointmentDao.findById(1L)).thenReturn(Optional.of(testAppointment));

        // When
        Appointment result = appointmentService.findAppointment(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(appointmentDao, times(1)).findById(1L);
    }

    /**
     * 测试用例：根据ID查询预约 - 不存在
     * Given: 不存在的预约ID
     * When: 调用findAppointment方法
     * Then: 返回null
     */
    @Test
    @DisplayName("测试findAppointment - 预约不存在")
    void testFindAppointment_NotFound() {
        // Given
        when(appointmentDao.findById(999L)).thenReturn(Optional.empty());

        // When
        Appointment result = appointmentService.findAppointment(999L);

        // Then
        assertNull(result);
        verify(appointmentDao, times(1)).findById(999L);
    }

    /**
     * 测试用例：确认预约
     * Given: 未确认的预约
     * When: 调用confirmAppointment方法
     * Then: 预约被标记为已确认，保存到数据库
     */
    @Test
    @DisplayName("测试confirmAppointment - 确认预约成功")
    void testConfirmAppointment_Success() {
        // Given
        when(appointmentDao.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentDao.save(any(Appointment.class))).thenReturn(testAppointment);

        // When
        appointmentService.confirmAppointment(1L);

        // Then
        assertTrue(testAppointment.isConfirmed());
        verify(appointmentDao, times(1)).findById(1L);
        verify(appointmentDao, times(1)).save(testAppointment);
    }

    /**
     * 测试用例：创建预约 - 保存返回null
     * Given: DAO保存返回null
     * When: 调用createAppointment方法
     * Then: 返回null
     */
    @Test
    @DisplayName("测试createAppointment - 保存返回null")
    void testCreateAppointment_ReturnsNull() {
        // Given
        when(appointmentDao.save(any(Appointment.class))).thenReturn(null);

        // When
        Appointment result = appointmentService.createAppointment(testAppointment);

        // Then
        assertNull(result);
        verify(appointmentDao, times(1)).save(testAppointment);
    }

    /**
     * 测试用例：根据用户名查询预约 - 用户不存在
     * Given: 不存在的用户名
     * When: 调用findByUsername方法
     * Then: 返回空列表
     */
    @Test
    @DisplayName("测试findByUsername - 用户不存在")
    void testFindByUsername_UserNotFound() {
        // Given
        when(userService.findByUsername("nonexistent")).thenReturn(null);
        when(appointmentDao.findByUser(null)).thenReturn(Arrays.asList());

        // When
        List<Appointment> result = appointmentService.findByUsername("nonexistent");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}