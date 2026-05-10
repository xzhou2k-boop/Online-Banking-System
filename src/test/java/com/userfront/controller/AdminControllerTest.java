package com.userfront.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.*;

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
import com.userfront.domain.PrimaryTransaction;
import com.userfront.domain.SavingsTransaction;
import com.userfront.domain.User;
import com.userfront.service.AppointmentService;
import com.userfront.service.TransactionService;
import com.userfront.service.UserService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AdminControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private Model model;

    @InjectMocks
    private AdminController adminController;

    private User testUser;
    private Appointment testAppointment;
    private PrimaryTransaction primaryTransaction;
    private SavingsTransaction savingsTransaction;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setEnabled(true);

        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setDate(new Date());
        testAppointment.setLocation("Bank Branch A");
        testAppointment.setConfirmed(false);

        primaryTransaction = new PrimaryTransaction();
        primaryTransaction.setId(1L);
        primaryTransaction.setAmount(100.0);

        savingsTransaction = new SavingsTransaction();
        savingsTransaction.setId(2L);
        savingsTransaction.setAmount(200.0);
    }

    @Test
    @DisplayName("测试adminHome - 跳转用户列表")
    void testAdminHome() {
        String result = adminController.adminHome();
        assertEquals("redirect:/admin/users", result);
    }

    @Test
    @DisplayName("测试userList - 获取用户列表")
    void testUserList() {
        List<User> users = Arrays.asList(testUser);
        when(userService.findUserList()).thenReturn(users);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String result = adminController.userList(model);

        assertEquals("admin/users", result);
        verify(userService, times(1)).findUserList();
        verify(model, times(1)).addAttribute("userList", users);
    }

    @Test
    @DisplayName("测试userList - 用户列表为空")
    void testUserList_Empty() {
        when(userService.findUserList()).thenReturn(Arrays.asList());
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String result = adminController.userList(model);

        assertEquals("admin/users", result);
        verify(userService, times(1)).findUserList();
    }

    @Test
    @DisplayName("测试enableUser - 启用用户")
    void testEnableUser() {
        doNothing().when(userService).enableUser("testuser");

        String result = adminController.enableUser("testuser");

        assertEquals("redirect:/admin/users", result);
        verify(userService, times(1)).enableUser("testuser");
    }

    @Test
    @DisplayName("测试disableUser - 禁用用户")
    void testDisableUser() {
        doNothing().when(userService).disableUser("testuser");

        String result = adminController.disableUser("testuser");

        assertEquals("redirect:/admin/users", result);
        verify(userService, times(1)).disableUser("testuser");
    }

    @Test
    @DisplayName("测试appointmentList - 获取预约列表")
    void testAppointmentList() {
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentService.findAll()).thenReturn(appointments);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String result = adminController.appointmentList(model);

        assertEquals("admin/appointments", result);
        verify(appointmentService, times(1)).findAll();
        verify(model, times(1)).addAttribute("appointmentList", appointments);
    }

    @Test
    @DisplayName("测试confirmAppointment - 确认预约")
    void testConfirmAppointment() {
        doNothing().when(appointmentService).confirmAppointment(1L);

        String result = adminController.confirmAppointment(1L);

        assertEquals("redirect:/admin/appointments", result);
        verify(appointmentService, times(1)).confirmAppointment(1L);
    }

    @Test
    @DisplayName("测试transactionList - 获取交易列表")
    void testTransactionList() {
        List<PrimaryTransaction> primaryList = Arrays.asList(primaryTransaction);
        List<SavingsTransaction> savingsList = Arrays.asList(savingsTransaction);
        when(transactionService.findAllPrimaryTransactions()).thenReturn(primaryList);
        when(transactionService.findAllSavingsTransactions()).thenReturn(savingsList);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String result = adminController.transactionList(model);

        assertEquals("admin/transactions", result);
        verify(transactionService, times(1)).findAllPrimaryTransactions();
        verify(transactionService, times(1)).findAllSavingsTransactions();
        verify(model, times(2)).addAttribute(anyString(), any());
    }

    @Test
    @DisplayName("测试transactionList - 交易列表为空")
    void testTransactionList_Empty() {
        when(transactionService.findAllPrimaryTransactions()).thenReturn(Arrays.asList());
        when(transactionService.findAllSavingsTransactions()).thenReturn(Arrays.asList());
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String result = adminController.transactionList(model);

        assertEquals("admin/transactions", result);
    }
}